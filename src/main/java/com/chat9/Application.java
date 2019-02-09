package com.chat9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.chat9","org.n3r.idworker"})
//scan mybatis mapper route in com.chat9.mapper
@MapperScan(basePackages = "com.chat9.mapper")
public class Application {
   public static void main(String[] args){
      SpringApplication.run(Application.class,args);
   }
}
