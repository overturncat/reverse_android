import os
import sys
import threading
import time

from Utils.LoggerUtils import LoggerUtils
from Utils.FileUtils import FileUtils

# 定义监控的文件夹， 及其最大大小
CONFIG = [
    {"dir_path": "./exports", "size_max": 10, "size_del": 2}, 
    {"dir_path": "./logs", "size_max": 200, "size_del": 2}, 
]
# 定义导出文件的最大大小(单位为MB), 超过则进行分割
MAX_EXPORT_SIZE= 1

def singleton(cls):
    _instance = {}
    # 创建一个字典用来保存被装饰类的实例对象 _instance = {}
    def _singleton(*args, **kwargs):
        # 判断这个类有没有创建过对象，没有新创建一个，有则返回之前创建的  
        if cls not in _instance:
            _instance[cls] = cls(*args, **kwargs)
        return _instance[cls]
    return _singleton

@singleton
class MonitorUtils(object):
    def __init__(self):
        #创建一个logger
        self.RUNTIME_INSTANCE= []
        # 启动线程, 检测删除
        detect_thread = threading.Thread(target = self.monitorThread )
        detect_thread.start()

    def register(self, obj):
        # 遍历runtime_info 模块状态信息， 监控正在使用的文件
        self.RUNTIME_INSTANCE.append(obj)
        LoggerUtils().get_log().debug("监控模块： 注册监控实例{}".format(self.RUNTIME_INSTANCE))
    
    def unregister(self, obj):
        self.RUNTIME_INSTANCE.remove(obj)
        LoggerUtils().get_log().debug("监控模块： 注销监控实例{}".format(self.RUNTIME_INSTANCE))


    # 1文件目录   2文件夹最大大小(M)   3超过后要删除的大小(M)
    def checkDirSize(self, dir_path, size_Max, size_Del):
        LoggerUtils().get_log().debug("监控模块检测文件夹大小: "+dir_path +": "+str(FileUtils.getFolderSize(dir_path)) + "MB")
        # 检测所有实例的引用计数, 如果为4则注销该实例的监控
        LoggerUtils().get_log().debug("监控模块： 共监控模块： {}".format(self.RUNTIME_INSTANCE))
        for i in range(len(self.RUNTIME_INSTANCE)-1, -1, -1):
            LoggerUtils().get_log().debug("监控模块： {}引用计数检测{}".format(self.RUNTIME_INSTANCE[i],sys.getrefcount(self.RUNTIME_INSTANCE[i])))
            if(sys.getrefcount(self.RUNTIME_INSTANCE[i]) == 4):
                LoggerUtils().get_log().debug("监控模块： 注销监控实例{}".format(self.RUNTIME_INSTANCE[i]))
                self.RUNTIME_INSTANCE.pop(i)
        if FileUtils.getFolderSize(dir_path) > size_Max:
            # 文件按时间排序
            fileList = FileUtils.getOrderedFileslistInDir(dir_path)
            white_list = []
            # 处理所有注册实例的文件列表
            for obj in self.RUNTIME_INSTANCE:
                runtime_info = obj.getMofuleStatus()
                # 检测是否为空，直接跳过
                if(not runtime_info):
                    return
                # 遍历runtime_info 模块状态信息， 监控正在使用的文件, 添加白名单
                for item in runtime_info.values():
                    for export in item["export_path"].values():
                        white_list.extend(export["exports"])
                # 去除重复项
                white_list = list(set(white_list))
                LoggerUtils().get_log().debug("监控模块： 注册监控白名单{}".format(white_list))
            # 处理日志文件列表
            white_list.extend(LoggerUtils().getLogfileList())
            # 排除掉运行状态下的模块信息
            for i in range(len(fileList)-1, -1, -1):
                if fileList[i] in str(white_list):
                    item = fileList.pop(i)
                    LoggerUtils().get_log().debug("监控模块排除文件(不删除): "+item)
            for file in fileList:
                # 删除文件
                if FileUtils.getFolderSize(dir_path) > (size_Max - size_Del):
                    LoggerUtils().get_log().debug("监控模块删除文件: "+dir_path + file)
                    FileUtils.remove(FileUtils.getFilePathWithOffset(dir_path, file))

    #检测线程，每个5秒检测一次
    # @staticmethod
    def monitorThread(self):
        global CONFIG
        while True:
            for item in CONFIG:
                self.checkDirSize(item["dir_path"],item["size_max"],item["size_del"])
            time.sleep(5)
   
if __name__ == "__main__":
    #创建检测线程
    obj = MonitorUtils()