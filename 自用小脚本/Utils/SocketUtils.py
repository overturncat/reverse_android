#!/usr/bin/env python
import random
import socket
import struct
import time

from Utils.FileUtils import FileUtils

ssl_sessions = {}

class SocketUtils(object):
    '''
    description: 把ip字符串 装换为 主机序整数
    param {*} addr： # 192.168.1.100 
    return {*} ： 0xc0a80164
    '''
    @staticmethod
    def ip2int(addr):
        return struct.unpack("!I", socket.inet_aton(addr))[0]

    '''
    description: 把 主机序整数 装换为 ip字符串
    param {*} addr 主机序整数 0xc0a80164
    return {*} ip字符串 '10.0.0.1'
    '''
    @staticmethod
    def int2ip(addr):
        return socket.inet_ntoa(struct.pack("!I", addr))

    # 生成随机文件名.pcap, 并根据pcap文件格式创建pcap
    @staticmethod
    def createRanomPcap(path, prefix="",):
        # 首先创建文件
        filepath = FileUtils.createRandomFile(path,prefix ,"pcap")
        with open(filepath, "ab") as pcap_file:
            for writes in (
                    ("=I", 0xa1b2c3d4),  # Magic number
                    ("=H", 2),  # Major version number
                    ("=H", 4),  # Minor version number
                    ("=i", time.timezone),  # GMT to local correction
                    ("=I", 0),  # Accuracy of timestamps
                    ("=I", 65535),  # Max length of captured packets
                    ("=I", 228)):  # Data link type (LINKTYPE_IPV4)
                pcap_file.write(struct.pack(writes[0], writes[1]))
        return filepath

    # log_pcap(pcap_file, p["ssl_session_id"], p["function"], p["src_addr"],  p["src_port"], p["dst_addr"], p["dst_port"], data)
    """Writes the captured data to a pcap file.
    Args:
        pcap_file: The opened pcap file.
        ssl_session_id: The SSL session ID for the communication.
        function: The function that was intercepted ("SSL_read" or "SSL_write").
        src_addr: The source address of the logged packet.
        src_port: The source port of the logged packet.
        dst_addr: The destination address of the logged packet.
        dst_port: The destination port of the logged packet.
        data: The decrypted packet data.
    """
    @staticmethod
    def appendPcapData(filepath, ssl_session_id, function, src_addr, src_port, dst_addr, dst_port, data):
        t = time.time()
        if ssl_session_id not in ssl_sessions:
            ssl_sessions[ssl_session_id] = (random.randint(0, 0xFFFFFFFF), random.randint(0, 0xFFFFFFFF))
        client_sent, server_sent = ssl_sessions[ssl_session_id]

        if function == "SSL_read":
            seq, ack = (server_sent, client_sent)
        else:
            seq, ack = (client_sent, server_sent)
        
        with open(filepath, "ab") as pcap_file:
            for writes in (
                    # PCAP record (packet) header
                    ("=I", int(t)),  # Timestamp seconds
                    ("=I", int((t * 1000000) % 1000000)),  # Timestamp microseconds
                    ("=I", 40 + len(data)),  # Number of octets saved
                    ("=i", 40 + len(data)),  # Actual length of packet
                    # IPv4 header
                    (">B", 0x45),  # Version and Header Length
                    (">B", 0),  # Type of Service
                    (">H", 40 + len(data)),  # Total Length
                    (">H", 0),  # Identification
                    (">H", 0x4000),  # Flags and Fragment Offset
                    (">B", 0xFF),  # Time to Live
                    (">B", 6),  # Protocol
                    (">H", 0),  # Header Checksum
                    (">I", src_addr),  # Source Address
                    (">I", dst_addr),  # Destination Address
                    # TCP header
                    (">H", src_port),  # Source Port
                    (">H", dst_port),  # Destination Port
                    (">I", seq),  # Sequence Number
                    (">I", ack),  # Acknowledgment Number
                    (">H", 0x5018),  # Header Length and Flags
                    (">H", 0xFFFF),  # Window Size
                    (">H", 0),  # Checksum
                    (">H", 0)):  # Urgent Pointer
                pcap_file.write(struct.pack(writes[0], writes[1]))
            pcap_file.write(data)

        if function == "SSL_read" or function == "HTTP_recv":
            server_sent += len(data)
        else:
            client_sent += len(data)
        ssl_sessions[ssl_session_id] = (client_sent, server_sent)


if __name__ == '__main__':
    print(SocketUtils.int2ip(0xc0a80164)) # 192.168.1.100
    print(SocketUtils.ip2int('10.0.0.1')) # 167772161