package com.ice.init.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ice.init.constant.CommonConstant;
import com.ice.init.mapper.ChartMapper;

import com.ice.init.model.dto.chart.ChartQueryRequest;
import com.ice.init.model.entity.Chart;
import com.ice.init.service.ChartService;
import com.ice.init.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hallen
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2023-10-21 16:12:13
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {


}




