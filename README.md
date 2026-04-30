# E-Commerce BackOffice System API

Spring Boot와 JPA를 기반의 E-Commerce BackOffice 및 고객 서비스 API 서버입니다.<br/>
관리자(Admin)의 가입 승인부터 상품, 주문, 고객, 리뷰 관리 기능을 제공합니다.
<br/><br/>

## 개발 환경

![Java](https://img.shields.io/badge/Java%2017-ED8B00?logo=openjdk&style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%204.0.5-6DB33F?logo=springboot&logoColor=white&style=for-the-badge)
![JPA](https://img.shields.io/badge/JPA%203.2.0-6DB33F?logo=hibernate&logoColor=white&style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL%208.4.8-4479A1?logo=mysql&logoColor=white&style=for-the-badge)

![IntelliJ IDEA](https://img.shields.io/badge/Intellij%20IDEA-000?logo=intellijidea&style=for-the-badge)
![Postman](https://img.shields.io/badge/Postman%2011.88.0-FF6C37?logo=postman&logoColor=white&style=for-the-badge)

<br/>

## 주요 기능

- **관리자 인증/관리**
  - 관리자 회원가입(승인 대기), 로그인(Session 기반), 계정 관리(조회/수정/삭제), 역할 및 상태 관리, 승인/거부 처리
  
- **고객 관리**
  - 고객 조회/수정/삭제, 상태 관리, 주문 기반 집계 데이터 제공(총 주문 수, 총 구매 금액)
  
- **상품 관리**
  - 상품 등록/조회/수정/삭제, 상태 관리
  - 재고 변경에 따른 상태 자동 전환(판매중/품절, 단종 유지)
  
- **주문 관리**
  - 주문 생성(CS), 조회, 상태 관리(준비중 → 배송중 → 배송완료)
  - 주문 취소 및 재고 복구(취소 사유 필수, 상태 기반 제한)
  
- **리뷰 관리**
  - 리뷰 조회/삭제, 평점 및 키워드 기반 필터링
  
- **공통 기능**
  - 페이징 및 정렬(Pageable), 검색 및 필터링
  - 입력값 검증(Validation), 전역 예외 처리
<br/>

## 팀원 역할 분담

### 팀원 구성 및 담당 업무

| 이름   | 역할           | 담당 Task                                                                |
|--------|----------------|---------------------------------------------------------------------------|
| 남동엽 | Leader         | 관리자 CRUD 및 상태, 승인/거부 관리, 세션 기반 인증 구현                  |
| 김소연 | Presenter      | 발표 진행, 주문 CRUD 및 상태 관리                                         |
| 이지현 | Presentation   | 발표자료, 고객 CRUD 및 상태 관리                                          |
| 정욱재 | Recorder       | SA 문서 관리, 상품 CRUD 및 상태 관리, 전역 예외처리 구현                  |
<br/>

## API 명세서 (API Specification)

### 1. 관리자 API
*(Base URL: `http://localhost:8080/api/admins`)*

| 기능 | Method | URL | 권한 | 비고 |
| :--- | :---: | :--- | :--- | :--- |
| **관리자 회원가입** | `POST` | `/signup` | `ALL` | 승인 대기 상태(`PENDING`)로 생성 |
| **로그인** | `POST` | `/login` | `ALL` | 세션 생성 (HttpOnly 쿠키) |
| **로그아웃** | `POST` | `/logout` | `AUTH` | 세션 무효화 |
| **관리자 목록 조회** | `GET` | `/` | `SUPER` | 필터/페이징 가능 |
| **관리자 상세 조회** | `GET` | `/{id}` | `SUPER` | 특정 관리자 조회 |
| **내 정보 조회** | `GET` | `/me` | `AUTH` | 본인 프로필 |
| **관리자 승인** | `POST` | `/{id}/approve` | `SUPER` | 승인 처리 (`ACTIVE`) |
| **관리자 거절** | `POST` | `/{id}/reject` | `SUPER` | 거절 처리 (`REJECTED`) |
| **내 정보 수정** | `PATCH` | `/me` | `AUTH` | 이름/이메일/전화번호 |
| **비밀번호 변경** | `PATCH` | `/me/password` | `AUTH` | 본인 비밀번호 변경 |
| **관리자 정보 수정** | `PATCH` | `/{id}` | `SUPER` | 타 관리자 정보 수정 |
| **관리자 역할 변경** | `PATCH` | `/{id}/role` | `SUPER` | Role 변경 |
| **관리자 상태 변경** | `PATCH` | `/{id}/status` | `SUPER` | 상태 변경 (`ACTIVE`, `SUSPENDED` 등) |
| **관리자 삭제** | `DELETE` | `/{id}` | `SUPER` | 관리자 계정 삭제 |
<br/>

### 2. 고객 API
*(Base URL: `http://localhost:8080/api/customers`)*

| 기능 | Method | URL | 권한 | 비고 |
| :--- | :---: | :--- | :--- | :--- |
| **고객 목록 조회** | `GET` | `/` | `SUPER` | 이름/이메일 검색, 페이징, 정렬, 상태 필터 지원 + 총 주문 수/총 구매 금액 포함 |
| **고객 상세 정보 조회** | `GET` | `/{customerId}` | `SUPER` | 고객 기본 정보 + 총 주문 수/총 구매 금액 포함 |
| **고객 상태 변경** | `PATCH` | `/{customerId}/status` | `SUPER` | 상태 변경 (`ACTIVE`, `INACTIVE`, `SUSPENDED`) |
| **고객 정보 수정** | `PATCH` | `/{customerId}` | `SUPER` | 이름/이메일/전화번호 수정 |
| **고객 삭제** | `DELETE` | `/{customerId}` | `SUPER` | 고객 계정 삭제(탈퇴 처리) |
<br/>

### 3. 상품 API
*(Base URL: `http://localhost:8080/api/products`)*

| 기능 | Method | URL | 권한 | 비고 |
| :--- | :---: | :--- | :--- | :--- |
| **상품 등록** | `POST` | `/` | `SUPER` | 상품명, 카테고리, 가격, 재고, 상태 입력 |
| **상품 목록 조회** | `GET` | `/` | `ALL` | 키워드 검색, 페이징, 정렬, 카테고리/상태 필터 지원 |
| **상품 상세 조회** | `GET` | `/{productId}` | `ALL` | 상품 상세 정보 조회 |
| **상품 정보 수정** | `PATCH` | `/{productId}` | `SUPER` | 상품명, 카테고리, 가격 수정 |
| **상품 상태 변경** | `PATCH` | `/{productId}/status` | `SUPER` | 상태 변경 (`판매중`, `품절`, `단종`) |
| **상품 재고 변경** | `PATCH` | `/{productId}/stock` | `SUPER` | 재고 변경 시 상태 자동 전환, 단종 상태는 유지 |
| **상품 삭제** | `DELETE` | `/{productId}` | `SUPER` | 상품 삭제 처리 |
<br/>

### 4. 주문 API
*(Base URL: `http://localhost:8080/api`)*

| 기능 | Method | URL | 권한 | 비고 |
| :--- | :---: | :--- | :--- | :--- |
| **주문 생성 (고객)** | `POST` | `/customer/orders` | `CUSTOMER` | 고객, 상품, 수량 입력 / 주문번호·주문일 자동 생성 / 초기 상태 `PREPARING` / 재고 차감 및 검증 |
| **주문 생성 (관리자)** | `POST` | `/admin/orders` | `ADMIN` | 고객, 상품, 수량 입력 / 주문번호·주문일 자동 생성 / 초기 상태 `PREPARING` / 재고 차감 및 검증 |
| **주문 목록 조회** | `GET` | `/orders` | `AUTH` | 주문번호/고객명 검색, 페이징, 정렬, 상태 필터 지원 |
| **주문 상세 조회** | `GET` | `/orders/{orderId}` | `AUTH` | 주문 상세 + 고객/상품 정보 + 관리자 정보 포함 |
| **주문 상태 변경** | `PATCH` | `/orders/{orderId}/status` | `ADMIN` | 상태 변경 (`PREPARING → SHIPPING → DELIVERED`) |
| **주문 취소** | `PATCH` | `/orders/{orderId}/cancel` | `AUTH` | `PREPARING` 상태에서만 가능 / 취소 사유 필수 / 재고 복구 및 상태 동기화 |
<br/>

### 5. 리뷰 (Review) API
*(Base URL: `http://localhost:8080/api/reviews`)*

| 기능 | Method | URL | 권한 | 비고 |
| :--- | :---: | :--- | :--- | :--- |
| **리뷰 목록 조회** | `GET` | `/` | `ALL` | 키워드 검색, 페이징, 정렬, 평점 필터 지원 |
| **리뷰 상세 조회** | `GET` | `/{reviewId}` | `ALL` | 리뷰 내용 및 작성자 정보 조회 |
| **리뷰 삭제** | `DELETE` | `/{reviewId}` | `ADMIN` | 부적절한 리뷰 관리자 삭제 |
<br/>

## ERD (Entity Relationship Diagram)

<img width="1562" height="1074" alt="Image" src="https://github.com/user-attachments/assets/820aed15-4e6a-484a-b866-b703f7d0eaab" />
<br/>

<br/>

## 프로젝트 구조
```text
src/main/java/com/sparta/backoffice
├─admin
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─enums
│  ├─repository
│  └─service
├─common
│  ├─config
│  ├─constant
│  ├─dto
│  ├─entity
│  └─exception
├─customer
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─enums
│  ├─repository
│  └─service
├─order
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─enums
│  ├─repository
│  └─service
├─product
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─enums
│  ├─repository
│  └─service
├─review
│  ├─controller
│  ├─dto
│  ├─entity
│  ├─repository
│  └─service
└─ BackofficeApplication.java
```
- **controller**: 클라이언트 요청/응답 처리 및 입력값 검증, Service 호출
- **service**: 비즈니스 로직 처리 및 트랜잭션 관리
- **repository**: 데이터 접근 계층 (JPA 기반 CRUD 및 쿼리 처리)
- **entity**: DB와 매핑되는 도메인 객체 (JPA Entity)
- **dto**: 요청/응답 데이터 전달 객체 (계층 간 데이터 분리)
- **enums**: 도메인 상태값 및 타입 정의
- **common**: 전역 공통 기능 관리
  - **config**: 애플리케이션 설정 및 공통 설정 클래스 관리
    - `PasswordEncoder`: BCrypt 기반 비밀번호 암호화/검증
      
  - **constant**: 공통 상수 관리
    - `SessionConst`: 세션 키 상수 관리
      
  - **dto**: 공통 DTO 관리
    - `AdminInfo`: 세션에 저장되는 관리자 정보 DTO
      
  - **entity**: 공통 엔티티 관리  
    - `BaseEntity`: 생성일시, 수정일시, 삭제일시 관리
      
  - **exception**: 전역 예외 처리 및 공통 예외 응답 관리 
    - `GlobalExceptionHandler`: 전역 예외 처리
    - `CustomException`: 비즈니스 예외 처리
    - `ErrorCode`: 에러 코드 및 메시지 관리
    - `ErrorResponse`: 에러 응답 포맷 관리
<br/>

## 주요 기능 및 로직 설명 (Feature & Logic)

### 1. 3-Layer Architecture & Entity 설계
- **계층 분리:** `Controller`, `Service`, `Repository`로 역할을 명확히 분리하여 유지보수성과 확장성을 고려했습니다.
- **DTO 사용:** Entity를 외부에 직접 노출하지 않고 `RequestDTO`, `ResponseDTO`를 사용하여 데이터 전달 계층을 분리했습니다.
- **BaseEntity:** 모든 엔티티는 `BaseEntity`를 상속받아 `createdAt`, `modifiedAt`, `deletedAt`을 자동 관리합니다. (JPA Auditing)
- **Soft Delete:** `@SQLDelete + @SQLRestriction`를 활용하여 물리 삭제 대신 논리 삭제를 적용했습니다.
<br/>

### 2. 인증 및 인가 (Session 기반 인증)
- **Session 기반 인증:** 로그인 성공 시 사용자 정보를 서버 세션에 저장하고, 클라이언트에는 `HttpOnly Cookie`로 세션 ID를 전달합니다.
- **인증 처리 방식:** 요청 시 세션에 저장된 사용자 정보를 기반으로 인증 여부를 검증합니다.
- **인가 처리:** 사용자 역할(Role)을 기반으로 API 접근을 제한합니다.
- **보안 고려:**
  - 비밀번호는 `BCrypt`를 사용하여 암호화 저장
  - 세션 만료 시간을 설정하여 보안 강화
<br/>

### 3. 예외 처리 및 응답 통일 (Global Exception Handling)
- **GlobalExceptionHandler 적용:** `@RestControllerAdvice`를 활용하여 모든 예외를 중앙에서 처리합니다.
- **Custom Exception:** 비즈니스 로직에서 발생하는 예외를 명확하게 분리하여 관리합니다.
- **Validation 처리:** `@Valid`를 활용한 요청값 검증 실패 시 일관된 에러 응답을 반환합니다.
- **응답 형식 통일:**
  - 성공/실패 여부를 명확히 구분
  - 공통 응답 구조를 사용하여 클라이언트 처리 일관성 확보

예시:
```json
{ 
  "status": 404, 
  "code": "ADMIN_NOT_FOUND", 
  "message": "존재하지 않는 관리자입니다." 
}
```
