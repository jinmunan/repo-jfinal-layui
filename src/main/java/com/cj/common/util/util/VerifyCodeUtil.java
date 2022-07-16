package com.cj.common.util.util;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 验证码
 *
 * @author THINKPAD
 */
public class VerifyCodeUtil {

    /**
     * 随机函数
     */
    private static Random random = new Random();

    /**
     * 输出验证码图片到页面
     *
     * @param response
     * @param type     1:数字验证码；2：算式验证码
     */
    public static String createImage(HttpServletResponse response, int type) {
        Map<String, Object> map = createImage(80, 30, type);
        BufferedImage image = (BufferedImage) map.get("image");
        String verityCode = map.get("verityCode").toString();
        //向响应体输出图片
        if (response != null) {
            ServletOutputStream out;
            try {
                out = response.getOutputStream();
                ImageIO.write(image, "jpeg", out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return verityCode;
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    public static Color createColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 创建4位数的验证码图片
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @param type   1:数字验证码，2：算式验证码
     * @return
     */
    public static Map<String, Object> createImage(int width, int height, int type) {
        Map<String, Object> result = new HashMap<>();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();

        // 设定背景色
        g.setColor(createColor(220, 250));
        g.fillRect(0, 0, width, height);
        // 设定字体
        g.setFont(new Font("Times New Roman", Font.PLAIN, 24));

        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(createColor(160, 200));
        for (int i = 0; i < 155; i++) {
            drawLine(width, height, g);
        }

        // 取随机产生的认证码(4位验证码)
        String vCode = "";
        char[] code = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        if (type == 1) {
            for (int i = 0; i < 4; i++) {
                vCode += drawString(g, i, code);
            }
        } else {
            //算式验证码
            vCode = drawString(g);
        }

        result.put("verityCode", vCode);
        result.put("image", image);
        return result;
    }

    /**
     * 画干扰线
     *
     * @param width
     * @param height
     * @param g
     */
    private static void drawLine(int width, int height, Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(12);
        int yl = random.nextInt(12);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 画验证码
     *
     * @param g
     * @param i
     * @param code
     */
    private static String drawString(Graphics g, int i, char[] code) {
        g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110),
                20 + random.nextInt(110)));
        if (i < 2) {// 字母
            String a = code[random.nextInt(26)] + "";
            // 将认证码显示到图象中
            g.drawString(a, (18 * i) + 9, 26);
            return a;
        } else {// 数字
            String rand = String.valueOf(random.nextInt(10));
            // 将认证码显示到图象中
            g.drawString(rand, (18 * i) + 9, 26);
            return rand;
        }
    }

    /**
     * 算式验证码
     *
     * @param g
     * @return
     */
    private static String drawString(Graphics g) {
        int sRand = 0;//算法结果
        int num1 = 0;
        int num2 = 0;
        String s = "+";
        String[] code = {"+", "-"};
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110),
                    20 + random.nextInt(110)));
            if (i == 0) {
                num1 = random.nextInt(10);
                g.drawString(num1 + "", 18 * i + 9, 26);
            } else if (i == 1) {// + -
                s = code[random.nextInt(2)];
                g.drawString(s, 18 * i + 9, 26);
            } else if (i == 2) {
                num2 = random.nextInt(10);
                g.drawString(num2 + "", 18 * i + 9, 26);
            } else {
                g.drawString("=", 18 * i + 9, 26);
            }
        }

        //计算结果
        if ("+".equals(s)) {
            sRand = num1 + num2;
        } else {
            sRand = num1 - num2;
        }

        return sRand + "";
    }
}
