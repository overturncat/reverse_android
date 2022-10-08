#!/home/ayuan/miniconda3/envs/py380/bin/python
import time 
import frida
import os
import sys  
import argparse
from gooey import Gooey, GooeyParser
from Utils.CmdUtils import CmdUtils
from Utils.FileUtils import FileUtils
from Utils.FridaUtils import FridaUtils
SERVER_PATH = "/home/ayuan/.wine/drive_c/IDA/dbgsrv/android_server"

def on_message(args):
    print("##################################################")
    if(args.StartIdaServer):
        # 上传 IDA 调试服务端
        CmdUtils.runcmd("adb push {} /data/local/tmp/".format(SERVER_PATH))
        # 修改文件权限，测试运行
        CmdUtils.runcmd('adb shell "chmod +x /data/local/tmp/{}"'.format(FileUtils.get_file_name(SERVER_PATH)))
        # 带参数，修改端口运行
        CmdUtils.runcmd('adb shell "nohup /data/local/tmp/{} -p23456 1>/dev/null 2>&1 &"'.format(FileUtils.get_file_name(SERVER_PATH)))
        # 设置端口转发
        CmdUtils.runcmd("adb forward tcp:23456 tcp:23456")
    if(args.GetAndroidScreen):
        CmdUtils.runcmd("scrcpy 1>/dev/null 2>&1 &", "scrcpy启动屏幕")
    if(args.GetAndroidInfo):
        CmdUtils.runcmd('adb shell getprop ro.product.cpu.abi', "Android CPU 版本")
    if(args.GetProcessInfo):
        CmdUtils.runcmd('adb shell dumpsys window | grep mCurrentFocus', "当前应用包名")
    if(args.StartFridaServer):
        CmdUtils.runcmd("adb push {} /data/local/tmp".format(args.StartFridaServer), "上传")
        filename = FileUtils.get_file_name(args.StartFridaServer)
        CmdUtils.runcmd("adb shell 'chmod 777 /data/local/tmp/{}'".format(filename), "授权")
        CmdUtils.runcmd("adb shell 'su -c \"/data/local/tmp/{} 1>/dev/null 2>&1 &\"'".format(filename), "进入shell并运行")
        time.sleep(10)
        CmdUtils.runcmd("adb shell 'su -c \"netstat -antlp | grep frida\"'".format(filename), "确认端口启动")
    if(args.CodeFridaProject):
        CmdUtils.runcmd_cwd("proxychains git clone https://github.com/oleavr/frida-agent-example.git", args.CodeFridaProject)
        CmdUtils.runcmd_cwd("proxychains npm install 1>/dev/null 2>&1 &", FileUtils.getFilePathWithOffset(args.CodeFridaProject, "frida-agent-example"))
    _cmd_Objection = 'objection -g {} run "{}"'
    if(args.Objection_ProgressName_or_Pid):
        # 要检测的包名或者进程
        _pid_or_packagename = ""
        if(args.Objection_ProgressName_or_Pid == "0"):
            _front_app = FridaUtils.getFrontApplication()
            print("选中前台进程：\n * identifier: {} \n * name: {} \n * pid: {}".format(_front_app["identifier"], _front_app["name"], _front_app["pid"]))
            _pid_or_packagename = _front_app["pid"]
        else:
            _pid_or_packagename = args.Objection_ProgressName_or_Pid
        # 列出内存中所有的类
        if(args.list_classes):
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking list classes"))
        # 在内存中所有已加载的类中搜索包含特定关键词的类
        if(args.search_classes):
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking search classes {}".format(args.search_classes)))
        # 在内存中所有已加载的类中搜索包含特定关键词的方法
        if(args.search_methods):
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking search methods {}".format(args.search_methods)))
        # 列出特定类所有的方法
        if(args.list_class_methods):
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking list class_methods {}".format(args.list_class_methods)))
        # 直接生成代码
        if(args.generate_simple):
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking generate simple {}".format(args.generate_simple)))
        # 搜索并打印由完全限定的类名指定的特定 Java 类的实时实例。 输出是instance.toString()，该字符串通常包含对象的属性值
        if(args.search_instances):
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android heap search instances {}".format(args.search_instances)))
        # 调用函数
        if(args.invoke_instance):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android heap execute {} {}".format(args.invoke_instance[0], args.invoke_instance[1])))
        # 列出进程所有的 activity
        if(args.list_activity):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking list activities"))
        # 调用特定Activity 
        if(args.launch_activity):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android intent launch_activity {}".format(args.launch_activity)))
        # 列出进程所有的 services
        if(args.list_services):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking list services"))
        # 列出进程所有的 services
        if(args.launch_service):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android intent launch_service {}".format(args.launch_service)))
        # 列出进程所有的 receivers
        if(args.list_receivers):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking list receivers"))
        if(args.watch_class_method):
            # print(args.invoke_instance)
            CmdUtils.runcmd(_cmd_Objection.format(_pid_or_packagename, "android hooking watch class_method {} --dump-args --dump-backtrace --dump-return".format(args.watch_class_method)))
def message_help(args):
    print("##################################################")
    print("默认功能回调")


@Gooey(num_required_cols =1, num_optional_cols=1,optional_cols=1,default_size=(1280, 720), program_name="逆向工具箱",clear_before_run=True, header_show_title=False,header_show_subtitle=False,header_height=40)
def main():
    parser = GooeyParser()
    # parser.add_argument('--verbose', help='be verbose', dest='verbose', action='store_true', default=False)
    subs = parser.add_subparsers(help='commands', dest='command')

    # ########################################################
    curl_parser = subs.add_parser('Android环境准备', help='环境准备')
    location = curl_parser.add_argument_group('基本功能', gooey_options={ 'show_border': True })
    location.add_argument('--GetAndroidInfo', action="store_true", help='获取CPU型号')
    location.add_argument('--GetProcessInfo', action="store_true", help='获取当前应用包名')
    location.add_argument('--GetAndroidScreen', action="store_true", help='获取Android屏幕')

    location = curl_parser.add_argument_group('Ida功能', gooey_options={ 'show_border': True })
    location.add_argument('--StartIdaServer', action="store_true", help='上传Ida服务端')
    # location.set_defaults(func=on_message)

    location = curl_parser.add_argument_group('Frida功能', gooey_options={ 'show_border': True })
    location.add_argument('--StartFridaServer', help='上传并启动Frida服务端(留空则不操作)', type=str, widget='FileChooser')
    location.add_argument('--CodeFridaProject', help='Frida项目文件夹(留空则不操作)', type=str, widget='DirChooser')
    # location.set_defaults(func=on_message)

    # Objection功能
    location_objection = curl_parser.add_argument_group('Objection功能', gooey_options={ 'show_border': True,"columns": 2})
    location_objection.add_argument('--Objection_ProgressName_or_Pid', help='指定要注入的包名/进程号(若要使用Objection功能必填), 0代表当前应用)', type=str)
    # Objection功能.加载的类及函数信息
    sub_location_objection = location_objection.add_argument_group('加载的类及函数信息', gooey_options={ 'show_border': True })
    sub_location_objection.add_argument('--list_classes', help='列出加载的所有类',  action="store_true")
    sub_location_objection.add_argument('--search_classes', help='包含**特定关键词**的类(已加载的类中搜索)', type=str)
    sub_location_objection.add_argument('--list_class_methods', help='列出**特定类**所有的方法', type=str)
    sub_location_objection.add_argument('--search_methods', help='包含**特定关键词**的方法(已加载的类中搜索)', type=str)
    sub_location_objection.add_argument('--generate_simple', help='生成**特定类**所有方法的hook代码', type=str)

    # Objection功能.加载的类及函数信息
    sub_location_objection = location_objection.add_argument_group('类实例信息', gooey_options={ 'show_border': True })
    sub_location_objection.add_argument('--search_instances', help='搜索**指定Java类**的实时实例。 输出是instance.toString()', type=str)
    sub_location_objection.add_argument('--invoke_instance', help='调用**指定实例**的**指定方法**(只能在交互模式中使用)\n(输入两个参数如 0x2526 getPreferenceScreenResId)', type=str,nargs=2)

    # Objection功能.四大组件信息
    sub_location_objection = location_objection.add_argument_group('四大组件信息', gooey_options={ 'show_border': True})
    sub_location_objection.add_argument('--list_activity', help='列出进程所有的 activity', action="store_true")
    sub_location_objection.add_argument('--launch_activity', help='调用特定Activity', type=str)
    sub_location_objection.add_argument('--list_services', help='列出进程所有的 services', action="store_true")
    sub_location_objection.add_argument('--launch_service', help='调用特定Activity', type=str)
    sub_location_objection.add_argument('--list_receivers', help='列出进程所有的 receivers', action="store_true")

    # Objection功能.四大组件信息
    sub_location_objection = location_objection.add_argument_group('Hook相关命令', gooey_options={ 'show_border': True })
    sub_location_objection.add_argument('--watch_class_method', help='对指定的方法进行Hook, 如java.io.File.$init', type=str)
    # sub_location_objection.add_argument('--launch_activity', help='调用特定Activity', type=str)
    # sub_location_objection.add_argument('--list_services', help='列出进程所有的 services', action="store_true")
    # sub_location_objection.add_argument('--launch_service', help='调用特定Activity', type=str)
    # sub_location_objection.add_argument('--list_receivers', help='列出进程所有的 receivers', action="store_true")

    curl_parser.set_defaults(func=on_message)
    # ########################################################
    siege_parser = subs.add_parser(
        'siege', help='Siege is an http/https regression testing and benchmarking utility')
    siege_parser.add_argument('--get', help='Pull down headers from the server and display HTTP transaction', type=str)
    siege_parser.add_argument('--concurrent', help='Stress the web server with NUM number of simulated users', type=int)
    siege_parser.add_argument('--time', help='allows you to run the test for a selected period of time', type=int)
    siege_parser.add_argument('--delay', help='simulated user is delayed for a random number of seconds between one and NUM', type=int)
    siege_parser.add_argument('--message', help='mark the log file with a separator', type=int)
    siege_parser.set_defaults(func=message_help)

    # # # ########################################################
    # 参考： https://github.com/chriskiehl/GooeyExamples
    # curl_parser = subs.add_parser( 'curl', help='curl is a tool to transfer data from or to a server')
    # curl_parser.add_argument('Path', help='URL to the remote server', type=str, widget='FileChooser')
    # curl_parser.add_argument('--connect-timeout', help='Maximum time in seconds that you allow curl\'s connection to take')
    # curl_parser.add_argument('--user-agent', help='Specify the User-Agent string ')
    # curl_parser.add_argument('--cookie', help='Pass the data to the HTTP server as a cookie')
    # curl_parser.add_argument('--dump-header', type=argparse.FileType(), help='Write the protocol headers to the specified file')
    # curl_parser.add_argument('--progress-bar', action="store_true", help='Make curl display progress as a simple progress bar')
    # curl_parser.add_argument('--http2', action="store_true", help='Tells curl to issue its requests using HTTP 2')
    # curl_parser.add_argument('--ipv4', action="store_true", help=' resolve names to IPv4 addresses only')

    # # ########################################################
    # siege_parser = subs.add_parser(
    #     'siege', help='Siege is an http/https regression testing and benchmarking utility')
    # siege_parser.add_argument('--get', help='Pull down headers from the server and display HTTP transaction', type=str)
    # siege_parser.add_argument('--concurrent', help='Stress the web server with NUM number of simulated users', type=int)
    # siege_parser.add_argument('--time', help='allows you to run the test for a selected period of time', type=int)
    # siege_parser.add_argument('--delay', help='simulated user is delayed for a random number of seconds between one and NUM', type=int)
    # siege_parser.add_argument('--message', help='mark the log file with a separator', type=int)

    # # ########################################################
    # ffmpeg_parser = subs.add_parser(
    #     'ffmpeg', help='A complete, cross-platform solution to record, convert and stream audio and video')
    # ffmpeg_parser.add_argument('Output', help='Pull down headers from the server and display HTTP transaction', widget='FileSaver', type=argparse.FileType())
    # ffmpeg_parser.add_argument('--bitrate', help='set the video bitrate in kbit/s (default = 200 kb/s)', type=str)
    # ffmpeg_parser.add_argument('--fps', help='set frame rate (default = 25)', type=str)
    # ffmpeg_parser.add_argument('--size', help='set frame size. The format is WxH (default 160x128)', type=str)
    # ffmpeg_parser.add_argument('--aspect', help='set aspect ratio (4:3, 16:9 or 1.3333, 1.7777)', type=str)
    # ffmpeg_parser.add_argument('--tolerance', help='set video bitrate tolerance (in kbit/s)', type=str)
    # ffmpeg_parser.add_argument('--maxrate', help='set min video bitrate tolerance (in kbit/s)', type=str)
    # ffmpeg_parser.add_argument('--bufsize', help='set ratecontrol buffere size (in kbit)', type=str)

    #设置默认函数
    parser.set_defaults(func=message_help)
    args = parser.parse_args()
    #执行函数功能
    args.func(args)

if __name__ == '__main__':
    main()