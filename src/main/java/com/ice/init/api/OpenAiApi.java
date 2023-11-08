package com.ice.init.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * User: hallen
 * Date: 2023/10/23
 * Time: 15:00
 */

public class OpenAiApi {
    public static void main(String[] args) {

        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        String url = "https://api.openai.com/v1/chat/completions";
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("message", prompt);
        String jsonStr = JSONUtil.toJsonStr(hashMap);
        System.out.println(jsonStr);
        String body = HttpRequest.post(url)
                .header("Authorization", "Bearer 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")
                .body(jsonStr)
                .execute()
                .body();


    }
    /**
     * AI 对话（需要自己创建请求响应对象）
     *
     * @param request
     * @param openAiApiKey
     * @return
     */
    //public CreateChatCompletionResponse createChatCompletion(CreateChatCompletionRequest request, String openAiApiKey) {
    //    if (StringUtils.isBlank(openAiApiKey)) {
    //        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未传 openAiApiKey");
    //    }
    //    String url = "https://api.openai.com/v1/chat/completions";
    //    String json = JSONUtil.toJsonStr(request);
    //    String result = HttpRequest.post(url)
    //            .header("Authorization", "Bearer " + openAiApiKey)
    //            .body(json)
    //            .execute()
    //            .body();
    //    return JSONUtil.toBean(result, CreateChatCompletionResponse.class);
    //}
}
