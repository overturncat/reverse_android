# -*- coding:utf-8 -*-
import logging
import os
from uuid import uuid1
from Utils.FileUtils import FileUtils
from datetime import datetime
from loguru import logger


MAX_LOG_SIZE= "100MB"

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
class LoggerUtils:
    def __init__(self):

        # 设置日志输出文件夹
        log_path = os.path.dirname(FileUtils.getFilePath(__file__))
        log_path = FileUtils.getFilePathWithOffset(log_path, "../logs/")
        random_uuid = uuid1().hex
        now = datetime.now()
        self.sub_log_path = FileUtils.getFilePathWithOffset(log_path, now.strftime('%Y-%m-%d-%H:%M:%S') + "_" + random_uuid)
        FileUtils.createDir(self.sub_log_path)
        sub_log_file = FileUtils.getFilePathWithOffset(self.sub_log_path, "{time}.log")
        #创建一个handler，用于将日志输出到控制台
        logger.add(sub_log_file,   # 指定文件
               format="{time:YYYY-MM-DD HH:mm:ss}  | {level}> {elapsed}  | {message}",
               encoding='utf-8',   
            # retention='1 days',  # 设置历史保留时长
               backtrace=True,  # 回溯
               diagnose=True,   # 诊断
               enqueue=True,   # 异步写入
               rotation=MAX_LOG_SIZE,  # 切割，设置文件大小，rotation="12:00"，rotation="1 week"
               # filter="my_module"  # 过滤模块
               # compression="zip"   # 文件压缩
            ) 

    def getLogfileList(self):
        return FileUtils.getFileslistInDirR(self.sub_log_path)

    def get_log(self):
        """定义一个函数，回调logger实例"""
        return logger

    def getExceptionInfo(self, e):
        logger.error("异常类型： "+repr(e))
        logger.error("异常堆栈： "+str(logging.exception(e)))
        return "异常类型： "+repr(e)