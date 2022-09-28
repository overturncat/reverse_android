import os

class CmdUtils(object):
    @staticmethod
    def runcmd(cmdline):
        print("执行命令： "+cmdline)
        result = os.popen(cmdline)
        print(result.read())
        return  result.read()
        