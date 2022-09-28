import os
import logging
import shutil
import zipfile

class FileUtils(object):
    '''
    description: 获取文件的绝对路径
    return {*}
    '''
    @staticmethod
    def getFilePath(path=__file__):
        return os.path.abspath(path)

    '''
    description: 获取路径的相对偏移的地址
    return {*}
    '''
    @staticmethod
    def getFilePathWithOffset(path, offset):
        return os.path.abspath(os.path.join(path, offset))
    
    @staticmethod
    def showDirectory(dir):
        listModules = []
        g = os.walk(dir)  
        for path,dir_list,file_list in g:  
            # 遍历指定目录下的所有文件
            for file_name in file_list:  
                listModules.append(file_name)
                # print(os.path.join(path, file_name) )
        return listModules


                
    @staticmethod
    def get_file_size(src):
        """ 文件大小
            Parameters
            ----------
            src : 源路径
        """
        try:
            size = os.path.getsize(src)
            return size
        except Exception as err:
            logging.error(err)

    @staticmethod
    def get_folder_size(src):
        """ 文件夹大小
            Parameters
            ----------
            src : 文件路径
        """
        sumsize = 0
        try:
            filename = os.walk(src)
            for root, dirs, files in filename:
                for fle in files:
                    size = os.path.getsize(src + fle)
                    sumsize += size
            return format_size(sumsize)
        except Exception as err:
            logging.error(err)

    @staticmethod
    def format_size(bytes):
        """ 格式化文件大小
            Parameters
            ----------
            bytes : 字节大小
        """
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
    def get_file_extension(src):
        """ 获取文件后缀名
            Parameters
            ----------
            src : 文件路径
        """
        return os.path.splitext(src)[1]

    @staticmethod
    def get_file_name(src):
        """ 获取文件名
            Parameters
            ----------
            src : 文件路径
        """
        return os.path.basename(src)

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