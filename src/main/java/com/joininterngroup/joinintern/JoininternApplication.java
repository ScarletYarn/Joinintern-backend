package com.joininterngroup.joinintern;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.joininterngroup.joinintern.mapper")
public class JoininternApplication {

	public static void main(String[] args) {
		SpringApplication.run(JoininternApplication.class, args);
	}

}
