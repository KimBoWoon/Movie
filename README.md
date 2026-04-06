# Surfy
[Now in Android](https://github.com/android/nowinandroid)를 참고해 만든 Android 토이 프로젝트

## 소개
[TMDB API](https://developer.themoviedb.org/reference/getting-started)를 사용하여 영화/TV/인물 정보를 탐색하고,
즐겨찾기/검색/상세 조회/주기적 동기화 기능을 제공하는 앱입니다.

## 프로젝트 개요
- **목적**: TMDB 데이터를 활용한 콘텐츠 탐색 앱 구현
- **플랫폼**: Android (minSdk 26)
- **언어/UI**: Kotlin + Jetpack Compose
- **핵심 기능**
    - 홈(현재 상영/개봉 예정/트렌딩)
    - 검색(영화/TV/인물/시리즈)
    - 상세(영화/TV/인물/시리즈)
    - 즐겨찾기
    - 딥링크 및 알림 기반 진입

## 모듈 구조
멀티 모듈 구조로 기능과 공통 계층을 분리했습니다.

### App / Feature
- `:surfy` : 앱 진입점, 메인 Activity/앱 상태/초기화
- `:feature:home` : 홈 화면
- `:feature:detail` : 상세 화면
- `:feature:search` : 검색 화면
- `:feature:favorite` : 즐겨찾기 화면
- `:feature:setting` : 설정 화면

### Core
- `:core:model` : 도메인 모델
- `:core:network` : TMDB 네트워크 계층 (Retrofit, Serialization)
- `:core:data` : Repository 구현
- `:core:domain` : UseCase
- `:core:database` : Room DB
- `:core:datastore` : DataStore(사용자 설정)
- `:core:sync` : WorkManager 기반 동기화
- `:core:navigation` : Navigation3 기반 앱 내비게이션 상태
- `:core:ui`, `:core:common`, `:core:analytics`, `:core:firebase`, `:core:notifications`,
  `:core:testing`

## 아키텍처
`feature -> domain -> data -> network/database/datastore` 흐름을 따릅니다.
- **Feature**: 화면 상태 관리(ViewModel) 및 Compose UI
- **Domain**: 비즈니스 조합 로직(UseCase)
- **Data**: 네트워크/로컬 데이터 소스 통합
- **Infra**: Room/DataStore/WorkManager/Firebase

## 기술 스택
- **Language**: Kotlin
- **UI**: Jetpack Compose, Material3
- **DI**: Hilt
- **Async**: Coroutines, Flow
- **Network**: Retrofit2, OkHttp, Kotlinx Serialization
- **Storage**: Room, DataStore(Proto)
- **Background**: WorkManager
- **Etc**: Firebase (Analytics/Crashlytics/FCM), Coil, Navigation3

## 실행 방법

### 1) 환경 준비
- JDK 17
- Android SDK / Gradle 환경

### 2) 시크릿 파일 준비
프로젝트는 `./sign/local.properties` 파일을 기준으로 API 키 및 서명 정보를 읽습니다.

`sign/local.properties` 예시:
```properties
tmdb_open_api_key=YOUR_TMDB_KEY
store_file_path=./sign/your_keystore.jks
store_password=******
key_alias=******
key_password=******
```

필요 시 `surfy/google-services.json`도 준비합니다(Firebase 사용 시).

### 3) 빌드 / 테스트
```bash
./gradlew assembleProdDebug
./gradlew testProdReleaseUnitTest
```

## CI/CD
- PR 빌드: 단위 테스트 실행
- 배포: `master`, `release/**` 브랜치 푸시 시 Firebase App Distribution 업로드

## 스크린샷

 <div align="center">
  <img src="/screenshot/Screenshot_20260116_150104.png" width="30%" height="30%" alt="home" />
  <img src="/screenshot/Screenshot_20260117_171019.png" width="30%" height="30%" alt="detail" />
  <img src="/screenshot/Screenshot_20260117_171236.png" width="30%" height="30%" alt="search" />
 </div>

## 개발자
- Android: [김보운](https://github.com/KimBoWoon)