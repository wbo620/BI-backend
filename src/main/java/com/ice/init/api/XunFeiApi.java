//package com.ice.init.api;
//
//import cn.hutool.http.HttpRequest;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.ice.init.manager.BigModelNew;
//import okhttp3.HttpUrl;
//import okhttp3.Response;
//import okhttp3.WebSocket;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.IOException;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * User: hallen
// * Date: 2023/10/23
// * Time: 15:00
// */
//
//public class XunFeiApi {
//    public static final String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
//    public static final String appid = "ef29e67a";
//    public static final String apiSecret = "YjE4NTk4ZTY1ZGNjNjlkMGE1ZDk3NjVm";
//    public static final String apiKey = "89b99944d0590b1b0fa339c26cc5662b";
//    public static void main(String[] args) {
//
//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//                "分析需求：\n" +
//                "{数据分析的需求或者目标}\n" +
//                "原始数据：\n" +
//                "{csv格式的原始数据，用,作为分隔符}\n" +
//                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//                "【【【【【\n" +
//                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//                "【【【【【\n" +
//                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
//        String url = "https://api.openai.com/v1/chat/completions";
//        Map<String, Object> hashMap = new HashMap<String, Object>();
//        hashMap.put("message", prompt);
//        String jsonStr = JSONUtil.toJsonStr(hashMap);
//        System.out.println(jsonStr);
//        String body = HttpRequest.post(url)
//                .header("Authorization", "Bearer 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")
//                .body(jsonStr)
//                .execute()
//                .body();
//
//
//    }
//    /**
//     * AI 对话（需要自己创建请求响应对象）
//     *
//     * @param request
//     * @param openAiApiKey
//     * @return
//     */
//    //线程来发送音频与参数
//    class MyThread extends Thread {
//        private WebSocket webSocket;
//
//        public MyThread(WebSocket webSocket) {
//            this.webSocket = webSocket;
//        }
//
//        public void run() {
//            try {
//                JSONObject requestJson=new JSONObject();
//
//                JSONObject header=new JSONObject();  // header参数
//                header.put("app_id",appid);
//                header.put("uid", UUID.randomUUID().toString().substring(0, 10));
//
//                JSONObject parameter=new JSONObject(); // parameter参数
//                JSONObject chat=new JSONObject();
//                chat.put("domain","generalv2");
//                chat.put("temperature",0.5);
//                chat.put("max_tokens",4096);
//                parameter.put("chat",chat);
//
//                JSONObject payload=new JSONObject(); // payload参数
//                JSONObject message=new JSONObject();
//                JSONArray text=new JSONArray();
//
//                //// 历史问题获取
//                //if(historyList.size()>0){
//                //    for(RoleContent tempRoleContent:historyList){
//                //        text.add(JSON.toJSON(tempRoleContent));
//                //    }
//                //}
//
//                //// 最新问题
//                BigModelNew.RoleContent roleContent=new BigModelNew.RoleContent();
//                roleContent.role="user";
//                roleContent.content=prompt;
//                text.add(JSON.toJSON(roleContent));
//                //historyList.add(roleContent);
//
//
//                message.put("text",text);
//                payload.put("message",message);
//
//
//                requestJson.put("header",header);
//                requestJson.put("parameter",parameter);
//                requestJson.put("payload",payload);
//                System.err.println(requestJson); // 可以打印看每次的传参明细
//                webSocket.send(requestJson.toString());
//                // 等待服务端返回完毕后关闭
//                while (true) {
//                    // System.err.println(wsCloseFlag + "---");
//                    Thread.sleep(200);
//                    if (wsCloseFlag) {
//                        break;
//                    }
//                }
//                webSocket.close(1000, "");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onOpen(WebSocket webSocket, Response response) {
//        super.onOpen(webSocket, response);
//        System.out.print("大模型：");
//        BigModelNew.MyThread myThread = new BigModelNew.MyThread(webSocket);
//        myThread.start();
//    }
//
//    @Override
//    public void onMessage(WebSocket webSocket, String text) {
//        // System.out.println(userId + "用来区分那个用户的结果" + text);
//        BigModelNew.JsonParse myJsonParse = gson.fromJson(text, BigModelNew.JsonParse.class);
//        if (myJsonParse.header.code != 0) {
//            System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
//            System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
//            webSocket.close(1000, "");
//        }
//        //List<Text> textList = myJsonParse.payload.choices.text;
//        //for (Text temp : textList) {
//        //    System.out.print(temp.content);
//        //    totalAnswer=totalAnswer+temp.content;
//        //}
//        //if (myJsonParse.header.status == 2) {
//        //    // 可以关闭连接，释放资源
//        //    System.out.println();
//        //    System.out.println("*************************************************************************************");
//        //    if(canAddHistory()){
//        //        RoleContent roleContent=new RoleContent();
//        //        roleContent.setRole("assistant");
//        //        roleContent.setContent(totalAnswer);
//        //        historyList.add(roleContent);
//        //    }else{
//        //        historyList.remove(0);
//        //        RoleContent roleContent=new RoleContent();
//        //        roleContent.setRole("assistant");
//        //        roleContent.setContent(totalAnswer);
//        //        historyList.add(roleContent);
//        //    }
//        //    wsCloseFlag = true;
//        //    totalFlag=true;
//        //}
//    }
//
//    @Override
//    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//        super.onFailure(webSocket, t, response);
//        try {
//            if (null != response) {
//                int code = response.code();
//                System.out.println("onFailure code:" + code);
//                System.out.println("onFailure body:" + response.body().string());
//                if (101 != code) {
//                    System.out.println("connection failed");
//                    System.exit(0);
//                }
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//
//    // 鉴权方法
//    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
//        URL url = new URL(hostUrl);
//        // 时间
//        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String date = format.format(new Date());
//        // 拼接
//        String preStr = "host: " + url.getHost() + "\n" +
//                "date: " + date + "\n" +
//                "GET " + url.getPath() + " HTTP/1.1";
//        // System.err.println(preStr);
//        // SHA256加密
//        Mac mac = Mac.getInstance("hmacsha256");
//        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
//        mac.init(spec);
//
//        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
//        // Base64加密
//        String sha = Base64.getEncoder().encodeToString(hexDigits);
//        // System.err.println(sha);
//        // 拼接
//        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
//        // 拼接地址
//        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
//                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
//                addQueryParameter("date", date).//
//                addQueryParameter("host", url.getHost()).//
//                build();
//
//        // System.err.println(httpUrl.toString());
//        return httpUrl.toString();
//    }
//
//    //返回的json结果拆解
//    class JsonParse {
//        BigModelNew.Header header;
//        BigModelNew.Payload payload;
//    }
//
//    class Header {
//        int code;
//        int status;
//        String sid;
//    }
//
//    class Payload {
//        BigModelNew.Choices choices;
//    }
//
//    class Choices {
//        List<BigModelNew.Text> text;
//    }
//
//    class Text {
//        String role;
//        String content;
//    }
//    class RoleContent{
//        String role;
//        String content;
//
//        public String getRole() {
//            return role;
//        }
//
//        public void setRole(String role) {
//            this.role = role;
//        }
//
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//    }
//}
