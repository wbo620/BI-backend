package com.ice.init.model.enums;

/**
 * User: hallen
 * Date: 2023/10/28
 * Time: 19:47
 */

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义图表状态码
 */
public enum ChartStatus {

    SUCCEED("succeed", "成功"),
    WAIT("wait", "等待"),
    RUNNING("running", "生成中"),
    FAILED("failed", "失败");
    private final String text;

    private final String value;

    ChartStatus(String value, String text) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ChartStatus chartStatus(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ChartStatus status : ChartStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}
