package com.ice.init.bizmq;

import com.alibaba.fastjson.JSON;
import com.ice.init.common.ErrorCode;
import com.ice.init.constant.BiMqConstant;
import com.ice.init.exception.BusinessException;
import com.ice.init.manager.AiManager;
import com.ice.init.model.entity.Chart;
import com.ice.init.model.enums.ChartStatus;
import com.ice.init.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    //绑定队列,确认方式改为手动
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        if (StringUtils.isBlank(message)) {
            //如果消息为空,消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "信息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。(为了防止同一个任务被多次执行)
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        // 把任务状态改为执行中
        updateChart.setStatus(ChartStatus.RUNNING.getValue());
        boolean b = chartService.updateById(updateChart);
        // 如果提交失败(一般情况下,更新失败可能意味着你的数据库出问题了)
        if (!b) {
            // 如果更新图表成功状态失败，拒绝消息并处理图表更新错误
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return;
        }

        String userInput = buildUserInput(chart);
        //调用Ai
        String result = aiManager.doChat(null, userInput.toString());
        //对生成结果分割
        String[] splits = result.split("分割标记：》》》》》");
        if (splits.length < 3) {
            // 如果更新图表成功状态失败，拒绝消息并处理图表更新错误
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        genChart = genChartCodeFilter(genChart);
        // 调用AI得到结果之后,再更新一次
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);

        updateChartResult.setStatus(ChartStatus.SUCCEED.getValue());
        boolean updateResult = chartService.updateById(updateChartResult);

        if (!updateResult) {
            // 如果更新图表成功状态失败，拒绝消息并处理图表更新错误
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
        //消息确认
        channel.basicAck(deliveryTag, false);
    }

    /**
     * 构建用户输入
     *
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        //构建用户输入
        StringBuilder userInput = new StringBuilder();
        //userInput.append("你是一个专业的数据分析师和前端工程师,接下来我会给出分析目标的csv格式的原始数据，用,作为分隔符,你的工作是分析给出的数据,使用合适的数据列生成正确的图表代码,并给出分析结论," +
        //                "请使用纯文本输出，图表代码使用前端 Echarts V5.0的格式的 option 标记包含的配置对象js代码\n" );
        //userInput.append("下面我将要给你一份CSV格式的原始数据，其中以逗号作为分隔符。" +
        //        "你的任务是利用你的数据分析和前端工程技能，根据适当的数据列生成正确的图表代码，使用前端 Echarts V5.0的格式的option标记包含的配置对象JS代码。请确保输出为纯文本，并附上你的分析结论。\n" );
        //userInput.append("你是一个专业的数据分析师和前端工程师,接下来我会给出分析需求的csv格式的原始数据\n");
        //拼接需求和目标
        userInput.append("分析需求: ").append("\n");
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += ",使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据:").append("\n");
        //压缩后的数据
        userInput.append(csvData).append("\n");

        userInput.append("请根据这两部分内容，按照以下指定格式生成：\n" +
                "分割标记：》》》》》\n" +
                "{纯文本输出，前端 Echarts V5.0的格式的 option 标记包含的配置对象JS代码，生成的键都要使用双引号包围，合理地将数据进行可视化，不要生成任何多余的内容，比如注释，Markdown标记等}\n" +
                "\n" +
                "分割标记：》》》》》\n" +
                "这里输出分析结论，越详细越好，不要生成多余的注释");

        return userInput.toString();
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

    /**
     * 使用正则表达式过滤生成的图表的代码
     *
     * @param genChart
     * @return 经过json处理后的图表代码
     */
    private String genChartCodeFilter(String genChart) {
        // 定义正则表达式,来过滤生成的结果
        //过滤```javascript标签
        String extractedData = genChart;
        String regex = "```javascript\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(genChart);

        if (matcher.find()) {
            extractedData = matcher.group(1);
        }
        ////过滤option标签
        String regex2 = "option\\s*=\\s*\\{(.*?)\\};";
        Pattern pattern2 = Pattern.compile(regex2, Pattern.DOTALL);
        Matcher matcher2 = pattern2.matcher(extractedData);

        if (matcher2.find()) {
            extractedData = "{" + matcher2.group(1) + "}";
        }
        extractedData = JSON.parse(extractedData).toString();
        //检查图表代码是否是以{}为开始结束标记
        // 移除字符串中的空格和换行符
        extractedData = extractedData.replaceAll("\\s", "");
        if (!extractedData.startsWith("{") && extractedData.endsWith("}")) {
            log.error("图表代码错误");
        }
        return extractedData;
    }
}
