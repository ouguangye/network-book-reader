package com.example.networkbookreader.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpUnit {
    // 提取字符串中的数字
    public int getContainsNum(String str){
        String regExp="[^0-9]"; // 反向字符集。匹配未包含的数字，替换着里面的数字
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(str);
        String num = matcher.replaceAll("").trim();
        if (num.equals("")) return 0;
        return Integer.parseInt(num);
    }
}
