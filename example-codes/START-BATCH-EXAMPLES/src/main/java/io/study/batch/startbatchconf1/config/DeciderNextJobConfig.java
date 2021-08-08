package io.study.batch.startbatchconf1.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.study.batch.startbatchconf1.OddDecider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DeciderNextJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job deciderNextJob(){
		return jobBuilderFactory.get("deciderNextJob")
			.start(startStep())
			.next(decider())
			.from(decider())
				.on("홀수")
				.to(oddStep())
			.from(decider())
				.on("짝수")
				.to(evenStep())
			.end()
			.build();
	}

	@Bean
	public Step startStep() {
		return stepBuilderFactory.get("startStep")
			.tasklet((contribution, chunkContext) -> {
				log.info(">>> [startStep]");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public JobExecutionDecider decider() {
		return new OddDecider();
	}

	@Bean
	public Step oddStep() {
		return stepBuilderFactory.get("oddStep")
			.tasklet((contribution, chunkContext) -> {
				log.info("홀수스텝>>>");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step evenStep() {
		return stepBuilderFactory.get("evenStep")
			.tasklet((contribution, chunkContext) -> {
				log.info("짝수스텝>>>");
				return RepeatStatus.FINISHED;
			})
			.build();
	}
}
