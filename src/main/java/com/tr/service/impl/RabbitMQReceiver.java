package com.tr.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.config.RabbitMQConfig;
import com.tr.dto.HbMqMessageDto;
import com.tr.service.RedEnvelopeInfoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import com.rabbitmq.client.Channel;

@Service
public class RabbitMQReceiver {

    @Autowired
    private RedEnvelopeInfoService redEnvelopeInfoService;

    @Component
    public class RabbitMQConsumer {

        @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
        public void receiveMessage(String message,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
            try {
                // 使用 ObjectMapper 将消息从 JSON 字符串反序列化为 HbMqMessageDto 对象
                ObjectMapper objectMapper = new ObjectMapper();
                HbMqMessageDto messageDto = objectMapper.readValue(message, HbMqMessageDto.class);

                // 从 messageDto 中获取数据，进行后续的业务处理
                Long userId = messageDto.getUserId();
                Long redEnvelopeId = messageDto.getRedEnvelopeId();
                BigDecimal amount = messageDto.getAmount();

                redEnvelopeInfoService.updateInfo(userId, redEnvelopeId, amount);

                // 手动确认消息已处理
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                try {
                    // 发生异常，拒绝消息，重新放回队列
                    channel.basicNack(deliveryTag, false, true);
                } catch (Exception nackException) {
                    throw new RuntimeException("消息拒绝失败", nackException);
                }
                throw new RuntimeException("消息消费失败", e);
            }
        }
    }
}
