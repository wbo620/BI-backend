package com.ice.init.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {
    @Resource
    private AiManager aiManager;

    @Test
    public void testAiManager() {
        String result = aiManager.doChat(1659171950288818178L, "周杰伦的歌");
        System.out.println(result);

    }
}