#! /bin/sh
# Copyright (C) 2022 ayuan <ayuan@ayuan-X5>

SERVER_PATH=/home/ayuan/.wine/drive_c/IDA/dbgsrv/android_server

# 上传 IDA 调试服务端
adb push ${SERVER_PATH} /data/local/tmp/
# 修改文件权限，测试运行
adb shell "chmod +x /data/local/tmp/android_server"
# 带参数，修改端口运行
adb shell "nohup /data/local/tmp/android_server -p23456 2>&1 &"
# 设置端口转发
adb forward tcp:23456 tcp:23456
