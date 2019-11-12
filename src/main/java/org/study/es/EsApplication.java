package org.study.es;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.es.util.EsNoUtil;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author lipo
 * @version v1.0
 * @date 2019-11-11 16:22
 */
@SpringBootApplication
@RestController
@Slf4j
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }

    @Autowired
    private EsNoUtil esNoUtil;

    /**
     * http://localhost:8080/orderNo
     * @author lipo
     * @date 2019-11-12 10:49
     */
    @GetMapping("orderNo")
    public Long orderNo() {
        return esNoUtil.nextOrderNo();
    }

    @GetMapping("orderNoMany")
    public String orderNoMany() throws InterruptedException {
        final int count = 1000;

        LocalDateTime begin = LocalDateTime.now();

        int i = 0;
        for (; i < count; i++) {
            esNoUtil.nextOrderNo();
        }

        //PT1M40.807S，平均一个id时间0.1秒
        System.out.println(Duration.between(begin, LocalDateTime.now()));

        return "ok";
    }

    @GetMapping("commonNo")
    public Long commonNo() {
        return esNoUtil.nextCommonNo();
    }


}
