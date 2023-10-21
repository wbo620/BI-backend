package com.ice.init.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ice.init.model.dto.chart.ChartQueryRequest;
import com.ice.init.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ice.init.model.entity.Post;

/**
* @author hallen
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-10-21 16:12:13
*/
public interface ChartService extends IService<Chart> {

}
