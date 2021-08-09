package io.study.batch.startbatchexamples.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SampleNextJob1Config {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job sampleNextJob1(){
		return jobBuilderFactory.get("sampleNextJob1")
			.start(sampleNextStep1())
			.next(sampleNextStep2())
			.next(sampleNextStep3())
			.build();

	}

	@Bean
	@JobScope
	public Step sampleNextStep1() {
		return stepBuilderFactory.get("sampleNextStep1")
			.tasklet((contribution, chunkContext) -> {
				log.info("[step1] >>> step1 이 실행되었습니다.");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	@JobScope
	public Step sampleNextStep2() {
		return stepBuilderFactory.get("sampleNextStep2")
			.tasklet((contribution, chunkContext) -> {
				log.info("[step2] >>> step2 가 실행되었습니다.");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	@JobScope
	public Step sampleNextStep3() {
		return stepBuilderFactory.get("sampleNextStep3")
			.tasklet((contribution, chunkContext) -> {
				log.info("[step3] >>> step3 이 실행되었습니다.");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

}
