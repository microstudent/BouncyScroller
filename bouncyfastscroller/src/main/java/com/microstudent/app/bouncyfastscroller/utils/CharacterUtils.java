package com.microstudent.app.bouncyfastscroller.utils;

/**
 *
 * Created by MicroStudent on 2016/4/25.
 */
public class CharacterUtils {
    public static boolean isChinese(char c) {
        return Character.toString(c).matches("[\\u4E00-\\u9FA5]+");
    }
}
