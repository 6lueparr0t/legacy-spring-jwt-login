# Rest API 기반 JWT(Json Web Token) AccessToken 서버

## 개발 프레임 워크
JAVA (Spring Boot v2.3.12.RELEASE)

## 테이블 설계
* **USER (TBL_USER)**

| 필드명 | 자료형 | 설명 |
| -------- | ------- | ----------- |
| **id** | Integer | Primary Key |
| **name** | String(200) | 사용자 이름 |
| **uid** | String(100) | 아이디 |
| **pw** | String(400) | 패스워드 |
| **cdtm** | String | 생성 날짜 |

```java
UserRepository.java

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@Column(length = 200, nullable = false, name = "name")
private String userName;
@Column(unique = true, length = 100, nullable = false, name = "uid")
private String userId;
@Column(length = 400, nullable = false, name = "pw")
private String userPassword;

@Column(name = "cdtm")
@CreatedDate
private LocalDateTime createdDate;
```

* **TOKEN (TBL_TOKEN)**

| 필드명 | 자료형 | 설명 |
| -------- | ------- | ----------- |
| **id** | Integer | Primary Key |
| **uid** | String(100) | 아이디 |
| **access** | String(400) | Access Token |
| **useCode** | Char | 사용여부(Y, N, *NULL*) |
| **cdtm** | String | 생성 날짜 |
| **udtm** | String | 수정 날짜 |

```java
TokenRepository.java

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(length = 100, name = "uid")
private String userId;

@Column(length = 400)
private String access;

@Convert(converter = UseCodeConverter.class)
@Column(columnDefinition = "char", name = "use")
private UseCode useCode;

@Column(name = "cdtm")
@CreatedDate
private LocalDateTime createdDate;

@Column(name = "udtm")
@LastModifiedDate
private LocalDateTime updatedDate;
```

## 문제해결 전략
- Spring Security 를 사용한 기본 사용자 생성 및 로그인 처리
- 로그인 후 JWT 를 생성하고, 생성한 토큰을 토큰 DB 에 저장
- 생성한 토큰은 UserId 정보와 만료 날짜 (1시간) 을 가지고 있음
- 검증 시 토큰에 있는 Claim 정보(Id, UserId)로 토큰 DB를 조회하여 기본 유효성 체크와 사용 유무를 체크함
- 로그아웃 API 요청 시, 토큰 DB 에서 사용 컬럼을 N 으로 업데이트 하고, 해당 토큰은 더 이상 사용하지 못함
- 가입(/sign/up), 로그인(/sign/in), 검증(/sign/check), 로그아웃(/sign/out)

## 빌드 및 실행 방법
gradle 을 통해 빌드 & 실행

```bash
# Build
gradle clean build -x test

# Execute
java -jar -Dspring.profiles.active=local kakaopaycoding-server-1.0-SNAPSHOT*.jar
```
