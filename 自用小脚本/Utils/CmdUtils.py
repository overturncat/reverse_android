import os
import subprocess


class CmdUtils(object):
    @staticmethod
    def runcmd(cmdline, prompt = "执行命令"):
        print(prompt + ": "+cmdline)
        result = os.popen(cmdline)
        print("输出: " + result.read())
        return result.read()
        
    @staticmethod
    def runcmd_cwd(cmdline, cwd , prompt = "执行命令"):
        print(prompt + ": "+cmdline)
        p = subprocess.Popen(cmdline, shell=True, cwd=cwd)
        retcode = p.wait()
        return retcode
        