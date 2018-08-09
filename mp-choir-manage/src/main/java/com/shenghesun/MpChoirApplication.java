package com.shenghesun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
/**
 * 项目启动主类
 * @author kevin
 *
 */
@SpringBootApplication
@RestController
public class MpChoirApplication {

	public static void main(String[] args) {
		SpringApplication.run(MpChoirApplication.class, args);
	}
}
