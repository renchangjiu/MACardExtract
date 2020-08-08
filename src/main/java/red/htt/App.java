package red.htt;

import red.htt.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    private static final int SLEEP = 3000;

    /**
     * 卡牌详情页里, 点击进入立绘页的坐标.
     */
    private static final Point POT1 = new Point(480, 500);

    /**
     * 立绘保存位置(不能包含空格).
     */
    private static final String path = "C:/Users/su/Pictures/ma";

    /**
     *
     */
    public static void main(String[] args) throws Exception {
        App app = new App();
        List<Point> pots = app.getAllPotsInOnePage();
        for (Point pot : pots) {
            app.click(pot);
            // 进入立绘页, 考虑到网络因素, 需要多等待一定时间.
            app.click(POT1);
            Thread.sleep(10000);
            app.screenshot(1, pots.indexOf(pot) + 1);
            app.back();
            app.back();
        }
        System.out.println("Hello World!");
    }

    /**
     * 获取每一页中所有的卡牌点击位置.
     */
    public List<Point> getAllPotsInOnePage() throws Exception {
        List<Point> res = new ArrayList<>();
        // 左上第一张卡牌的点击位置
        int x = 345;
        int y = 378;
        // 每张卡牌的横向距离, 下为纵向距离
        int w = 295;
        int h = 400;
        // 每行卡牌数量
        int num = 5;
        // 上排卡牌
        for (int i = 0; i < num; i++) {
            Point up = new Point(x + (i * w), y);
            res.add(up);
        }
        // 下排卡牌
        for (int i = 0; i < num; i++) {
            Point down = new Point(x + (i * w), 378 + h);
            res.add(down);
        }
        return res;
    }

    /**
     * 点击方法.
     */
    public void click(Point pot) throws Exception {
        String cmd = "cmd /k adb shell input tap %d %d";
        cmd = String.format(cmd, pot.x, pot.y);
        Runtime.getRuntime().exec(cmd);
        Thread.sleep(SLEEP);
        Log.println(String.format("Click at: (%d, %d).", pot.x, pot.y));
    }

    /**
     * 截图方法.
     *
     * @param page     当前为第几页.
     * @param location 当前卡牌在当前页中的位置, 左上 -> 右上 -> 左下 -> 右下, 可选值为: 1,2,3,4,5,6,7,8,9,10
     */
    public void screenshot(int page, int location) throws Exception {
        String cmd = "cmd /k adb exec-out screencap -p > %s";
        String p = path + "/" + page + "-" + location + ".png";
        cmd = String.format(cmd, p);
        Runtime.getRuntime().exec(cmd);
        Thread.sleep(SLEEP);
        Log.println(String.format("Screenshot success, save location: %s.", p));
    }

    /**
     * 返回上一页.
     */
    public void back() throws Exception {
        String cmd = "cmd /k adb shell input keyevent 4";
        Runtime.getRuntime().exec(cmd);
        Thread.sleep(SLEEP);
        Log.println("back.");
    }
}
