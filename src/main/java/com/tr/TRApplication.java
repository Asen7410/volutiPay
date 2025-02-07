package com.tr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@Slf4j
@SpringBootApplication
@EnableScheduling
public class TRApplication {

    public static void main(String[] args) {

        SpringApplication.run(TRApplication.class, args);
        log.info("tr start");
        log.info("JVM initialized memory, usedMemory:{}MB, totalMemory:{}MB, maxMemory:{}MB",
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024,
                Runtime.getRuntime().totalMemory() / 1024 / 1024, Runtime.getRuntime().maxMemory() / 1024 / 1024);
        Runtime.getRuntime().addShutdownHook(new Thread("trShutdown") {
            @Override
            public void run() {
                log.info("tr shutdown");
            }
        });
    }

}
