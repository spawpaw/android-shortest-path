package com.spawpaw.util;

import java.util.Random;

/**
 * Created by CDFAE1CC on 2016.12.16.
 * 工具类，用于从枚举类中随机选取枚举值
 */
public class Enums {

    /**
     * 初始化随机数种子
     */
    private static Random rand = new Random(System.currentTimeMillis());

    public static <T extends Enum<T>> T random(Class<T> ec) {
        return random(ec.getEnumConstants());
    }

    public static <T> T random(T[] values) {
        return values[rand.nextInt(values.length)];
    }
}
