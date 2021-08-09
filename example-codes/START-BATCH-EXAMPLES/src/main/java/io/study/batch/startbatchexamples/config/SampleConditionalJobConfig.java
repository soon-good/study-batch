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
public class SampleConditionalJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job sampleConditionalJob(){
		return jobBuilderFactory.get("sampleConditionalJob")
			.start(sampleConditionalStep1())
				.on("FAILED") // FAILED 일 경우
				.to(sampleConditionalStep3())	// STEP3 로 이동
				.on("*") // STEP3의 결과와는 무관하게
				.end()	// STEP3 이후에는 Flow 를 종료
			.from(sampleConditionalStep1())
				.on("*") // FAILED 외의 모든 경우에
				.to(sampleConditionalStep2())	// step2() 를 실행한다.
				.next(sampleConditionalStep3()) // step2의 결과와 관계 없이 강제로 step3 를 실행
				.on("*") // step3 의 결과와는 무관하게
				.end()// Flow 를 종료시킨다. (FlowBuilder)
			.end()// Job을 종료시킨다. (FlowJobBuilder)
			.build();
	}

	@Bean
	@JobScope
	public Step sampleConditionalStep1() {
		return stepBuilderFactory.get("sampleConditionalStep1")
			.tasklet((contribution, chunkContext) -> {
				log.info("[step1 실행] sampleConditionalStep1 >>");
				// contribution.setExitStatus(ExitStatus.FAILED);
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	@JobScope
	public Step sampleConditionalStep2() {
		return stepBuilderFactory.get("sampleConditionalStep2")
			.tasklet((contribution, chunkContext) -> {
				log.info("[step2 실행] sampleConditionalStep2 >>");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	@JobScope
	public Step sampleConditionalStep3() {
		return stepBuilderFactory.get("sampleConditionalStep3")
			.tasklet((contribution, chunkContext) -> {
				log.info("[step3 실행] sampleConditionalStep3 >>");
				return RepeatStatus.FINISHED;
			})
			.build();
	}
}
