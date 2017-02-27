package com.netease.frescodemo.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by MrDong on 2017/2/27.
 */

public class StringUtiles {
    private static final String STR_EMPTY = "";
    private static final String STR_MD5 = "MD5";

    /**
     * 超过１０　则省略
     */
    public static String substringUserName(String str) {
        if (str.length() > 10) {
            return str.substring(0, 10) + "...";
        }
        return str;
    }

    public static float getTextNum(Editable s) {
        float charNum = 0;
        final int length = s.length();
        char[] dest = new char[length];
        s.getChars(0, length, dest, 0);
        for (char c : dest) {
            if (c > 127) {
                charNum++;
            } else {
                charNum += 0.5;
            }
        }
        return charNum;
    }

    public static float getTextNum(String src) {
        float charNum = 0;
        final int length = src.length();
        char[] dest = new char[length];
        src.getChars(0, length, dest, 0);
        for (char c : dest) {
            if (c > 127) {
                charNum++;
            } else {
                charNum += 0.5;
            }
        }
        return charNum;
    }

    public static String getTextNum(String src, int textLength) {
        StringBuilder sBuilder = new StringBuilder();
        float charNum = 0;
        final int length = src.length();
        char[] dest = new char[length];
        src.getChars(0, length, dest, 0);
        for (char c : dest) {
            if (c > 127) {
                charNum++;
            } else {
                charNum += 0.5;
            }
            sBuilder.append(c);
            if (charNum >= textLength) {
                break;
            }
        }
        return sBuilder.toString();
    }

    public static String parseHtml(String source) {
        if (!TextUtils.isEmpty(source)) {
            return source.replace("&amp;", "&").replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "\'").replace("&nbsp;", " ").replace("&#39", "\'").replace("&#96", "`");
        }
        if (null == source) {
            return "";
        }
        return source;
    }

    public static String markStr(String str) {
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        return "\"" + str + "\"";
    }

    public static String getUTF8(String content) {
        if (!TextUtils.isEmpty(content)) {
            try {
                return URLEncoder.encode(content, "utf-8");
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return content;
    }

    public static String getMD5(String content) {
        if (content != null && !"".equals(content)) {
            try {
                MessageDigest digest = MessageDigest.getInstance(STR_MD5);
                digest.update(content.getBytes());
                return getHashString(digest);
            } catch (NoSuchAlgorithmException ignored) {
            }
            return STR_EMPTY;
        }
        return "";
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder(STR_EMPTY);
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }



    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][34587]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    @SuppressWarnings("deprecation")
    public static SpannableString getSp(Context context, int srcId, String title ){
        Drawable dImage = context.getResources().getDrawable(srcId);
        if (dImage != null) {
            dImage.setBounds(0, 0, dImage.getIntrinsicWidth(), dImage.getIntrinsicHeight());
        }
        SpannableString sp = new SpannableString("     " + title);
        ImageSpan imageSpan = new ImageSpan(dImage, ImageSpan.ALIGN_BOTTOM);
        sp.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }
}
