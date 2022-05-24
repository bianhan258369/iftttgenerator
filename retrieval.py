import math
import pickle
import re

import Levenshtein
import cn2an
import pypinyin

place_list = ['办公室', '屋内']
action_list = ['关灯', '开灯', '关闭空气净化器', '打开空气净化器', '关闭加湿器', '打开加湿器', '保持空气湿润', '关电扇', '开电扇', '启动扫地机器人', '停止扫地机器人', '打扫卫生',
               '允许监视室内', '禁止监视室内', '亮一点', '暗一点', '热一点', '冷一点', '温度高一点', '温度低一点', '二氧化碳浓度需要降低', '湿度需要增加']
device_status = ['门打开', '门关闭', '窗户打开', '窗户关闭', '灯打开', '灯关闭']
trigger_list = ['有人', '没人', '天黑']
trigger_list.extend(device_status)
time_list = ['超过?小时', '超过?分钟', '超过?秒']
attribute_dict = {'温度': '?度', '亮度': '?勒克斯', '湿度': '?%', '二氧化碳浓度': '?'}

special_sents = ['再见']

re_chn = re.compile("([\u4E00-\u9Fa5a-zA-Z0-9+#&]+)")

number_modifiers = '(超过|高于|低于|小于|保持在)'
number_replacement = '?'


def chn_to_arabic(text: str):
    result = text
    chn_numbers = re.findall(r'(超过)(.+)(秒|分钟|小时)(时)', text)
    if len(chn_numbers) > 0:
        temp = list(chn_numbers[0])
        temp[1] = cn2an.transform(temp[1], 'cn2an')
        result = result.replace(''.join(chn_numbers[0]),
                                ''.join(temp))
    pattern = number_modifiers + '([\u4E00-\u9Fa5]{1,6})'
    chn_numbers = re.findall(pattern, result)
    if len(chn_numbers) > 0:
        temp = list(chn_numbers[0])
        temp[1] = cn2an.transform(temp[1], 'cn2an')
        result = result.replace(''.join(chn_numbers[0]),
                                ''.join(temp))
    return result


def text_extract(sent: str):
    text = ""
    blks = re_chn.split(sent)
    for blk in blks:
        if not blk:
            continue
        if re_chn.match(blk):
            text += blk
    return text


def num_extract(text: str):
    pattern = number_modifiers + r'(\d+)'
    matches = re.findall(pattern, text)
    numbers = []
    new_text = text
    for match in matches:
        numbers.append(match[1])
        orig = ''.join(match)
        temp = list(match)
        temp[1] = number_replacement
        repl = ''.join(temp)
        new_text = text.replace(orig, repl, 1)

    pattern = r'(\d+)(分钟|小时|秒)'
    matches = re.findall(pattern, text)
    for match in matches:
        numbers.append(match[0])
        orig = ''.join(match)
        temp = list(match)
        temp[0] = number_replacement
        repl = ''.join(temp)
        new_text = text.replace(orig, repl, 1)
    return new_text, numbers


class Retrieval(object):
    def __init__(self):
        self.map_length_to_sent_and_pinyin = {}
        self.initialized = False

    def add_to_map(self, sent):
        sent_pinyin = pypinyin.slug(sent, separator='')
        if len(sent_pinyin) in self.map_length_to_sent_and_pinyin:
            self.map_length_to_sent_and_pinyin[len(sent_pinyin)].append((sent_pinyin, sent))
        else:
            self.map_length_to_sent_and_pinyin[len(sent_pinyin)] = [(sent_pinyin, sent)]

    def _initialize(self):

        for sent in special_sents:
            self.add_to_map(sent)

        # action
        for action in action_list:
            self.add_to_map(action)
        # place + action
        for place in place_list:
            for action in action_list:
                sent = place + action
                self.add_to_map(sent)
        # place one + trigger (+ place two) + action
        for place_one in place_list:
            for trigger in trigger_list:
                for action in action_list:
                    sent = place_one + trigger + '时' + action
                    self.add_to_map(sent)
                for place_two in place_list:
                    for action in action_list:
                        sent = place_one + trigger + '时' + place_two + action
                        self.add_to_map(sent)
        # trigger (+ time) + action
        for trigger in trigger_list:
            for time in time_list:
                for action in action_list:
                    sent = '{}{}时{}'.format(trigger, time, action)
                    self.add_to_map(sent)
            for action in action_list:
                sent = '{}时{}'.format(trigger, action)
                self.add_to_map(sent)
        # place one + trigger + time (+ place two) + action
        for place_one in place_list:
            for trigger in trigger_list:
                for time in time_list:
                    for action in action_list:
                        sent = place_one + trigger + time + '时' + action
                        self.add_to_map(sent)
                    for place_two in place_list:
                        for action in action_list:
                            sent = place_one + trigger + time + '时' + place_two + action
                            self.add_to_map(sent)

        for attribute, unit in attribute_dict.items():
            sent = "{}保持在{}".format(attribute, unit)
            self.add_to_map(sent)
            sent1 = "{}不能高于{}".format(attribute, unit)
            sent2 = "{}不能低于{}".format(attribute, unit)
            sent3 = "{}必须高于{}".format(attribute, unit)
            sent4 = "{}必须低于{}".format(attribute, unit)
            self.add_to_map(sent1)
            self.add_to_map(sent2)
            self.add_to_map(sent3)
            self.add_to_map(sent4)
        for place in place_list:
            for attribute, unit in attribute_dict.items():
                sent = "{}{}保持在{}".format(place, attribute, unit)
                self.add_to_map(sent)
                sent1 = "{}{}不能高于{}".format(place, attribute, unit)
                sent2 = "{}{}不能低于{}".format(place, attribute, unit)
                sent3 = "{}{}必须高于{}".format(place, attribute, unit)
                sent4 = "{}{}必须低于{}".format(place, attribute, unit)
                self.add_to_map(sent1)
                self.add_to_map(sent2)
                self.add_to_map(sent3)
                self.add_to_map(sent4)

        length = len(device_status)
        for k in range(0, length):
            for j in range(k + 1, length):
                sent = "{}和{}不能同时发生".format(device_status[k], device_status[j])
                self.add_to_map(sent)
        for place in place_list:
            length = len(device_status)
            for k in range(0, length):
                for j in range(k + 1, length):
                    sent = "{}{}和{}不能同时发生".format(place, device_status[k], device_status[j])
                    self.add_to_map(sent)

        self.initialized = True

    def save_sent_data(self):
        self._initialize()
        with open('data/sent_data.pickle', 'wb+') as f:
            pickle.dump(self.map_length_to_sent_and_pinyin, f, pickle.DEFAULT_PROTOCOL)

    def load_sent_data(self):
        with open('data/sent_data.pickle', 'rb') as f:
            self.map_length_to_sent_and_pinyin = pickle.load(f)
        self.initialized = True

    def retrieve(self, sent):
        if not sent:
            return sent
        text = text_extract(sent)
        text = chn_to_arabic(text)
        text, nums = num_extract(text)
        if not text:
            return text
        if not self.initialized:
            self.load_sent_data()
        sent_pinyin = pypinyin.slug(text, separator='')
        length = len(sent_pinyin)
        interval = 10
        top_sent = text
        min_distance = math.inf
        for cl in range(max(0, length - interval), length + interval):
            if cl in self.map_length_to_sent_and_pinyin:
                pair_list = self.map_length_to_sent_and_pinyin[cl]
                for pair in pair_list:
                    distance = Levenshtein.distance(pair[0], sent_pinyin)
                    if min_distance > distance:
                        min_distance = distance
                        top_sent = pair[1]
        res = top_sent
        for number in nums:
            res = res.replace(number_replacement, number, 1)
        return res


if __name__ == '__main__':
    r = Retrieval()
    r.save_sent_data()
