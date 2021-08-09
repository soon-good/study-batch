# Batch Start (1) Spring Batch 환경설정

[개발자 이동욱님, 2.Spring Batch 가이드 - Batch Job 실행해보기](https://jojoldu.tistory.com/325?category=902551) 의 글을 읽고, 나중에 다시 보기 쉽게 요약해두는 용도로 정리를 시작<br>

<br>

## 참고자료

[개발자 이동욱님, 2.Spring Batch 가이드 - Batch Job 실행해보기](https://jojoldu.tistory.com/325?category=902551)<br>

<br>

## 예제코드 

[github/gosgjung/study-batch](https://github.com/gosgjung/study-batch/tree/develop/example-codes/START-BATCH-EXAMPLES)<br>

<br>

## 데이터베이스 선택

데이터베이스는 Postgresql 을 선택했다. 도커로 실행해서 Postgresql 을 띄우는 방식으로 했다. 테스트 환경에서만 사용하기 위한 용도라면 도커가 괜찮은 것 같다는 생각이 자주 들었었다. <br>

도커 컨테이너 구동/삭제 쉘 스크립트를 예전에 만들어 둔게 있는데 해당 [쉘스크립트](https://github.com/gosgjung/docker-scripts/tree/develop/docker-postgresql)는 여기에 있다.<br>

원래는 설명을 더 자세히 할까 했는데, 그럴 필요가 없다. 괜히 힘뺄필요가 없다.

<br>

## 의존성 추가

gradle 의존성

- `spring-boot-starter-batch`

- - implementation 'org.springframework.boot:spring-boot-starter-batch'

- `spring-batch-test`

- - testImplementation 'org.springframework.batch:spring-batch-test'

<br>

**build.gradle**<br>

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-batch'
    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    runtimeOnly 'org.postgresql:postgresql'
    
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.batch:spring-batch-test'
}
```

<br>

h2 에서 테스트하는 것이 아니라면, Database 를 생성해야 한다. 스키마 및 테이블들을 생성하는 sql 들은 spring-boot-starter-batch 를 의존성으로 추가하는 순간 클래스 패스 안에 다운로드 받아진다. <br>



## 스키마 생성

시프트 두번 클릭 > 파일 검색 > 'schema-' 검색<br>

아래와 같이 파일들이 보이는데, 내 경우는 postgresql 을 사용하기에 postgresql 에 관련된 sql 파일들을 선택했다.<br>

![이미지](./img/batch-start/1-SCHEMA-DDL.png)



위에서 찾은 두 파일들은 아래와 같다.<br>

- schema-postgresql.sql
- schema-drop-postgresql.sql

위의 sql 파일 중 schema-postgresql.sql 파일 내의 내용을 실행해주어야 하는데, postgresql의 경우 반드시 public 스키마 내에서 실행시켜야 한다. 그렇지 않으면 에러를 낸다. (커스터마이징 하는 법은 아직 모른다. schema를 직접 지정하는 방법에 대해 찾아봐야 할 것 같다.)<br>

**베치 메타 테이블들을 생성한 결과**<br>

![이미지](./img/batch-start/1-SCHEMA-RESULT.png)

<br>

## application.yml

```yaml
spring:
  profiles:
    active: test-postgresql
---
spring:
  config:
    activate:
      on-profile: test-postgresql
  datasource:
    url: jdbc:postgresql://localhost:35432/postgres
    username: postgres
    password: 1111
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        show_sql: true
        format_sql: true
        use_sql_comments: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

<br>

## 샘플 코드

**SampleJobConfig.java**

```java
package io.study.jpa.key_combination.config;

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
         .start(sampleStep())
         .build();
   }

   @Bean
   public Step sampleStep() {
      return stepBuilderFactory.get("sampleStep1")
         .tasklet((contribution, chunkContext) -> {
            log.info(">>>>> step1 ");
            return RepeatStatus.FINISHED;
         })
         .build();
   }
}
```

<br>

## Batch 설정 Enable

`@EnableBatchProcessing` 애노테이션을 붙여주자.

```java
package io.study.batch.startbatchconf1;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class StartBatchConf1Application {

	public static void main(String[] args) {
		SpringApplication.run(StartBatchConf1Application.class, args);
	}

}
```

