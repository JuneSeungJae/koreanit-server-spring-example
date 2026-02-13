# Koreanit Community Backend (Spring Boot)

세션 기반 인증과 커뮤니티 기능(회원/게시글/댓글)을 구현한 **Spring Boot REST API 프로젝트**입니다.

---

## 👋 프로젝트 소개

학습용 CRUD를 넘어, 실제 서비스 흐름을 고려해 아래를 중점적으로 구현했습니다.

- 로그인 상태 유지가 가능한 **세션 인증 구조**
- 사용자/게시글/댓글 도메인 분리
- 작성자/본인 검증이 포함된 **권한 제어**
- 일관된 JSON 응답 포맷과 전역 예외 처리

---

## 🛠 기술 스택

- **Java 17**
- **Spring Boot 3.5**
- Spring Web, Spring Security, Validation
- Spring JDBC
- MySQL
- Redis + Spring Session
- Gradle

---

## 📌 핵심 구현 포인트

### 1) 인증/보안
- `HttpSession` 기반 로그인/로그아웃
- Redis에 세션 저장 (서버 재시작/멀티 인스턴스 대응 기반)
- `/api/**` 보호 + 공개 엔드포인트만 예외 허용
- `@PreAuthorize`로 메서드 단 권한 제어
  - 본인 또는 ADMIN만 수정/삭제 가능

### 2) API 설계
- 공통 응답 래퍼 `ApiResponse<T>` 적용
- 성공/실패 형식 통일 (`success`, `message`, `data`, `code`)
- `@RestControllerAdvice` 기반 전역 예외 처리

### 3) 커뮤니티 기능
- 회원가입/로그인/내 정보 조회/수정
- 게시글 생성/조회/수정/삭제
- 댓글 생성/목록/삭제
- 게시글 조회수 증가, 댓글 수 동기화

### 4) 구조화
- 도메인 중심 패키지 분리 (`user`, `post`, `comment`, `security`, `common`)
- Controller / Service / Repository 계층 분리

---

## 🔗 주요 API

### Auth
- `POST /api/login`
- `POST /api/logout`
- `GET /api/me`

### User
- `POST /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}/nickname`
- `PUT /api/users/{id}/email`
- `PUT /api/users/{id}/password`
- `DELETE /api/users/{id}`

### Post
- `POST /api/posts`
- `GET /api/posts?page=1&limit=20`
- `GET /api/posts/{id}`
- `PUT /api/posts/{id}`
- `DELETE /api/posts/{id}`

### Comment
- `POST /api/posts/{postId}/comments`
- `GET /api/posts/{postId}/comments?before=&limit=20`
- `DELETE /api/comments/{id}`

---

## ▶ 실행 방법

### 1. 개발 실행
```bash
./gradlew bootRun
```

### 2. 빌드 및 실행
```bash
./gradlew clean bootJar
java -jar build/libs/spring-0.0.1-SNAPSHOT.jar
```

> 실행 전 MySQL/Redis 연결 정보(application 설정) 확인이 필요합니다.

---

## 💡 트러블슈팅 경험

- 외부 클라이언트 연동 시 CORS/세션 이슈를 점검하며,
  인증 쿠키(`credentials: include`)와 허용 Origin 정책의 중요성을 확인했습니다.
- 세션 timeout 설정에 따라 로그인 유지가 달라지는 문제를 수정했습니다.

---

## 📂 프로젝트 목표

단순 CRUD를 넘어,
**“인증 + 권한 + 예외 처리 + 도메인 분리”가 갖춰진 백엔드 기본기**를 보여주는 것을 목표로 했습니다.
