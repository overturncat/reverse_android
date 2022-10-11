from datetime import datetime
import os
import logging
import shutil
from uuid import uuid1
import zipfile
import json

class FileUtils(object):
    '''
    description: 获取文件的绝对路径
    return {*}
    '''
    @staticmethod
    def getFilePath( path=__file__):
        return os.path.abspath(path)

    '''
    description: 获取路径的相对偏移的地址
    return {*}
    '''
    @staticmethod
    def getFilePathWithOffset( path, offset):
        return os.path.abspath(os.path.join(path, offset))

    @staticmethod
    def getFileDir( src= __file__):
        """ 获取文件目录 """
        return os.path.dirname(src)

    @staticmethod
    def get_file_extension(src):
        """ 获取文件后缀名
        """
        return os.path.splitext(os.path.basename(src))[1]

    @staticmethod
    def get_file_nameNoEx(src):
        """ 获取文件名，不包含后缀 输入"123/123.txt" 返回123
        """
        return os.path.splitext(os.path.basename(src))[0]
    @staticmethod
    def get_file_name(src):
        """ 获取文件名, 输入"123/123.txt" 返回123.txt
        """
        return os.path.basename(src)


    '''
    description: 单层循环， 只找一层目录下的文件，返回文件名列表
    param {*} dirPath: 路径 ./modules/
    return {*}  文件名列表: ['hook_http.js', 'hook_https.js']
    '''
    @staticmethod
    def getFileslistInDir(dirPath):
        listModules = []
        g = os.walk(dirPath)  
        for path,dir_list,file_list in g:  
            # 遍历指定目录下的所有文件
            for file_name in file_list:  
                listModules.append(file_name)
                # print(os.path.join(path, file_name) )
        return listModules

    '''
    description: 递归查找，返回绝对路径列表
    param {*} dirPath: 绝对路径
    return {*}  文件名列表: ['hook_http.js', 'hook_https.js']
    '''
    @staticmethod
    def getFileslistInDirR(dirPath):
        listModules = []
        g = os.walk(dirPath)  
        for path,dir_list,file_list in g:  
            # 遍历指定目录下的所有文件
            for file_name in file_list:  
                listModules.append(FileUtils.getFilePathWithOffset(path, file_name))
                # print(os.path.join(path, file_name) )
            for dir in dir_list:  
                FileUtils.getFileslistInDirR(FileUtils.getFilePathWithOffset(path, dir))
        return listModules

    #文件列表按最后修改时间排序
    @staticmethod
    def getOrderedFileslistInDir(file_path):
        dir_list = FileUtils.getFileslistInDirR(file_path)
        if not dir_list:
            return []
        else:
            dir_list = sorted(dir_list, key=lambda x: os.path.getmtime(os.path.join(file_path, x)))
            return dir_list

    '''
    description: 获取文件的大小
    param {*} src: 文件路径
    return {*} 文件的大小
    '''
    @staticmethod
    def getFileSize(src):
        try:
            size = os.path.getsize(src)
            return size/1024/1024
        except Exception as err:
            logging.error(err)
    '''
    description: 获取文件夹中所有文件的总大小（不递归子文件夹）
    param {*} src
    return {*}
    '''
    @staticmethod
    def getFolderSize(src):
        sumsize = 0
        try:
            filename = os.walk(src)
            for root, dirs, files in filename:
                for fle in files:
                    size = os.path.getsize(FileUtils.getFilePathWithOffset(root,fle))
                    sumsize += size
            return sumsize/1024/1024
        except Exception as err:
            logging.error(err)

    """ 格式化文件大小
        Parameters
        ----------
        bytes : 字节大小
    """
    @staticmethod
    def format_size(bytes):
        try:
            bytes = float(bytes)
            kb = bytes / 1024
        except:
            logging.error("传入的字节格式不对")
            return "Error"
        if kb >= 1024:
            M = kb / 1024
            if M >= 1024:
                G = M / 1024
                return "%fG" % (G)
            else:
                return "%fM" % (M)
        else:
            return "%fkb" % (kb)



    @staticmethod
    def create(src):
        """创建空文件"""
        # 先创建路径
        FileUtils.createDir(FileUtils.getFileDir(src))
        # 再创建文件
        file = open(src,'w')
        file.close()
    
    # 生成随机文件， path: 绝对路径， 后缀: json
    @staticmethod
    def createRandomFile(path, prefix="", extention="json"):
        # 生成文件名
        random_uuid = uuid1().hex
        now = datetime.now().strftime('%Y-%m-%d-%H:%M:%S')
        filename = prefix + "_"+now + "_" +random_uuid + "." +extention
        # 路径
        filepath = FileUtils.getFilePathWithOffset(path, filename)
        # 创建文件
        FileUtils.create(filepath)
        return filepath


    @staticmethod
    def createDir(path):
        ''' 创建文件夹 '''
        if not os.path.exists(path):
            os.makedirs(path)    

    @staticmethod
    def move(src, dest):
        """ 移动
            Parameters
            ----------
            src  : 文件路径
            dest : 目标路径
        """
        if not os.path.isfile(src):
            logging.error("%s not exist!" % (src))
        else:
            fpath, fname = os.path.split(dest)
            if not os.path.exists(fpath):
                os.makedirs(fpath)
            shutil.move(src, dest)
            logging.debug("move %s -> %s" % (src, dest))

    @staticmethod
    def copy(src, dest):
        """ 复制
            Parameters
            ----------
            src  : 文件路径
            dest : 目标路径
        """
        if not os.path.isfile(src):
            logging.error("%s not exist!" % (src))
        else:
            fpath, fname = os.path.split(dest)
            if not os.path.exists(fpath):
                os.makedirs(fpath)
            shutil.copyfile(src, dest)
            logging.debug("copy %s -> %s" % (src, dest))

    @staticmethod
    def rename(src, dest):
        """ 复制
            Parameters
            ----------
            src  : 文件路径
            dest : 目标路径
        """
        try:
            os.rename(src, dest)
        except Exception as e:
            logging.error('rename file fail\r\n %s' % e)
        else:
            logging.info('rename file success\r\n')

    @staticmethod
    def remove(src):
        """ 删除
            Parameters
            ----------
            src  : 文件路径
        """
        if not os.path.isfile(src):
            logging.error("%s not exist!" % (src))
        else:
            os.remove(src)
            logging.debug("remove %s" % src)

    @staticmethod
    def uncompress(src, dest):
        """ 解压缩
            Parameters
            ----------
            src  : 文件路径
            dest : 目标路径
        """
        if not os.path.isfile(src):
            logging.error("%s not exist!" % (src))
        else:
            unc = zipfile.ZipFile(src)
            unc.extractall(path=dest)
            unc.close()

    @staticmethod
    def readString(path):
        with open(path, "r",encoding='utf-8') as fi:
            data = fi.read()
        return data

    @staticmethod
    def readJson(path):
        data_json = {}
        with open(path,"r+") as fi:
            update_str = fi.read()
            if update_str == "":
                update_str = "{}"
            data_json:dict = json.loads(update_str)
        return data_json

    @staticmethod
    def writeJson(path, data):
        with open(path,"w+") as fi:
            fi.write(json.dumps(data,ensure_ascii=False))

    @staticmethod
    def compress(src, dest, suffix='zip'):
        """ 压缩
            Parameters
            ----------
            src    : 文件路径
            dest   : 目标路径
            suffix : 压缩格式
        """
        compress = ['.zip', '.rar']
        src = os.path.abspath(src)
        suffix = '.' + suffix.lstrip('.')
        if os.path.isdir(src):
            if(os.path.splitext(dest)[1] not in compress):
                dirname = os.path.split(src)[1]
                dest = os.path.join(dest, (dirname + suffix))
            newZip = zipfile.ZipFile(dest, 'a')
            for dirpath, dirnames, filenames in os.walk(src):
                for dirname in dirnames:
                    dp = filepath = os.path.join(dirpath, dirname)
                    newZip.write(dp, dp[len(src):])
                for filename in filenames:
                    filepath = os.path.join(dirpath, filename)
                    newZip.write(filepath, filepath[len(src):])
        elif os.path.isfile(src):
            if(os.path.splitext(dest)[1] not in compress):
                filename = os.path.splitext(src)
                filename = os.path.split(filename[0])[1]
                dest = os.path.join(dest, (filename+suffix))
            newZip = zipfile.ZipFile(dest, 'a')
            newZip.write(src, src[len(os.path.split(src)[0]):])
        else:
            logging.info("%s is not supported" % src)
        newZip.close()
        logging.info('compress is successed -> %s' % dest)