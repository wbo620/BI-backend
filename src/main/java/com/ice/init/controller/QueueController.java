package com.ice.init.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: hallen
 * Date: 2023/10/28
 * Time: 20:40
 */

/**
 * 队列 测试
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({ "dev", "local" })
public class QueueController {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
}
