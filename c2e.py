import signal
import wave
import sys
import json
import requests
import time
import os
import base64
from pyaudio import PyAudio, paInt16
import webbrowser
from fetchToken import fetch_token
import time
import unicodedata

IS_PY3 = sys.version_info.major == 3
if IS_PY3:
    from urllib.request import urlopen
    from urllib.request import Request
    from urllib.error import URLError
    from urllib.parse import urlencode
    from urllib.parse import quote_plus
else:
    import urllib2
    from urllib import quote_plus
    from urllib2 import urlopen
    from urllib2 import Request
    from urllib2 import URLError
    from urllib import urlencode

interrupted = False  # snowboy监听唤醒结束标志
endSnow = False  # 程序结束标志

framerate = 16000  # 采样率
num_samples = 2000  # 采样点
channels = 1  # 声道
sampwidth = 2  # 采样宽度2bytes


music_exit = 'audio/exit.wav'  # 唤醒系统退出语音
music_open = 'audio/open.wav'  # 唤醒系统打开语音
music_alarm = 'audio/alarm.wav'
music_listen = 'audio/listen.wav'
music_over = 'audio/over.wav'
# os.close(sys.stderr.fileno())  # 去掉错误警告

FILEPATH = 'audio/audio.wav'
MAPPATH = 'cemap.txt'
SRC = 'ChineseRequirements.txt'
requirementTexts = ''

def get_audio(file):
    """
    获取音频文件
    """
    with open(file, 'rb') as f:
        data = f.read()
    return data

def play(filename):
    """
    播放音频
    """
    wf = wave.open(filename, 'rb')  # 打开audio.wav
    p = PyAudio()  # 实例化 pyaudio
    # 打开流
    stream = p.open(format=p.get_format_from_width(wf.getsampwidth()),
                    channels=wf.getnchannels(),
                    rate=wf.getframerate(),
                    output=True)
    data = wf.readframes(1024)
    while data != b'':
        data = wf.readframes(1024)
        stream.write(data)
    # 释放IO
    stream.stop_stream()
    stream.close()
    p.terminate()


def save_wave_file(filepath, data):
    """
    存储文件
    """
    wf = wave.open(filepath, 'wb')
    wf.setnchannels(channels)
    wf.setsampwidth(sampwidth)
    wf.setframerate(framerate)
    wf.writeframes(b''.join(data))
    wf.close()

def speech2text(speech_data, token, dev_pid=1537):
    """
    音频转文字
    """
    FORMAT = 'wav'
    RATE = '16000'
    CHANNEL = 1
    CUID = 'baidu_workshop'
    SPEECH = base64.b64encode(speech_data).decode('utf-8')
    data = {
        'format': FORMAT,
        'rate': RATE,
        'channel': CHANNEL,
        'cuid': CUID,
        'len': len(speech_data),
        'speech': SPEECH,
        'token': token,
        'dev_pid': dev_pid
    }

    # 语音转文字接口 该接口可能每个人不一样，取决于你需要哪种语音识别功能，本文使用的是 语音识别极速版

    url = 'https://vop.baidu.com/pro_api'
    headers = {'Content-Type': 'application/json'}  # 请求头
    print('正在识别...')
    r = requests.post(url, json=data, headers=headers)
    Result = r.json()
    if 'result' in Result:
        return Result['result'][0]
    else:
        return Result


def my_record():
    """
    录音
    """
    pa = PyAudio()
    global requirementTexts
    text = ''
    TOKEN = fetch_token()  # 获取token
    while text != '再见。':
        stream = pa.open(format=paInt16, channels=channels, rate=framerate, input=True, frames_per_buffer=num_samples)
        my_buf = []
        print('开始录音...')
        for i in range(0, int(framerate / num_samples * 5)):
            string_audio_data = stream.read(num_samples)
            my_buf.append(string_audio_data)
            save_wave_file(FILEPATH, my_buf)
        speech = get_audio(FILEPATH)
        result = speech2text(speech, TOKEN, int(80001))
        text = result
        print(text)
        if text == '':text = '再见。'
        if text != '再见。':
            try:
                eRequirement = c2e(text).strip()
                print(eRequirement)
                requirementTexts = requirementTexts + eRequirement + '//'
                play(music_listen)
            except Exception as e:
                pass
        stream.close()
    print('再见!')
    play(music_over)
    if requirementTexts != '':
        os.system("java -jar iftttgenerator.jar \"" + requirementTexts + "\"")

def getRequirementTexts(srcPath):
    requirementTexts = ''
    for line in open(srcPath,'r'):
        eRequirement = c2e(line).strip()
        requirementTexts = requirementTexts + eRequirement + '//'
    requirementTexts = requirementTexts[:-2]
    # print(requirementTexts)
    os.system("java -jar iftttgenerator.jar \"" + requirementTexts + "\"")


def c2e(cRequirement):
    cRequirement = str(cRequirement)
    cRequirement = cRequirement.replace("，","")
    cRequirement = cRequirement.replace("。", "")
    cRequirement = cRequirement.replace("！", "")
    cRequirement = cRequirement.replace("？", "")
    eRequirement = ''
    cemap = initDict(MAPPATH)
    rooms = ["办公室"]
    room = ''
    for temp in rooms:
        if cRequirement.startswith(temp):
            room = temp
            cRequirement = cRequirement[cRequirement.index(room) + len(room):]
            break
    if room != '':
        room = cemap[room].strip()
    # command with time
    if cRequirement.find('秒后') != -1:
        timeValue = cRequirement.split('秒后')[0]
        timeValue = toDigit(timeValue)
        cRequirement = cRequirement.split('秒后')[1]
        eRequirement = room + ":" + cemap[cRequirement].strip() + " IN " + timeValue.strip() + "s"
    elif cRequirement.find('分后') != -1:
        timeValue = cRequirement.split('分后')[0]
        timeValue = toDigit(timeValue)
        cRequirement = cRequirement.split('分后')[1]
        eRequirement = room + ":" + cemap[cRequirement].strip() + " IN " + timeValue.strip() + "m"
    elif cRequirement.find('分钟后') != -1:
        timeValue = cRequirement.split('分钟后')[0]
        timeValue = toDigit(timeValue)
        cRequirement = cRequirement.split('分钟后')[1]
        eRequirement = room + ":" + cemap[cRequirement].strip() + " IN " + timeValue.strip() + "m"
    elif cRequirement.find('小时后') != -1:
        timeValue = cRequirement.split('小时后')[0]
        timeValue = toDigit(timeValue)
        cRequirement = cRequirement.split('小时后')[1]
        eRequirement = room + ":" + cemap[cRequirement].strip() + " IN " + timeValue.strip() + "h"
    #command without time
    elif cRequirement in cemap:
        if room != '':
            eRequirement = room + ":" + cemap[cRequirement]
        else:
            eRequirement = cemap[cRequirement]
    #rules
    elif cRequirement.find('保持在') != -1:
        attribute = cemap[cRequirement.split('保持在')[0]].strip()
        value = getValue(cRequirement.split('保持在')[1])
        if room != '':
            eRequirement = room + ":PREFERRED " + attribute + " IS " + value;
        else:
            eRequirement = "PREFERRED " + attribute + " IS " + value;
    elif cRequirement.find('不能高于') != -1:
        attribute = cemap[cRequirement.split('不能高于')[0]].strip()
        value = getValue(cRequirement.split('不能高于')[1]).strip()
        if room != '':
            eRequirement = room + ":" + attribute + " SHOULD NEVER BE ABOVE " + value;
        else:
            eRequirement = attribute + " SHOULD NEVER BE ABOVE " + value;
    elif cRequirement.find('必须高于') != -1:
        attribute = cemap[cRequirement.split('必须高于')[0]].strip()
        value = getValue(cRequirement.split('必须高于')[1]).strip()
        if room != '':
            eRequirement = room + ":" + attribute + " SHOULD ALWAYS BE ABOVE " + value;
        else:
            eRequirement = attribute + " SHOULD ALWAYS BE ABOVE " + value;
    elif cRequirement.find('不能低于') != -1:
        attribute = cemap[cRequirement.split('不能低于')[0]].strip()
        value = getValue(cRequirement.split('不能低于')[1]).strip()
        if room != '':
            eRequirement = room + ":" + attribute + " SHOULD NEVER BE BELOW " + value;
        else:
            eRequirement = attribute + " SHOULD NEVER BE BELOW " + value;
    elif cRequirement.find('必须低于') != -1:
        attribute = cemap[cRequirement.split('必须低于')[0]].strip().strip()
        value = getValue(cRequirement.split('必须低于')[1]).strip()
        if room != '':
            eRequirement = room + ":" + attribute + " SHOULD ALWAYS BE BELOW " + value;
        else:
            eRequirement = attribute + " SHOULD ALWAYS BE BELOW " + value;
    elif cRequirement.find('时') != -1:
        if cRequirement.find('超过') != -1:
            if cRequirement.find('超过') != -1:
                trigger = getTrigger(cRequirement.split('超过')[0], cemap).strip()
                triggerRoom = room
                time = getTime(cRequirement.split("超过")[1].split("时")[0]).strip()
                timeValue = time[:-1]
                timeUnit = time[-1]
                timeValue = toDigit(timeValue)
                time = timeValue + timeUnit
                action = cRequirement.split("时")[1].strip()
                actionRoom = ''
                flag = False
                for temp in rooms:
                    if action.startswith(temp):
                        flag = True
                        actionRoom = cemap[temp.strip()].strip()
                        action = cemap[action[action.index(temp) + len(temp):]]
                        break
                if flag == False:
                    action = cemap[action]
                if room != '':
                    eRequirement = room + ":IF " + trigger + " FOR " + time + " THEN " + action;
                elif actionRoom != '':
                    eRequirement = triggerRoom + ',' + actionRoom + ":IF " + trigger + " FOR " + time + " THEN " + action
                else:
                    eRequirement = "IF " + trigger + " FOR " + time + " THEN " + action;
        else:
            trigger = getTrigger(cRequirement.split("时")[0],cemap).strip()
            triggerRoom = room
            action = cRequirement.split("时")[1].strip()
            actionRoom = ''
            for temp in rooms:
                if action.startswith(temp):
                    actionRoom = cemap[temp.strip()].strip()
                    action = cemap[action[action.index(temp) + len(temp):]]
                    break
            if room == '':
                eRequirement = "IF " + trigger + " THEN " + cemap[action];
            elif actionRoom != '':
                eRequirement = triggerRoom + ',' + actionRoom + ":IF " + trigger + " THEN " + action
            else:
                eRequirement = room + ":IF " + trigger + " THEN " + cemap[action]
            # if room != '':
            #     eRequirement = room + ":IF " + trigger + " THEN " + action
            # else:
            #     eRequirement = "IF " + trigger + " THEN " + action;
    return eRequirement


def initDict(mapPath):
    d = dict()
    for line in open(mapPath, 'r'):
        if line.find('->') != -1:
            d[line.split('->')[0]] = line.split('->')[1]
    return d


def getValue(tempValue):
    value = ''
    for char in tempValue:
        if is_number(char):
            value = value + char
    return value


def is_number(s):
    try:  # 如果能运行float(s)语句，返回True（字符串s是浮点数）
        float(s)
        return True
    except ValueError:  # ValueError为Python的一种标准异常，表示"传入无效的参数"
        pass  # 如果引发了ValueError这种异常，不做任何事情（pass：不做任何事情，一般用做占位语句）
    try:
        import unicodedata  # 处理ASCii码的包
        unicodedata.numeric(s)  # 把一个表示数字的字符串转换为浮点数返回的函数
        return True
    except (TypeError, ValueError):
        pass
    return False


def getTrigger(trigger, d):
    if trigger.find("且") == -1:
        if trigger in d:
            trigger = d[trigger]
        else:
            if trigger.find('高于') != -1:
                attribute = trigger.split("高于")[0]
                tempValue = trigger.split("高于")[1]
                value = getValue(tempValue)
                trigger = d[attribute].strip() + ">" + value
            elif trigger.find('低于') != -1:
                attribute = trigger.split("低于")[0]
                tempValue = trigger.split("低于")[1]
                value = getValue(tempValue)
                trigger = d[attribute].strip() + "<" + value
            elif trigger.find('等于') != -1:
                attribute = trigger.split("等于")[0]
                tempValue = trigger.split("等于")[1]
                value = getValue(tempValue)
                trigger = d[attribute].strip() + "=" + value
    else:
        trigger1 = trigger.split('且')[0]
        trigger2 = trigger.split('且')[1]
        trigger = getTrigger(trigger1, d) + " AND " + getTrigger(trigger2,d)
    return trigger.strip()


def getTime(time):
    if time.find('秒') != -1:
        value = time.split("秒")[0]
        return value + 's'
    elif time.find('分钟') != -1:
        value = time.split("分钟")[0]
        return value + 'm'
    elif time.find('小时') != -1:
        value = time.split("小时")[0]
        return value + 'h'

def toDigit(timeValue):
    if timeValue.isdigit():
        return timeValue
    else:
        return str(int(unicodedata.numeric(timeValue)))

my_record()