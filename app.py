import csv
import requests
import os

from config import Config


class App(object):
    fmt_url = "http://app.cache.kairisei-ma.jp/i244590/chr51/chr51_%s.png"
    fmt_save = "%s/%s %s.png"

    def __init__(self) -> None:
        super().__init__()
        self.success_count = 0
        self.failed_count = 0
        self.c404s = []
        self.c404_path = Config.save_path + "/404.txt"
        if os.path.exists(self.c404_path):
            f = open(self.c404_path, "r")
            self.c404s = list(map(lambda x: x.strip(), f.readlines()))
            f.close()

    def run(self):
        file = open("card.csv", mode="r", encoding="utf-8")
        data = csv.reader(file)
        list_ = list(map(lambda v: v, data))
        for i in range(len(list_)):
            row = list_[i]
            print(i, end="\t")
            print(row)
            self.__request(row[0], row[5])
            print("\r\r")

    def __request(self, card_id: str, card_name: str):
        card_name = card_name.replace("/", "")
        path = App.fmt_save % (Config.save_path, card_id, card_name)
        url = App.fmt_url % card_id

        # 跳过已下载的立绘
        if os.path.exists(path):
            self.success_count += 1
            print(path + "\t exists, break")
            print("total: %d, success: %d, failed: %d" % (
                self.success_count + self.failed_count, self.success_count, self.failed_count))
            return

        # 跳过404的立绘
        if card_id in self.c404s:
            self.failed_count += 1
            print("404: " + url)
            print("total: %d, success: %d, failed: %d" % (
                self.success_count + self.failed_count, self.success_count, self.failed_count))
            return
        r = requests.get(url)
        code = r.status_code
        if code != 404:
            image = open(path, mode="wb")
            image.write(r.content)
            self.success_count += 1
            print(path)
        else:
            self.failed_count += 1
            print("404: " + url)
            self.__write404s(card_id)
        print("total: %d, success: %d, failed: %d" % (
            self.success_count + self.failed_count, self.success_count, self.failed_count))

    def __write404s(self, card_id: str):
        self.c404s.append(card_id)
        copy = list(map(lambda x: x + "\n", self.c404s.copy()))
        f = open(self.c404_path, "w")
        f.writelines(copy)
        f.close()


if __name__ == "__main__":
    App().run()
