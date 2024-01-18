package com.ice.init.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ice.init.model.entity.Chart;

import java.util.Date;

/**
 * @author hallen
 * @description 针对表【chart(图表信息表)】的数据库操作Mapper
 * @createDate 2023-10-21 16:12:13
 * @Entity com.ice.init.model.entity.Chart
 */
public interface ChartMapper extends BaseMapper<Chart> {

    /**
     * TODO 动态根据id建立数据表
     */
    default void buildDataBaseByChartId(Chart chart) {

        Long id = chart.getId();
        String goal = chart.getGoal();
        String chartData = chart.getChartData();
        String chartType = chart.getChartType();
        String genChart = chart.getGenChart();
        String genResult = chart.getGenResult();
        Long userId = chart.getUserId();
        Date createTime = chart.getCreateTime();
        Date updateTime = chart.getUpdateTime();
        Integer isDelete = chart.getIsDelete();
        String name = chart.getName();
        String sql = "create table" + id;
    }

}




