package com.my.mobilesafe.utils;

import java.util.regex.Pattern;

/**
 * Created by MY on 2016/10/12.
 */

public class RegexUtil {
    public final static String MOBILE_REGEX = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
    public final static String EMAIL_REGEX = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    public static boolean isMobileNum(String mobile) {
        mobile = mobile.replace(" ", "");
        return Pattern.matches(MOBILE_REGEX, mobile);
    }

    public static boolean isEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }
}
