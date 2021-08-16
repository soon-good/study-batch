package io.study.batch.startbatchexamples.simple_job;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.study.batch.startbatchexamples.jobs.simple_job.SimpleListItemJob1;

@SpringBootTest
public class SimpleJob1Test {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private SimpleListItemJob1 jobConfig1;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");

	@Test
	@DisplayName("잡런처_실행해볼까")
	public void 잡런처_실행해볼까(){
		final JobParameters jobParameters = new JobParametersBuilder()
			.addString("requestId", OffsetDateTime.now().format(formatter))
			.toJobParameters();

		try {
			jobLauncher.run(jobConfig1.simpleListJob1(), jobParameters);
		} catch (
			JobExecutionAlreadyRunningException | JobRestartException |
			JobInstanceAlreadyCompleteException | JobParametersInvalidException e
		) {
			e.printStackTrace();
		}
	}
}
