# 쇼핑몰 백엔드 애플리케이션

- 본 프로젝트는 Spring Boot를 기반으로 한 온라인 쇼핑몰 백엔드 애플리케이션입니다.
- RESTful API를 제공하며, 제품 관리, 주문 처리, 장바구니 관리, 사용자 인증 및 결제 처리 기능을 포함하고 있습니다.
- 또한 서비스는 JWT를 사용한 인증, BCrypt를 이용한 비밀번호 암호화, KakaoPay와의 결제 등을 지원합니다.
- FrontEnd의 경우 React/Next.js로 구현 중에 있습니다.

## 목차

- [주요 기능]
- [아키텍처]
- [사용된 기술 스택]
- [설치 및 실행]
- [환경 설정]
- [API 엔드포인트]
- [보안]
- [결제 통합]
- [파일 저장]
- [Docker와 Docker Compose 설정]
- [개발 환경]

## 주요 기능

- 사용자 관리: 사용자 등록, 로그인, 비밀번호 재설정, 이메일 인증.
- 제품 관리: 제품의 CRUD(생성, 조회, 업데이트, 삭제) 기능.
- 장바구니 관리: 사용자가 장바구니에 제품을 추가, 수정, 삭제할 수 있음.
- 주문 처리: 사용자 주문 생성 및 상태 업데이트 기능.
- 결제 처리: KakaoPay를 이용한 결제 처리 기능.
- 역할 기반 접근 제어: 사용자 및 관리자 역할에 따른 접근 제어.
- CORS 설정: 프론트엔드와의 통신을 위한 CORS 설정.
- JWT 기반 인증: 무상태 인증 방식으로 JWT 사용.

## 아키텍처

이 프로젝트는 Spring Boot의 전형적인 아키텍처를 따르며, 각 계층은 다음과 같은 역할을 합니다:

- **Controller Layer**: HTTP 요청 및 응답을 처리합니다.
- **Service Layer**: 비즈니스 로직을 처리합니다.
- **Repository Layer**: JPA를 통해 데이터베이스와 상호작용합니다.

## 사용된 기술 스택

- **Spring Boot**: 백엔드 프레임워크.
- **Spring Security**: 애플리케이션 보안을 담당.
- **JWT**: 무상태 인증을 위한 JSON Web Token.
- **Spring Data JPA**: 데이터베이스 상호작용.
- **MySQL**: 주요 데이터베이스.
- **Swagger/OpenAPI**: API 문서화.
- **KakaoPay API**: 결제 처리 통합.
- **Gradle**: 프로젝트 빌드 및 의존성 관리.
- **Docker & Docker Compose**: 컨테이너화 및 환경 설정 관리.

## 설치 및 실행

이 프로젝트를 로컬에서 실행하기 위해 다음 단계를 따르세요:

### 레포지토리 클론

```bash
git clone https://github.com/yourusername/shopping-mall-backend.git
cd Spring-ShoppingMall
```

### 데이터베이스 설정

- MySQL을 설치하고, 프로젝트용 데이터베이스를 생성하세요.
- `application.properties` 파일에서 데이터베이스 설정을 업데이트하세요.

### 의존성 설치

```bash
./gradlew clean build
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```
애플리케이션은 `http://localhost:8080`에서 실행됩니다.

## 환경 설정

### `application.properties` 파일에서 다음 속성들을 업데이트:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shopping_mall
spring.datasource.username=your_username
spring.datasource.password=your_password

jwt.secret-key=your_secret_key
jwt.expiration-ms=3600000

kakaopay.cid=your_kakaopay_cid
kakaopay.secret-key=your_kakaopay_secret_key
kakaopay.api-url=https://kapi.kakao.com
kakaopay.approval-url=http://localhost:3000/payment/success
kakaopay.cancel-url=http://localhost:3000/payment/cancel
kakaopay.fail-url=http://localhost:3000/payment/fail
```

### CORS 설정

애플리케이션은 기본적으로 `http://localhost:3000`에서 오는 CORS 요청을 허용합니다. 이 설정은 `SecurityConfig`와 `WebConfig` 클래스에서 수정할 수 있습니다.

## API 엔드포인트

### 사용자 관리

- **사용자 등록:** `POST /api/users/register`
- **로그인:** `POST /api/users/login`
- **이메일 인증:** `GET /api/users/verify-email`
- **아이디 찾기:** `GET /api/users/find-id`
- **비밀번호 재설정:** `POST /api/users/reset-password`
- **사용자 업데이트:** `POST /api/users/update`
- **사용자 삭제:** `POST /api/users/delete`
- **인증 확인:** `GET /api/users/check-auth`

### 제품 관리

- **모든 제품 조회:** `GET /api/products`
- **제품 ID로 조회:** `GET /api/products/{id}`
- **제품 생성:** `POST /api/products` (관리자만 접근 가능)
- **제품 업데이트:** `PUT /api/products/{id}` (관리자만 접근 가능)
- **제품 삭제:** `DELETE /api/products/{id}` (관리자만 접근 가능)

### 장바구니 관리

- **장바구니 조회:** `GET /api/cart`
- **장바구니 항목 추가:** `POST /api/cart/add`
- **장바구니 항목 업데이트:** `PUT /api/cart/update/{cartItemId}`
- **장바구니 항목 삭제:** `DELETE /api/cart/remove/{cartItemId}`

### 주문 관리

- **주문 생성:** `POST /api/orders/create`
- **사용자 주문 조회:** `GET /api/orders`
- **주문 상태 업데이트:** `POST /api/orders/update-status/{orderId}`

### 결제

- **KakaoPay 결제 준비:** `POST /api/payment/kakao/ready`
- **KakaoPay 결제 승인:** `GET /api/payment/kakao/approve`

### Swagger 문서

애플리케이션이 실행된 후 `http://localhost:8080/swagger-ui.html`에서 [API 문서](https://github.com/kyudori/kyudori-Spring-ShoppingMall/blob/main/Swagger%20UI.pdf)를 확인할 수 있습니다.

## 보안

- **비밀번호 암호화:** BCrypt를 사용하여 비밀번호를 안전하게 저장합니다.
- **JWT 인증:** JSON Web Tokens(JWT)를 사용하여 무상태 인증을 구현합니다.
- **역할 기반 접근 제어:** 사용자 및 관리자 역할에 따른 접근 제어가 구현되어 있습니다.
- **CORS 설정:** 특정 오리진에서만 API에 접근할 수 있도록 설정되어 있습니다.

## 결제 통합

애플리케이션은 KakaoPay와 통합되어 결제 처리를 지원합니다. 결제 흐름은 다음과 같습니다:

1. **결제 준비:** `POST /api/payment/kakao/ready`
   - KakaoPay로 결제 세션을 준비합니다.
2. **결제 승인:** `GET /api/payment/kakao/approve`
   - 사용자가 결제를 승인한 후 결제 프로세스를 완료합니다.

## 파일 저장

제품 이미지는 `frontshoppingmall/public/images/products` 디렉토리에 저장됩니다. 파일 업로드는 `FileStorageService`에서 관리되며, 고유한 파일명을 생성하여 파일 충돌을 방지합니다.

## 도커와 Docker Compose 설정

이 프로젝트는 Docker를 이용해 쉽게 배포할 수 있습니다. Docker를 이용해 MySQL 데이터베이스와 백엔드 애플리케이션, 프론트엔드 애플리케이션을 컨테이너로 구성할 수 있습니다.

### Dockerfile

`Dockerfile`은 다음과 같이 구성되어 있습니다:

```Dockerfile
FROM amazoncorretto:17
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/shoppingmall-0.0.1-SNAPSHOT.jar"]
```

### Docker Compose 설정

`docker-compose.yml` 파일은 다음과 같이 구성되어 있습니다:

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: shoppingmall_db
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: shoppingmall
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql
    networks:
      - shoppingmall-network

  backend:
    build: .
    container_name: shoppingmall_app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/shoppingmall
      SPRING_DATASOURCE_USERNAME: ${MYSQL_ROOT_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_MAIL_HOST: ${SPRING_MAIL_HOST}
      SPRING_MAIL_PORT: ${SPRING_MAIL_PORT}
      SPRING_MAIL_USERNAME: ${SPRING_MAIL_USERNAME}
      SPRING_MAIL_PASSWORD: ${SPRING_MAIL_PASSWORD}
      SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH: true
      SPRING_MAIL_PROPERTIES_MAIL_SMTP_SSL_ENABLE: true
      SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE: false
      SPRING_MAIL_PROPERTIES_MAIL_SMTP_SSL_TRUST: ${SPRING_MAIL_HOST}
    depends_on:
      - db
    networks:
      - shoppingmall-network

  frontend:
    build:
      context: .
      dockerfile: frontshoppingmall/Dockerfile
    ports:
      - "3000:3000"
    container_name: shoppingmall_frontend
volumes:
  db_data:

networks:
  shoppingmall-network:
    driver: bridge
```

이 설정을 사용하여 MySQL 데이터베이스, 백엔드, 프론트엔드 애플리케이션을 각각의 Docker 컨테이너로 구성할 수 있습니다.

## 개발 환경

### 요구 사항

- **Java 17**
- **Gradle**
- **MySQL**
- **KakaoPay API**
- **Docker & Docker Compose**
