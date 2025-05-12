package com.justdevthat.dispatcher.service.impl;

import com.justdevthat.dispatcher.controller.UpdateProcessor;
import com.justdevthat.dispatcher.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.justdevthat.RabbitQueue.ANSWER_MESSAGE;

/**
 * Читает ответы из Node-microservice
 * */

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
  private final UpdateProcessor updateProcessor;

  public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
    this.updateProcessor = updateProcessor;
  }

  @Override
  @RabbitListener(queues = ANSWER_MESSAGE)
  public void consume(SendMessage sendMessage) {
    updateProcessor.setView(sendMessage);
  }
}
