package io.study.batch.startbatchconf1;

import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OddDecider implements JobExecutionDecider {
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		Random random = new Random();

		int num = random.nextInt(10)+1;
		log.info("[난수발생기] random.nextInt(10) + 1 = {}", num);

		if(num % 2 == 1){
			return new FlowExecutionStatus("홀수");
		}
		else{
			return new FlowExecutionStatus("짝수");
		}
	}
}
