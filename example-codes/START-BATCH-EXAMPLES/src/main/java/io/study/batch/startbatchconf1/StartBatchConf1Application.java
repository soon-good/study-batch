package io.study.batch.startbatchconf1;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class StartBatchConf1Application {

	public static void main(String[] args) {
		SpringApplication.run(StartBatchConf1Application.class, args);
	}

}