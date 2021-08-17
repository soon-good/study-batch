# Play Batch (1) - 단순 ListItemReader 예제 1

예제로 어떤걸 만들까? 하다가 머리 뽀개지는 줄 알았다. 자꾸 하나 정했는데, 다른 아이디어가 떠오르고 그래서 힘들었었다.<br>

오늘은 일단 예제의 소스코드와 출력결과만 정리해놓고, 아... 이번주 내로 정리할 예정이다. 요즘 공부하는거 왜 이렇게 체력이 딸리는지 모르겠다.<br>

<br>

## 예제 시나리오

- Job
- ListItemReader
  - 아래 예제에서는 `simpleBookReader1()` 메서드다. 
  - ListItemReader 에서는 `size = 10` 인 리스트를 생성해 리턴한다.
  - 0 부터 9 까지의 숫자를 반복문으로 순회하면서 Book 객체를 생성하는데, `0 ~ 9` 사이의 숫자를 각 book 객체의 bookName, bookId에 지정하고 list.add() 로 이 객체들을 하나씩 추가해준다.
    - ex) book1(bookName=1, bookId=1), book2(booName=2, bookId=2), ...
  - 만들어진 0~9 까지의 Book 들의 리스트를 ListItemReader 의 생성자에 넘겨주어 ListItemReader 객체를 생성한다.
  - 생성한 ListItemReader 객체를 리턴한다.
  - 보통의 경우는 JPAItemReader, AbstractPagingItemReader 등을 사용하겠지만, DB에서 조회해온 `List<T>` 타입의 자료를 받는 예제를 1번 예제로 만들면 복잡해보이기도 하고 재미도 없을것 같아서 ListItemReader 기반의 예제로 선택했다.
- ItemProcessor
  - 아이템 프로세서에서는 현재 시각을 각각의 book 객체의 `createdDt` 필드에 세팅해준다.
  - 이때 1초의 슬립을 두어서 각각의 시간이 서로 구분되도록 했다.
- ItemWriter
  - 보통 ItemWriter 에서는 DB에 저장하는 등의 동작을 정의하는 경우가 많다.
  - 오늘 작성하는 예제에서는 DB를 사용하지 않을 것이기 때문에 그냥 log 를 출력하도록 작성했다.<br>

<br>

## 기대되는 동작

Job의 설정을 chunkSize = 3으로 지정할 것이다. 이렇게 지정했으니 리스트 내의 10개의 요소를 읽어들일때 한번에 3개씩 processing 하고, write(=log 문 출력) 해야 한다.<br>

만약, 실제 DB를 연동하게 된다면, 10개의 요소를 가진 리스트를 한번에 불러오고, 이것을 한번에 3개의 요소를 가져와 처리하고 저장하는 방식이다.<br>

<br>


## JobComponent 만들기

Job 설정 클래스는 보통 @Configuration, @Component 등으로 모두 가능한데, 오늘 작성하는. `play-batch` 1번 예제에서는 @Component 애노테이션으로 설정 클래스를 애플리케이션 전역에 Bean 으로 등록했다.<br>

**SimpleListItemJob1.java**<br>

```java
@Slf4j
@RequiredArgsConstructor
@Component
public class SimpleListItemJob1 {

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
```

<br>

## 테스트 코드

이렇게 작성한 소스를 실행시키려면, 인텔리제이의 Run Configuration 을 바꿔서 해도 실행한다. 하지만 GUI를 클릭해가면서 수정하는것은 아무래도 귀찮다. 이번 예제에서는 테스트 코드에서 프로그램 내에서 동적으로 Job 을 생성되도록 하는 테스트 코드를 작성했다.

```java
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
```

<br>

## 출력결과

로그를 자세히 보면, size = 10 인 리스트를 chunk size 인 3 만큼 읽어들이고 있다.<br>

기대한 결과와 같다.<br>

<br>

![이미지](./img/play-batch/1-SIMPLE-LIST-ITEM-JOB1-1.png)

<br>

해당 로그의 텍스트는 아래에 남겨두었다.

```plain
2021-08-16 22:25:43.409  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : list 아이템 추가 >>> 10, requestId = null
2021-08-16 22:25:43.506  INFO 52473 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=simpleListJob1]] launched with the following parameters: [{requestId=20210816 22:25:43.409}]
2021-08-16 22:25:43.549  INFO 52473 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [simpleStep1]
2021-08-16 22:25:44.580  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:44.577575+09:00, requestId = null
2021-08-16 22:25:45.583  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:45.583608+09:00, requestId = null
2021-08-16 22:25:46.585  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:46.585733+09:00, requestId = null
2021-08-16 22:25:46.591  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : write :: [Book(bookName=0, bookId=0, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:44.577575+09:00), Book(bookName=1, bookId=1, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:45.583608+09:00), Book(bookName=2, bookId=2, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:46.585733+09:00)]
2021-08-16 22:25:47.603  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:47.603865+09:00, requestId = null
2021-08-16 22:25:48.604  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:48.604857+09:00, requestId = null
2021-08-16 22:25:49.606  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:49.605894+09:00, requestId = null
2021-08-16 22:25:49.606  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : write :: [Book(bookName=3, bookId=3, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:47.603865+09:00), Book(bookName=4, bookId=4, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:48.604857+09:00), Book(bookName=5, bookId=5, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:49.605894+09:00)]
2021-08-16 22:25:50.617  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:50.617071+09:00, requestId = null
2021-08-16 22:25:51.622  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:51.622464+09:00, requestId = null
2021-08-16 22:25:52.625  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:52.625274+09:00, requestId = null
2021-08-16 22:25:52.625  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : write :: [Book(bookName=6, bookId=6, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:50.617071+09:00), Book(bookName=7, bookId=7, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:51.622464+09:00), Book(bookName=8, bookId=8, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:52.625274+09:00)]
2021-08-16 22:25:53.634  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : 프로세서 >>> 2021-08-16T22:25:53.634166+09:00, requestId = null
2021-08-16 22:25:53.634  INFO 52473 --- [           main] i.s.b.s.j.simple_job.SimpleListItemJob1  : write :: [Book(bookName=9, bookId=9, authorId=null, publishCompanyId=null, createdDt=2021-08-16T22:25:53.634166+09:00)]
2021-08-16 22:25:53.649  INFO 52473 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [simpleStep1] executed in 10s100ms
2021-08-16 22:25:53.667  INFO 52473 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=simpleListJob1]] completed with the following parameters: [{requestId=20210816 22:25:43.409}] and the following status: [COMPLETED] in 10s140ms
2021-08-16 22:25:53.678  INFO 52473 --- [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
```



