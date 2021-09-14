import paramiko
import time

REMOTEPATH = "/root/smarthome_data/"

def uploadFile(localFilePath):
    # 创建ssh对象
    ssh = paramiko.SSHClient()
    # ssh.load_host_keys("C:/Users/Administrator/.ssh/known_hosts")
    # 允许连接不在know_hosts文件的主机上
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    # 本地文件路径
    # 服务器的文件路径
    currenttime = time.localtime(time.time())
    remoteFileName = str(currenttime.tm_year) + str(currenttime.tm_mon).zfill(2) + str(currenttime.tm_mday).zfill(
        2) + str(currenttime.tm_hour).zfill(2) + str(currenttime.tm_min).zfill(2) + str(currenttime.tm_sec).zfill(2)
    remotepath = REMOTEPATH + remoteFileName + ".wav"
    # 可设置多台服务器，尽量服务器的密码保持一致
    server = "1.117.155.93"
    # words = server.split(",")
    # for word in words:
    # 连接服务器
    print(server, '开始数据传输')
    ssh.connect(server, username="root", password="Taolunban.1102")
    sftp = ssh.open_sftp()
    sftp.put(localFilePath, remotepath, callback=None)
    # 关闭连接
    ssh.close()
    print('数据传输完成')

# 创建ssh对象
ssh = paramiko.SSHClient()
# ssh.load_host_keys("C:/Users/Administrator/.ssh/known_hosts")
# 允许连接不在know_hosts文件的主机上
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
# 本地文件路径
localpath = "./audio/audio.wav"
# 服务器的文件路径
currenttime = time.localtime(time.time())
remoteFileName = str(currenttime.tm_year) + str(currenttime.tm_mon).zfill(2) + str(currenttime.tm_mday).zfill(2) + str(currenttime.tm_hour).zfill(2)+ str(currenttime.tm_min).zfill(2)+ str(currenttime.tm_sec).zfill(2)
remotepath = "/root/smarthome_data/" + remoteFileName + ".wav"
# 可设置多台服务器，尽量服务器的密码保持一致
server = "1.117.155.93"
# words = server.split(",")
# for word in words:
# 连接服务器
print(server, '开始数据传输')
ssh.connect(server, username="root", password="Taolunban.1102")
sftp = ssh.open_sftp()
sftp.put(localpath, remotepath, callback=None)
# 关闭连接
ssh.close()
print('数据传输完成')