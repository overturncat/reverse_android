'''
Author: theoverturncat yuanahiguoabc@gmail.com
Date: 2022-10-08 15:37:00
LastEditors: theoverturncat yuanahiguoabc@gmail.com
LastEditTime: 2022-10-08 15:37:01
FilePath: /自用小脚本/Utils/DateUtils.py
Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
'''

from datetime import datetime, timedelta

class DateUtils(object):
    def __init__(self, *args):
        super(DateUtils, self).__init__()
    
    '''
    description: 获取datetime对象, 如昨天calDate(days=-1)
    return {*} datetime.datetime 对象
    '''
    @staticmethod
    def getDatetime(days = 0, seconds=0, microseconds=0, milliseconds=0, minutes=0, hours=0, weeks=0):
        # 时间计算
        now = datetime.now()
        dt = now +timedelta(days = days, seconds=seconds, microseconds=microseconds, milliseconds=milliseconds, minutes=minutes, hours=hours, weeks=weeks)
        return dt

    '''
    description: 获取datetime对象, 从时间戳中
    param {*} timestamp: 1429417200.0
    return {*} datetime.datetime 对象
    '''
    def getDatetimeFromTimestamp(timestamp):
        # 注意到timestamp是一个浮点数，它没有时区的概念，而datetime是有时区的。上述转换是在timestamp和本地时间做转换。
        # print(datetime.fromtimestamp(timestamp))   2015-04-19 12:20:00
        # timestamp也可以直接被转换到UTC标准时区的时间，此刻的格林威治标准时间与北京时间差了8小时
        # print(datetime.utcfromtimestamp(timestamp)) UTC时间 2015-04-19 04:20:00
        return datetime.fromtimestamp(timestamp)
        
    '''
    description: 获取datetime对象, 从str时间中
    param {*} str
    param {*} format
    return {*}
    '''
    def getDatetimeFromStr(str="2015-6-1 18:19:59", format="%Y-%m-%d-%H:%M:%S"):
        return datetime.strptime(str, format)
    
    ''' 
    
    description: 获取datetime的字符串表示形式
    param {*} format
    return {*} 2022-07-08 21:59:18
    '''
    @staticmethod
    def getDateStr(dt:datetime, format= "%Y-%m-%d-%H:%M:%S"):
        return dt.strftime(format)

    '''
    description: 返回datetime的时间戳形式
    param {datetime} dt
    param {*} format
    return {*} int格式: 1657374185,float格式: 1657374017.353718
    '''
    @staticmethod
    def getTimestampFromDatetime(dt:datetime, format = "float"):
        if format == "float":
            return dt.timestamp()
        if format == "int": 
            return int(dt.timestamp())

if __name__ == '__main__':
    # "date": "20220708",昨天
    print(DateUtils.getDataStr(DateUtils.getDatetime(days=-1), "%Y%m%d"))
    # "created": 1657282125, 昨天
    print(DateUtils.getTimestampFromDatetime(DateUtils.getDatetime(days=-1), format="int"))
