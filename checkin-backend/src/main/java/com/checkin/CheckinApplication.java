package com.checkin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 打卡签到系统启动类
 */
@SpringBootApplication
@MapperScan("com.checkin.mapper")
public class CheckinApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckinApplication.class, args);
        System.out.println("✅ 打卡签到系统启动成功！");
    }
}
