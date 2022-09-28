import time 
import os
import sys  
import argparse
from Utils.CmdUtils import CmdUtils
from Utils.FileUtils import FileUtils

SERVER_PATH = "/home/ayuan/.wine/drive_c/IDA/dbgsrv/android_server"


def parse_arguments(argv):    
    parser = argparse.ArgumentParser()
    # 添加参数名、默认值，类型  
    parser.add_argument('--StartIdaServer',action='store_true', help='上传Ida服务端')
    return parser.parse_args(argv)

def main(args): #接收输入参数#打印输入的参数
    if(args.StartIdaServer):
        CmdUtils.runcmd('adb root')
        # 上传 IDA 调试服务端
        CmdUtils.runcmd("adb push {} /data/local/tmp/".format(SERVER_PATH))
        # 修改文件权限，测试运行
        CmdUtils.runcmd('adb shell "chmod +x /data/local/tmp/{}"'.format(FileUtils.get_file_name(SERVER_PATH)))
        # 带参数，修改端口运行
        CmdUtils.runcmd('adb shell "nohup /data/local/tmp/{} -p23456 2>&1 &"'.format(FileUtils.get_file_name(SERVER_PATH)))
        # 设置端口转发
        CmdUtils.runcmd("adb forward tcp:23456 tcp:23456")
    else:
        print("--help 查询帮助文档")

if __name__ == "__main__":
    start = time.time()
    main(parse_arguments(sys.argv[1:]))