package io.meowkita.backend.controller;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CounterController {

    @Autowired SimpMessagingTemplate simpMessagingTemplate;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private int counter = 0;

    public CounterController() {
        autoIncreaseCounter();
    }

    @MessageMapping("/counter/current")
    @SendTo("/counter/current")
    public int getCounterOnConnect() {
        return counter;
    }

    @MessageMapping("/counter/increase")
    @SendTo("/counter/current")
    public int increaseCounter() throws Exception {
        counter++;
        return counter;
    }

    public void autoIncreaseCounter() {
        executorService.submit(() -> {
            while(true){
                counter += new Random().nextInt(10);
                simpMessagingTemplate.convertAndSend("/ws/counter/current", counter);
                System.out.println("Automatic increase ... ");
                Thread.sleep(1000);
            }
        });
    }
}
