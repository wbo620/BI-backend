package com.ice.init.model.dto.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hallen
 * Date: 2023/11/6
 * Time: 21:50
 */
class maiin {
    public static void main(String[] args) {
        String data = "```javascript\n" +
                "option = {\n" +
                "    xAxis: {\n" +
                "        type: 'category',\n" +
                "        data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15']\n" +
                "    },\n" +
                "    yAxis: {\n" +
                "        type: 'value'\n" +
                "    },\n" +
                "    series: [{\n" +
                "        data: [10, 23, 33, 45, 11, 68, 65, 11, 34, 114, 55, 22, 26, 160, 171],\n" +
                "        type: 'line'\n" +
                "    }]\n" +
                "};\n" +
                "```";

        // 定义正则表达式
        String regex = "```javascript\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(data);
        String extractedData = "";
        if (matcher.find()) {
            extractedData = matcher.group(1);
            System.out.println(extractedData);
        } else {
            System.out.println("未找到匹配的数据");
        }
        String regex2 = "option\\s*=\\s*\\{(.*?)\\};";
        Pattern pattern2 = Pattern.compile(regex2, Pattern.DOTALL);
        Matcher matcher2 = pattern2.matcher(extractedData);

        if (matcher2.find()) {
            extractedData = "{"+matcher2.group(1)+"}";
            System.out.println(extractedData);
        } else {
            System.out.println("未找到匹配的数据");
        }

    }

}
