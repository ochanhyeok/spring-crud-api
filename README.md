# CRUD 게시판 REST API

Spring Boot 기반 REST API 학습 프로젝트입니다.
회원 / 게시글 / 댓글의 CRUD를 구현하며, **순수 자바(메모리 저장소)로 먼저 구현한 뒤 JPA로 리팩터링**하는 것을 목표로 합니다.

## 학습 목표

- REST API 설계 및 구현 (`@RestController`, `@RequestBody`, DTO)
- 계층형 아키텍처 (Controller → Service → Repository)
- 도메인 간 연관관계 처리 (게시글·댓글의 작성자 조회)
- 단위 테스트 작성 (JUnit5, AssertJ)
- **[예정] JPA + H2 전환** — 메모리 저장소를 JPA로 리팩터링하며 연관관계 매핑, 영속성 학습

## 기술 스택

| 구분 | 내용 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Build | Gradle |
| Test | JUnit5, AssertJ |
| 저장소 | 인메모리 (`ConcurrentHashMap`) — *JPA 전환 예정* |

## 도메인 구조

```
Member (회원)
  └─ 1:N ─ Post (게시글)      // 작성자 = memberId
             └─ 1:N ─ Comment (댓글)  // 게시글 = postId, 작성자 = memberId
```

- 연관관계는 현재 **id 참조**(`memberId`, `postId`)로 표현
- JPA 전환 시 객체 참조(`@ManyToOne`)로 변경 예정

## API

### 회원
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/members` | 회원 생성 |
| GET | `/api/members` | 회원 목록 |
| GET | `/api/members/{id}` | 회원 단건 조회 |

### 게시글
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/posts` | 게시글 생성 |
| GET | `/api/posts` | 게시글 목록 |
| GET | `/api/posts/{id}` | 게시글 단건 조회 |

### 댓글
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/comments` | 댓글 생성 |
| GET | `/api/comments` | 댓글 목록 |
| GET | `/api/comments/{id}` | 댓글 단건 조회 |

> 게시글·댓글 응답에는 작성자 이름(`authorName`)이 포함됩니다. (id로 회원을 조회해 조립)

## 설계 포인트

- **DTO 분리**: 요청(`XxxCreateRequest`) / 응답(`XxxResponse`)을 도메인과 분리. 응답에서 비밀번호 등 민감 정보 제외.
- **Repository 인터페이스화**: 구현체(`XxxRepositoryImpl`)를 분리해 JPA 전환 시 교체가 용이하도록 설계.
- **도메인 불변성**: `@Setter` 대신 생성자·의미 있는 메서드(`assignId`)로 상태 변경.
- **Optional**: 단건 조회는 `Optional` 반환, 없으면 예외 처리.

## 실행

```bash
./gradlew bootRun
```

## 테스트

```bash
./gradlew test
```

Service 계층 단위 테스트 작성 (회원/게시글/댓글). 성공·실패(예외) 케이스 및 연관관계(작성자 이름) 검증 포함.

## 진행 상황

- [x] 도메인 · Repository (메모리)
- [x] REST API (Member / Post / Comment)
- [x] Service 단위 테스트
- [ ] Controller API 테스트 (MockMvc)
- [ ] JPA + H2 전환 (연관관계 매핑, 삭제 정책)
- [ ] 로그인 · 인증
