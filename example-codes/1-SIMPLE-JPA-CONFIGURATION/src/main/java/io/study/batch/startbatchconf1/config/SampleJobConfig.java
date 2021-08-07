package io.study.batch.startbatchconf1.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SampleJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job sampleJob(){
		return jobBuilderFactory.get("sampleJob")
			.start(sampleStep1())
			.build();
	}

	@Bean
	public Step sampleStep1() {
		return stepBuilderFactory.get("sampleStep1")
			.tasklet((contribution, chunkContext) -> {
				log.info(" >> Step1 실행됨 ");
				return RepeatStatus.FINISHED;
			})
			.build();
	}
}
