# 👨‍👩‍👧‍👦 엄빠도 어렸다
> 부모의 추억을 자식과 공유하며 공감대를 찾는 문답형 아카이빙 서비스, 엄빠도 어렸다

<img src="https://user-images.githubusercontent.com/80024278/254717698-0849c495-c344-4cd2-a369-8b5def6c1154.jpg" width="750"/>
<br/>


## 🌸 금쪽이들
|                             이동섭                             |                                                                박예준                                                                 |
| :----------------------------------------------------------: |:----------------------------------------------------------------------------------------------------------------------------------:|
| <img src="https://user-images.githubusercontent.com/80024278/254709711-99261fdf-9a96-48e3-abfd-21346143aa8c.png" width="300"/> | <img src="https://user-images.githubusercontent.com/80024278/254709696-8d5a2b63-1fca-4f70-8fcd-242a04a21be9.jpg" width="300"/> |
|              [ddongseop](https://github.com/ddongseop)               |                                             [jun02160](https://github.com/jun02160)                                              |

## 👻 Role

| 담당 역할              |   Role   |
|:-------------------|:--------:|
| Nginx 배포, CI/CD 구축 |   이동섭    |
| DB 구축 (RDS)         |   이동섭    |
| ERD 작성              | 이동섭, 박예준 |
| API 개발              | 이동섭, 박예준 |
| 소셜로그인 기능 구현      | 이동섭 |
| 푸시알림 기능 구현       | 박예준 |


<hr>

## 🛠️ 개발 환경

| |  |
| --- | --- |
| 통합 개발 환경 | IntelliJ |
| Spring 버전 | 2.7.13 |
| 데이터베이스 | AWS RDS(MySQL), Redis |
| 배포 | AWS EC2(Ubuntu), S3 |
| Project 빌드 관리 도구 | Gradle |
| CI/CD 툴 | Github Actions, CodeDeploy |
| ERD 다이어그램 툴 | ERDCloud |
| Java version | Java 11  |
| 패키지 구조 | 도메인 패키지 구조 |
| API 테스트 | PostMan, Swagger |
| 외부 연동 | Slack, FCM |


## 🔧 시스템 아키텍처

<img width="950" src="https://user-images.githubusercontent.com/80024278/254716740-eaf5b1e6-b16d-4fc9-88e3-7e7c14dd56dd.png">

<hr>

## 📜 API Docs

### 🔗 [API Docs](https://harsh-step-7dd.notion.site/API-887ec56c3fdd48e19fec44820b63a83d?pvs=4)

<hr>

## ☁️ ERD
<img width="600" src="https://user-images.githubusercontent.com/80024278/254723343-d27666c0-1a4d-4e0e-a4cf-ea79dcd08c08.png">

<hr>

## 📂 Project Structure
```
📂 umbba-api
├── build.gradle
└── src
    ├── main
    │   ├── 📂 java/sopt/org/umbba/api
    │   │    ├── ApiApplication.java
    │   │    ├── 🗂 config
    │   │    │   ├── SecurityConfig.java
    │   │    │   ├── 🗂 auth
    │   │    │   │   ├── CustomJwtAuthenticationEntryPoint.java
    │   │    │   │   ├── JwtAuthenticationFilter.java
    │   │    │   │   └── UserAuthentication.java
    │   │    │   ├── 🗂 jwt
    │   │    │   │   ├── JwtProvider.java
    │   │    │   │   ├── JwtValidationType.java
    │   │    │   │   ├── TokenDto.java
    │   │    │   │   └── TokenRepository.java
    │   │    │   └── 🗂 sqs
    │   │    │       ├── SqsConfig.java
    │   │    │       └── 🗂 producer
    │   │    │           └── SqsProducer.java
    │   │    ├── 🗂 controller
    │   │    │   ├── 🗂 advice
    │   │    │   │   └── ControllerExceptionAdvice.java
    │   │    │   ├── 🗂 health
    │   │    │   │   ├── DemoController.java
    │   │    │   │   ├── ServerProfileController.java
    │   │    │   │   └── SlackTestController.java
    │   │    │   ├── 🗂 parentchild
    │   │    │   │   ├── ParentchildController.java
    │   │    │   │   └── 🗂 dto
    │   │    │   │       ├── 🗂 request
    │   │    │   │       │   ├── InviteCodeRequestDto.java
    │   │    │   │       │   ├── OnboardingInviteRequestDto.java
    │   │    │   │       │   └── OnboardingReceiveRequestDto.java
    │   │    │   │       └── 🗂 response
    │   │    │   │           ├── InviteResultResponseDto.java
    │   │    │   │           ├── OnboardingInviteResponseDto.java
    │   │    │   │           └── OnboardingReceiveResponseDto.java
    │   │    │   ├── 🗂 qna
    │   │    │   │   ├── QnAController.java
    │   │    │   │   └── 🗂 dto
    │   │    │   │       ├── 🗂 request
    │   │    │   │       │   └── TodayAnswerRequestDto.java
    │   │    │   │       └── 🗂 response
    │   │    │   │           ├── GetInvitationResponseDto.java
    │   │    │   │           ├── GetMainViewResponseDto.java
    │   │    │   │           ├── QnAListResponseDto.java
    │   │    │   │           ├── SingleQnAResponseDto.java
    │   │    │   │           └── TodayQnAResponseDto.java
    │   │    │   └── 🗂 user
    │   │    │       ├── AuthController.java
    │   │    │       └── 🗂 dto
    │   │    │           ├── 🗂 request
    │   │    │           │   ├── RefreshRequestDto.java
    │   │    │           │   ├── SocialLoginRequestDto.java
    │   │    │           │   └── UserInfoDto.java
    │   │    │           └── 🗂 response
    │   │    │               └── UserLoginResponseDto.java
    │   │    └── 🗂 service
    │   │        ├── 🗂 notification
    │   │        │   └── NotificationService.java
    │   │        ├── 🗂 parentchild
    │   │        │   └── ParentchildService.java
    │   │        ├── 🗂 qna
    │   │        │   └── QnAService.java
    │   │        └── 🗂 user
    │   │            ├── AuthService.java
    │   │            └── 🗂 social
    │   │                ├── 🗂 apple
    │   │                │   ├── AppleLoginService.java
    │   │                │   └── 🗂 verify
    │   │                │       ├── AppleClaimsValidator.java
    │   │                │       ├── AppleJwtParser.java
    │   │                │       ├── EncryptUtils.java
    │   │                │       └── PublicKeyGenerator.java
    │   │                └── 🗂 kakao
    │   │                    └── KakaoLoginService.java
    │   └── 📂 resources
            └── application.yaml

📂 umbba-notification
├── build.gradle
└── src
    └── main
        ├── 📂 java/sopt/org/umbba/notification
        │   ├── NotificationApplication.java
        │   ├── 🗂 config
        │   │   ├── 🗂 fcm
        │   │   │   └── FCMConfig.java
        │   │   ├── 🗂 scheduler
        │   │   │   └── ScheduleConfig.java
        │   │   └── 🗂 sqs
        │   │       ├── SqsConfig.java
        │   │       └── 🗂 consumer
        │   │           └── SqsConsumer.java
        │   └── 🗂 service
        │       ├── 🗂 fcm
        │       │   ├── FCMController.java
        │       │   ├── FCMService.java
        │       │   └── 🗂 dto
        │       │       └── FCMMessage.java
        │       ├── 🗂 scheduler
        │       │   └── FCMScheduler.java
        │       └── 🗂 slack
        │              └── SlackApi.java
        └── 📂 resources
            ├── application.yaml
            └── 🗂 firebase
                └── umbba-fcm-firebase-adminsdk.json

📂 umbba-domain
├── build.gradle
└── src
    └── main
        └── 📂 java/sopt/org/umbba/domain
             ├── UmbbaDomainRoot.java
             ├── 🗂 config
             │   └── 🗂 jpa
             │       └── JpaConfig.java
             └── 🗂 domain
                 ├── 🗂 common
                 │   └── AuditingTimeEntity.java
                 ├── 🗂 parentchild
                 │   ├── Parentchild.java
                 │   ├── ParentchildRelation.java
                 │   ├── 🗂 dao
                 │   │   └── ParentchildDao.java
                 │   └── 🗂 repository
                 │       └── ParentchildRepository.java
                 ├── 🗂 qna
                 │   ├── OnboardingAnswer.java
                 │   ├── QnA.java
                 │   ├── Question.java
                 │   ├── QuestionSection.java
                 │   ├── QuestionType.java
                 │   ├── 🗂 dao
                 │   │   └── QnADao.java
                 │   └── 🗂 repository
                 │       ├── QnARepository.java
                 │       └── QuestionRepository.java
                 ├── 🗂 redis
                 │   └── RefreshToken.java
                 └── 🗂 user
                     ├── SocialPlatform.java
                     ├── User.java
                     └── 🗂 repository
                         └── UserRepository.java

📂 umbba-common
├── build.gradle
└── src
    └── main
        └── 📂 java/sopt/org/umbba/common
             ├── UmbbaCommonRoot.java
             ├── 🗂 exception
             │   ├── ErrorType.java
             │   ├── SuccessType.java
             │   ├── 🗂 dto
             │   │   └── ApiResponse.java
             │   └── 🗂 model
             │       └── CustomException.java
             └── 🗂 sqs
                 ├── MessageType.java
                 ├── MessageUtils.java
                 └── 🗂 dto
                     ├── FCMPushRequestDto.java
                     ├── FirebaseDto.java
                     ├── MessageDto.java
                     ├── PushMessage.java
                     └── SlackDto.java

📂 umbba-external
├── build.gradle
└── src
    └── main
        └── 📂 java/sopt/org/umbba/external
             ├── UmbbaExternalRoot.java
             └── 🗂 client
                  └── 🗂 auth
                       ├── 🗂 apple
                       │   ├── AppleApiClient.java
                       │   └── 🗂 response
                       │       ├── ApplePublicKey.java
                       │       └── ApplePublicKeys.java
                       └── 🗂 kakao
                            ├── KakaoApiClient.java
                            ├── KakaoAuthApiClient.java
                            └── 🗂 response
                                 ├── KakaoAccessTokenResponse.java
                                 ├── KakaoAccount.java
                                 ├── KakaoUserProfile.java
                                 └── KakaoUserResponse.java
```

<hr>

# 🌱 Branch

<aside>

> main, develop, feat, refactor, hotfix, release
>

`main`: 최최최최최최종본 - stable all the time

`develop`: 우리가 개발하면서 코드를 모을 공간, 배포하기 전까지는 이게 default로 하여 PR은 여기로 날립니다.

`feat`: 기능을 개발하면서 각자가 사용할 브랜치

- Git Flow 전략에 따라 → “**feat/#이슈번호-구현하려는기능**” 형식으로

- ex. **feat/#3-login**, feat/#5-book_info_detail

- 해당 branch 작업 완료 후 PR 보내기
    -   항상 local에서 충돌 해결 후 → remote에 올리기
    -   reviewer에 서로 tag후 code-review
    -   approve 전 merge 불가!
    -   작동 확인된 기능의 branch는 삭제
- 코드리뷰 컨벤션
  - 서로 상대 실수 한 것 없는지 귀찮아도 꼭 읽어보기
  - 긍정적인 코멘트 적극적으로 남겨주기
  - 우선순위 반영한 코드리뷰 진행하기 ex.`[P1] 이건 꼭 반영해주셔야해요!`

</aside>
<hr>

# 🙏 Commit Convention
```
- [CHORE] : 동작에 영향 없는 코드 or 변경 없는 변경사항(주석 추가 등) or 파일명, 폴더명 수정 or 파일, 폴더 삭제 or 디렉토리 구조 변경
- [RENAME] : 파일 이름 변경시
- [FEAT] : 새로운 기능 구현
- [FIX] : 버그, 오류 해결
- [REFACTOR] : 전면 수정, 코드 리팩토링
- [ADD] : Feat 이외의 부수적인 코드 추가, 라이브러리 추가, 새로운 파일 생성
- [DEL] : 쓸모없는 코드 삭제
- [CORRECT] : 주로 문법의 오류나 타입의 변경, 이름 변경시
- [DOCS] : README나 WIKI 등의 문서 수정
- [MERGE]: 다른 브랜치와 병합
- [TEST] : 테스트 코드 추가/작성
```
- 커밋은 세부 기능 기준
- 이슈번호 붙이는 단위 : **FEAT, FIX, REFACTOR**

  ex. `git commit -m “[FEAT] 로그인 기능 구현 #2”`


# 🙏 Code Convention

> 💡 **동료들과 말투를 통일하기 위해 컨벤션을 지정합니다.**
> 
> 오합지졸의 코드가 아닌, **한 사람이 짠 것같은 코드**를 작성하는 것이 추후 유지보수나 협업에서 도움이 됩니다. 내가 코드를 생각하면서 짤 수 있도록 해주는 룰이라고 생각해도 좋습니다!

1. 기본적으로 네이밍은 **누구나 알 수 있는 쉬운 단어**를 선택한다.
    - 우리는 외국인이 아니다. 쓸데없이 어려운 고급 어휘를 피한다.
2. 변수는 CamelCase를 기본으로 한다.
    - userEmail, userCellPhone ...
3. URL, 파일명 등은 kebab-case를 사용한다.
    - /user-email-page ...
4. 패키지명은 단어가 달라지더라도 무조건 소문자를 사용한다.
    - frontend, useremail ...
5. ENUM이나 상수는 대문자로 네이밍한다.
    - NORMAL_STATUS ...
6. 함수명은 소문자로 시작하고**동사**로 네이밍한다.
    - getUserId(), isNormal() ...
7. 클래스명은**명사**로 작성하고 UpperCamelCase를 사용한다.
    - UserEmail, Address ...
8. 객체 이름을 함수 이름에 중복해서 넣지 않는다. (= 상위 이름을 하위 이름에 중복시키지 않는다.)
    - line.getLength() (O) / line.getLineLength() (X)
9. 컬렉션은 복수형을 사용하거나 컬렉션을 명시해준다.
    - List ids, Map<User, Int> userToIdMap ...
10. 이중적인 의미를 가지는 단어는 지양한다.
    - event, design ...
11. 의도가 드러난다면 되도록 짧은 이름을 선택한다.
    - retreiveUser() (X) / getUser() (O)
    - 단, 축약형을 선택하는 경우는 개발자의 의도가 명백히 전달되는 경우이다. 명백히 전달이 안된다면 축약형보다 서술형이 더 좋다.
12. 함수의 부수효과를 설명한다.
    - 함수는 한가지 동작만 수행하는 것이 좋지만, 때에 따라 부수 효과를 일으킬 수도 있다.
        ```
        fun getOrder() {
          if (order == null) {
              order = Order()
          }
        return order
        }
        ```
    - 위 함수는 단순히 order만 가져오는 것이 아니라, 없으면 생성해서 리턴한다.
    - 그러므로 getOrder() (X) / getOrCreateOrder() (O)
13. LocalDateTime -> xxxAt, LocalDate -> xxxDt로 네이밍
14. 객체를 조회하는 함수는 JPA Repository에서 findXxx 형식의 네이밍 쿼리메소드를 사용하므로 개발자가 작성하는 Service단에서는 되도록이면 getXxx를 사용하자.
