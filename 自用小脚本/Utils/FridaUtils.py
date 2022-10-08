import frida, sys
from Utils.FileUtils import FileUtils
from Utils.LoggerUtils import LoggerUtils
from pprint import pformat
import js2py

class FridaUtils(object):
    device = frida.get_usb_device()
    # device = frida.get_local_device()
    '''
    description: 加载指定的Frida模块， 并指定回调函数
    param {*} filepath: js文件路径
    param {*} onCall: 回调函数
    return {*} session: 注入文件的句柄
    '''
    @staticmethod
    def loadModule(filepath, onCall, pids=[]):
        try:
            # USB 设备句柄
            sessions = []
            # 如果为空， 默认获取活跃Activity进程的pid
            if not pids :
                pids.append(FridaUtils.device.get_frontmost_application().pid)
            # 读取注入脚本
            with open(filepath) as f:
                jscode = f.read()
            # 循环注入进程，返回模块句柄
            for pid in pids:
                session = FridaUtils.device.attach(pid)
                # 加载脚本, 注册JavaScript消息处理函数
                script = session.create_script(jscode)
                script.on('message', onCall)
                script.load()
                script.exports.main()
                # 保存注入的模块句柄
                sessions.append(session)
            return sessions
        except frida.ServerNotRunningError as msg:
            LoggerUtils().get_log().error("服务器未启动")
            sys.exit()
        except Exception as msg:
            # LoggerUtils().get_log().error("设备未连接")
            LoggerUtils().getExceptionInfo(msg)
            sys.exit()

    '''
    description: 简单处理图片字节函数
    param {*} icon: 图片字节
    return {*} icon[0:16]
    '''
    @staticmethod
    def trim_icon(icon):
        result = dict(icon)
        result['image'] = result['image'][0:16] + b"..."
        return result

    '''
    description: 遍历进程
    param {*} moduleName
    return {*}
    '''
    @staticmethod
    def enumProcesses():
        processes = FridaUtils.device.enumerate_processes(scope='full')
        list_process = []
        for proc in processes:
            params = dict(proc.parameters)
            if 'icons' in params:
                params['icons'] = [FridaUtils.trim_icon(icon) for icon in params['icons']]
            list_process.append({"pid": proc.pid, "name": proc.name, "parameters": pformat(params).rstrip()})
            # LoggerUtils().get_log().debug("Process(pid={}, name=\"{}\", parameters={})".format(proc.pid, proc.name, pformat(params).rstrip()))
        return list_process

    '''
    description: 遍历应用
    param {*} moduleName
    return {*}
    '''
    @staticmethod
    def enumApplications():
        apps = FridaUtils.device.enumerate_applications(scope='full')
        list_apps = []
        for app in apps:
            params = dict(app.parameters)
            if 'icons' in params:
                params['icons'] = [FridaUtils.trim_icon(icon) for icon in params['icons']]
            list_apps.append({"identifier": app.identifier,"pid": app.pid, "name": app.name, "parameters": pformat(params).rstrip()})
            # print("Application(identifier=\"{}\", name=\"{}\", pid={}, parameters={})".format(
            #     app.identifier,
            #     app.name,
            #     app.pid,
                # 检测是否已经Hook过了
                # if(self.RUNTIME_INFO.setdefault(item, {}).setdefault("session", 0)):
                #     self.logger.error("API调用：hook， 模块重复加载")
                #     continue
                # self.RUNTIME_INFO[item] = self.MODULES_INFO[item]
            #     pformat(params).rstrip()))
        return list_apps

    @staticmethod
    def hook(moduleName):
        session = FridaUtils.loadModule(moduleName)
        return session
        
    @staticmethod
    def unhook(session):
        session.detach()
    
    @staticmethod
    def getFrontApplication():
        _front_app = FridaUtils.device.get_frontmost_application()
        info = {"identifier":_front_app.identifier,  "name":_front_app.name, "pid": _front_app.pid}
        return info

    '''
    description: 调用js文件中的变量
    return {*}
    '''
    @staticmethod
    def invokeJsVar(jsPath,var):
        # 读取js文件的内容, 去掉rpc内容
        data = FileUtils.readString(jsPath)
        data = data.split("rpc")[0]
        # 使用获取上下js2py生成一个上下文环境
        context = js2py.EvalJs()
        context.execute(data)
        result = context[var]
        return result