package com.yupi.yuaibackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yupi.yuaibackend.mapper")
public class YuAiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuAiBackendApplication.class, args);
	}

}
