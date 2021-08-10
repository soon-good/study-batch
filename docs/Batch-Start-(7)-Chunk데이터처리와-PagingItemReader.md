# Batch Start (7) Chunk 데이터 처리와 PagingItemReader

[6. Spring Batch 가이드 - Chunk 지향 처리](https://jojoldu.tistory.com/331?category=902551) 의 내용을 요약함. 최대한 짧게 무한 반복으로 볼수 있게 액기스만~!!<br>

일단은 다른 문서를 정리하면서 Chunk 개념도 같이 정리할 예정이다. Chunk 쪽에서는 다루는 이야기가 많기도 하고, 체감상 개념을 정리할때 내 방식대로 요약하듯이 정리하기에 가장 애매한 개념이었던것 같다.

<br>

## 참고자료

[6. Spring Batch 가이드 - Chunk 지향 처리](https://jojoldu.tistory.com/331?category=902551)<br>

<br>

## Page Size vs Chunk Size

Chunk 처리에 대해 흔히 이야기할때 Page Size, Chunk Size 를 이야기하는 것 같다.  나중에 읽으면서 굉장히 혼동될수 있기에 서두에 못박아두듯 정리해두는 것이 좋겠다고 생각했다.

- Page Size

  - 한번 조회시 가져올 ITEM 의 갯수 (page size)

- Chunk Size 

  - 한번에 처리될 트랜잭션에서 다루는 row 의 크기
  - 페이지를 여러번 모아서 한번 처리할 때 총 몇개의 데이터여야 하는지를 의미 

<br>

## Paging x n = Chunk Size

### **Paging 단위로 데이터를 여러번 불러온후 Chunk Size 에 도달하면 반영한다.**

예를 들어보자. 만약 Chunk Size 를 1000 으로 설정해두었는데, Page Size 가 200 으로 설정해두었다고 해보자. 이 경우 200 개의 row 단위로 페이징을 걸어서 데이터를 가져온다. 그리고 이렇게 200개 row의 데이터로 Chunk Size 의 단위를 채우려면 5번의 페이징 쿼리를 수행하면 된다.<br>

<br>

### 실행 절차

요약해보면 이렇다.<br>

Chunk Size 를 1000 으로 설정했고, Page Size 를 200 으로 설정한 경우<br>

- 0~ 199 까지의 데이터를 페이징으로 가져온다.

- 200 ~ 399 까지의 데이터를 페이징으로 가져온다.

- 400 ~ 599 까지의 데이터를 페이징으로 가져온다.

- 600 ~ 799 까지의 데이터를 페이징으로 가져온다.

- 800 ~ 999 까지의 데이터를 페이징으로 가져온다.

- Chunk Size 인 1000 개의 데이터를 모두 가져왔다.

- - 이제 Chunk 데이터 처리를 한다.
  - 이렇게 Chunk 데이터에 도달해 처리를 하면 트랜잭션 단위로 취급되고 커밋이 된다.(JOB 이 커밋됨)

만약 한번에 불러와야 하는 데이터가 100만건 정도로 클 경우, 데이터를 한번에 불러오는 데에 소모되는 시간이 길어지게 된다. 이 경우 20만건씩 5번 데이터를 처리하는 것이 나을 수도 있다. Chunk 데이터 처리는 이렇게, 큰 데이터를 여러번 페이징으로 가져와야 하거나, 여러개의 데이터를 모아두었다가 한번에 처리할 때에 사용한다. <br>

<br>

<br>

TO Be continue....<br>

PagingItemReader, AbstractPagingItemReader<br>

커스텀 페이징 리더 만들어보기<br>

<br>

<br>