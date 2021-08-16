package io.study.batch.startbatchexamples.jobs.simple_job;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.study.batch.startbatchexamples.book.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SimpleListJob1 {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean(name="simpleListJob1")
	public Job simpleListJob1(){
		return jobBuilderFactory.get("simpleListJob1")
			.start(simpleStep1(null))
			.build();
	}

	@JobScope
	@Bean(name = "simpleStep1")
	public Step simpleStep1(@Value("#{jobParameters[requestId]}") String requestId) {
		return stepBuilderFactory.get("simpleStep1")
			.<Book, Book>chunk(3)
			.reader(simpleBookReader1(null))
			.processor(simpleBookProcessor1(null))
			.writer(simpleBookWriter1())
			.build();
	}

	@StepScope
	@Bean(name = "simpleBookReader1")
	public ListItemReader<Book> simpleBookReader1(@Value("#{jobParameters[requestId]}") String requestId){
		LinkedList<Book> bookList = new LinkedList<>();

		for(int i=0; i<10; i++){
			Book b = Book.builder()
				.bookName(String.valueOf(i))
				.bookId(Long.parseLong(String.valueOf(i)))
				.build();

			bookList.add(b);
		}
		log.info("list 아이템 추가 >>> " + bookList.size() + ", requestId = " + requestId);

		return new ListItemReader<>(bookList);
	}

	@StepScope
	@Bean(name = "simpleBookProcessor1")
	public ItemProcessor<Book, Book> simpleBookProcessor1(@Value("#{jobParameters[requestId]}") String requestId){
		return new ItemProcessor<Book, Book>() {
			@Override
			public Book process(Book item) throws Exception {
				Thread.sleep(1000L);
				OffsetDateTime nowDt = OffsetDateTime.now();
				log.info("프로세서 >>> " + nowDt + ", requestId = " + requestId);
				item.setCreatedDt(nowDt);
				return item;
			}
		};
	}

	@StepScope
	@Bean(name = "simpleBookWriter1")
	public ItemWriter<Book> simpleBookWriter1(){
		return new ItemWriter<Book>() {
			@Override
			public void write(List<? extends Book> items) throws Exception {
				log.info("write :: " + items);
			}
		};
	}

}
