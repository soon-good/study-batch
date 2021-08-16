package io.study.batch.startbatchexamples.book;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Book {
	private String bookName;
	private Long bookId;
	private Long authorId;
	private Long publishCompanyId;
	private OffsetDateTime createdDt;

	@Builder
	public Book(String bookName, Long bookId){
		this.bookName = bookName;
		this.bookId = bookId;
	}

	@Builder
	public Book(String bookName, Long bookId, OffsetDateTime createdDt){
		this.bookName = bookName;
		this.bookId = bookId;
		this.createdDt = createdDt;
	}
}
