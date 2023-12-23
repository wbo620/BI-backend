package com.ice.init.manager;

/**
 * User: hallen
 * Date: 2023/10/23
 * Time: 15:38
 */

import cn.hutool.core.util.StrUtil;
import com.ice.init.common.ErrorCode;
import com.ice.init.config.XfXhConfig;
import com.ice.init.exception.BusinessException;
import com.ice.init.listener.XfXhStreamClient;
import com.ice.init.listener.XfXhWebSocketListener;
import com.ice.init.model.dto.xfxh.MsgDTO;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

/**
 * 用于对接 AI 平台
 */
@Service
@Slf4j
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    @Resource
    private XfXhStreamClient xfXhStreamClient;

    @Resource
    private XfXhConfig xfXhConfig;

    /**
     * AI 对话
     *
     * @param message
     * @return
     */
    public String doChat2(long modelId, String message) {
        // 第三步，构造请求参数
        DevChatRequest devChatRequest = new DevChatRequest();
        // 模型id，尾后加L，转成long类型
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        // 第四步，获取响应结果
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        // 如果响应为null，就抛出系统异常，提示“AI 响应错误”
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }

    /**
     * 发送问题
     * XfxhManager
     * @param message 问题
     * @return 星火大模型的回答
     */
    public String doChat(Long id,String message) {
        // 如果是无效字符串，则不对大模型进行请求
        if (StrUtil.isBlank(message)) {
            return "无效问题，请重新输入";
        }
        // 获取连接令牌
        if (!xfXhStreamClient.operateToken(XfXhStreamClient.GET_TOKEN_STATUS)) {
            return "当前大模型连接数过多，请稍后再试";
        }

        // 创建消息对象
        MsgDTO msgDTO = MsgDTO.createUserMsg(message);
        // 创建监听器
        XfXhWebSocketListener listener = new XfXhWebSocketListener();
        // 发送问题给大模型，生成 websocket 连接
        WebSocket webSocket = xfXhStreamClient.sendMsg(UUID.randomUUID().toString().substring(0, 10), Collections.singletonList(msgDTO), listener);
        if (webSocket == null) {
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
            return "系统内部错误，请联系管理员";
        }
        try {
            int count = 0;
            // 为了避免死循环，设置循环次数来定义超时时长
            int maxCount = xfXhConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                if (listener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                return "大模型响应超时，请联系管理员";
            }
            // 响应大模型的答案
            return listener.getAnswer().toString();
        } catch (InterruptedException e) {
            log.error("错误：" + e.getMessage());
            return "系统内部错误，请联系管理员";
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
        }
    }
}