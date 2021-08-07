package io.study.batch.startbatchconf1.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
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
			.start(sampleStep1(null))
			.next(sampleStep2(null))
			.build();
	}

	@Bean
	@JobScope
	public Step sampleStep1(@Value("#{jobParameters[startDate]}") String startDate) {
		return stepBuilderFactory.get("sampleStep1")
			.tasklet((contribution, chunkContext) -> {
				log.info(" >> [실행] sampleStep1 ");
				log.info(">>> jobParameter = {}", startDate);
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	@JobScope
	public Step sampleStep2(@Value("#{jobParameters[startDate]}") String startDate) {
		return stepBuilderFactory.get("sampleStep2")
			.tasklet((contribution, chunkContext) -> {
				log.info(">>> [실행] sampleStep2 ");
				log.info(">>> jobParameter = {}", startDate);
				return RepeatStatus.FINISHED;
			})
			.build();
	}
}
