package red.htt;

import red.htt.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author yui
 */
public class Ma {

    private final int sleep;

    /**
     * 立绘保存位置(不能包含空格).
     */
    private final String path;

    /**
     * 卡牌详情页里, 点击进入立绘页的坐标.
     */
    private final Point pot1;

    /**
     * 开始页的页码, 取值范围为: [1, 780].
     */
    private final int startPageNumber;

    /**
     * 左上第一张卡牌的点击位置
     */
    private final int leftUpCardX;
    private final int leftUpCardY;

    /**
     * 左右两张卡牌的横向间距, 及上下两张卡牌的纵向间距
     */
    private final int widthSpacing;
    private final int heightSpacing;

    /**
     * 卡牌详情页面里的详细按钮的点击位置
     */
    private final Point cardDetailPot;
    /**
     * 卡牌详情页面里的详细按钮的点击位置
     */
    private final Point nextPagePot;

    public Ma() throws IOException {
        Properties pp = new Properties();
        InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/app.properties"), StandardCharsets.UTF_8);
        pp.load(in);
        in.close();
        // 解析配置参数
        this.sleep = this.getIntProp(pp, "sleep.time");
        this.path = (String) pp.get("save.path");
        this.pot1 = new Point(this.getIntProp(pp, "pot1.x"), this.getIntProp(pp, "pot1.y"));
        this.startPageNumber = this.getIntProp(pp, "start.page.number");
        this.leftUpCardX = this.getIntProp(pp, "left.up.card.x");
        this.leftUpCardY = this.getIntProp(pp, "left.up.card.y");
        this.widthSpacing = this.getIntProp(pp, "width.spacing");
        this.heightSpacing = this.getIntProp(pp, "height.spacing");
        this.cardDetailPot = new Point(this.getIntProp(pp, "card.detail.btn.x"), this.getIntProp(pp, "card.detail.btn.y"));
        this.nextPagePot = new Point(this.getIntProp(pp, "next.page.btn.x"), this.getIntProp(pp, "next.page.btn.y"));
    }

    public void run() {
        List<Point> pots = this.getAllPotsInOnePage();
        Log.println(String.format("Page %d started.", startPageNumber));
        for (int i = this.startPageNumber; i < 781; i++) {
            for (Point pot : pots) {
                this.doOneCard(pot, i, pots.indexOf(pot) + 1);
            }
            // 整一页的卡牌立绘提取完成, 进入下一页
            Log.println(String.format("Page %d completed, now enter next page: %d", i, i + 1));
            this.click(this.nextPagePot);
            this.waitAndPrint(4);
        }
    }

    /**
     * 封装了提取一张卡牌立绘的全部流程.<p>
     * 调用此方法前, 屏幕需显示在卡牌列表页面, 方法结束后, 会返回列表页面.<p>
     *
     * @param pot      该卡牌在卡牌列表中的位置.
     * @param page     当前为第几页, 取值范围为: [1, 780].
     * @param location 当前卡牌在当前页中的位置, 左上 -> 右上 -> 左下 -> 右下, 可选值为: 1,2,3,4,5,6,7,8,9,10
     */
    public void doOneCard(Point pot, int page, int location) {
        // 截详情图1
        this.click(pot);
        String fmt = "%s/%d-%d-desc1.png";
        this.screenshot(String.format(fmt, this.path, page, location));

        // 截详情图2
        this.click(this.cardDetailPot);
        fmt = "%s/%d-%d-desc2.png";
        this.screenshot(String.format(fmt, this.path, page, location));

        // 截立绘
        this.click(this.pot1);
        // 此处需多等待一定时间.
        this.waitAndPrint(5);
        fmt = "%s/%d-%d.png";
        this.screenshot(String.format(fmt, this.path, page, location));
        this.back();
        this.back();
    }


    /**
     * 获取每一页中所有的卡牌点击位置.
     */
    private List<Point> getAllPotsInOnePage() {
        List<Point> res = new ArrayList<>();
        // 每行卡牌数量
        int num = 5;
        // 上排卡牌
        for (int i = 0; i < num; i++) {
            Point up = new Point(this.leftUpCardX + (i * this.widthSpacing), this.leftUpCardY);
            res.add(up);
        }
        // 下排卡牌
        for (int i = 0; i < num; i++) {
            Point down = new Point(this.leftUpCardX + (i * this.widthSpacing), this.leftUpCardY + this.heightSpacing);
            res.add(down);
        }
        return res;
    }

    /**
     * 点击方法.
     */
    private void click(Point pot) {
        String cmd = "cmd /k adb shell input tap %d %d";
        cmd = String.format(cmd, pot.x, pot.y);
        this.runAdbCmd(cmd);
        this.waitAndPrint(1);
        Log.println(String.format("Click at: (%d, %d).", pot.x, pot.y));
    }

    /**
     * 截取当前屏幕, 保存到指定位置.
     *
     * @param savePath 保存的完整路径.
     */
    private void screenshot(String savePath) {
        String cmd = "cmd /k adb exec-out screencap -p > %s";
        cmd = String.format(cmd, savePath);
        this.runAdbCmd(cmd);
        this.waitAndPrint(1);
        Log.println(String.format("Screenshot success, save location: %s.", savePath));
    }

    /**
     * 返回上一页.
     */
    private void back() {
        String cmd = "cmd /k adb shell input keyevent 4";
        this.runAdbCmd(cmd);
        this.waitAndPrint(1);
        Log.println("back.");
    }

    /**
     * 封装了ADB命令的执行方法.
     */
    private void runAdbCmd(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停指定的秒数并打印
     *
     * @param sleepMultiple 等待时间的倍数
     */
    private void waitAndPrint(int sleepMultiple) {
        Log.print("Waiting......");
        int time = this.sleep * sleepMultiple;
        for (int i = time; i > 0; i--) {
            try {
                Thread.sleep(1000);
                if (i == 1) {
                    Log.println(i);
                } else {
                    Log.print(i + " ");
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public int getIntProp(Properties pp, String propName) {
        return Integer.parseInt(pp.getProperty(propName));
    }
}
