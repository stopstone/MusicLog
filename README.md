# 감정플리 - 음악감정일기 안드로이드 앱
> 개인 프로젝트  
> PlayStore: https://play.google.com/store/apps/details?id=com.stopstone.musicplaylist

## 📌 프로젝트 개요
감정플리는 매일의 감정을 음악으로 기록하는 다이어리 앱입니다.  
하루를 대표하는 주제곡을 선정하고 그날의 감정을 기록할 수 있습니다.  
마치 나만의 작은 음악 다이어리처럼, 음악을 통해 그날의 감정과 순간을 더 풍성하게 기록할 수 있습니다.
Spotify API로 음악을 검색하고, 캘린더를 통해 과거의 기록을 돌아볼 수 있습니다.  
인스타그램 스토리로 음악 기록을 공유하거나, 일일 알림을 통해 꾸준히 기록할 수 있습니다.
MVVM을 적용하여 확장 가능하고 유지보수가 용이한 구조로 설계했습니다.

![MusicLogBanner](https://github.com/user-attachments/assets/a9a7d0b3-4ae4-4ade-94e8-2efabfaa9684)

### 프로젝트 목표
* MVVM 아키텍처 패턴 실습
* Repository Pattern을 통한 데이터 계층 분리
* UseCase를 활용한 비즈니스 로직 모듈화
* Room Database 설계 및 구현
* Coroutines & Flow 활용 실습
* irebase Firestore를 통한 클라우드 데이터 동기화
* WorkManager를 활용한 백그라운드 작업 처리
* 소셜 로그인 연동 및 사용자 인증 구현

## 📅 프로젝트 기간
2024.06 - 2024.08 (현재 지속적으로 업데이트 중)

## 🏗 아키텍처
프로젝트는 Clean Architecture를 기반으로 구현되었으며, 다음과 같은 레이어로 구성되어 있습니다:

![Clean Architecture Diagram](https://github.com/user-attachments/assets/0ca27179-b1f5-4c26-b9c6-ef86521111b5)

### UI Layer
- 사용자 인터페이스 요소
- Activity, Fragment
- ViewModel: UI 상태 관리 및 비즈니스 로직 처리
- UI 모델: 화면에 표시될 데이터 객체

### Domain Layer
- 비즈니스 로직 정의
- UseCase: 각 기능별 비즈니스 로직 캡슐화
- Repository Interface: 데이터 계층과의 통신 규약 정의
- Domain Model: 비즈니스 로직에서 사용되는 모델

### Data Layer
- 실제 데이터 처리 구현
- Repository Implementation: 데이터 소스 관리
- Local Database: Room을 이용한 로컬 저장소
- Remote Data Source: Spotify API, Firebase Firestore 연동
- Data Model: API 응답 및 데이터베이스 모델

## 🛠 사용 기술
### Architecture
* Clean Architecture
* MVVM Pattern
* Repository Pattern

### Android
* Kotlin
* Jetpack Components (ViewModel, Navigation, Room)
* Coroutines Flow
* Dagger Hilt

### Network & Data
* Retrofit2 & OkHttp3
* Room Database
* DataStore
* Firebase Firestore
* Firebase Analytics

### Background & Notification
* WorkManager
* Notification Channel

### Authentication
* Kakao SDK

## 🌟 주요 기능
### 1. 음악 검색 및 저장 
* Spotify API를 통한 음악 검색
* 일일 음악 저장 기능
* 메모 작성 기능
* 최근 검색어 저장 및 관리
  - 최근 검색어 5개까지 저장
  - 검색어 개별 삭제 및 전체 삭제

### 2. 캘린더 뷰
* 월별 음악 기록 표시
* 앨범 커버 이미지로 시각화
* 날짜별 상세 정보 조회
* 이전/다음 월 네비게이션

### 4. 인스타그램 공유
* 인스타그램 스토리 공유 기능
* 커스텀 스토리 이미지 생성
* 감정, 메모, 기록 시간 표시 옵션 설정
* 공유 설정 커스터마이징 (감정 표시 여부, 메모 표시 여부 등)

### 5. 알림 기능
* 일일 음악 기록 알림
* 매일 지정된 시간(21시)에 알림 발송
* 오늘 기록이 없을 때만 알림 발송
* 알림 설정 ON/OFF 기능

### 6. 로그인 및 사용자 관리
* 카카오 소셜 로그인 지원
* 자동 로그인 기능
* Firebase Firestore를 통한 사용자 데이터 동기화
* 사용자별 음악 기록 및 설정 동기화

## 🔥 구현 내용 및 해결 과제
### 1. Spotify API 연동
* Token 기반 인증 시스템 구현
* API 응답 데이터 모델링
* DataStore를 활용한 토큰 저장 및 관리
* AuthInterceptor를 통한 API 요청 관리

### 2. 로컬 데이터베이스 설계
* Room Database 테이블 설계
  - DailyTrack: 일일 음악 정보 저장
  - SearchHistory: 최근 검색어 관리
  - SignatureSong: 인생곡 저장
* Entity 관계 설정

### 3. 상태 관리
* StateFlow를 활용한 UI 상태 관리
* 검색, 추천, 캘린더 상태 관리

### 4. 데이터 관리
* Room Database를 통한 로컬 데이터 관리
* DataStore를 통한 토큰 관리
* 검색 기록 관리 (최근 5개)

### 5. 인스타그램 공유 기능
* 커스텀 View를 Bitmap으로 변환하여 스토리 이미지 생성
* FileProvider를 통한 안전한 이미지 공유
* 인스타그램 스토리 공유 인텐트 처리
* 공유 설정(감정, 메모, 시간 표시) 커스터마이징
* 인스타그램 미설치 시 Play Store 리다이렉트 처리

### 6. 알림 시스템
* WorkManager를 활용한 주기적 알림 스케줄링
* BootReceiver를 통한 재부팅 후 알림 재등록
* Hilt EntryPoint를 활용한 Worker 의존성 주입
* Android 13+ 알림 권한 처리
* 알림 채널 관리 및 사용자 설정 연동
* 오늘 기록 여부 확인 후 조건부 알림 발송

### 7. 소셜 로그인 및 데이터 동기화
* 카카오 SDK를 활용한 소셜 로그인 구현
* Firebase Firestore를 통한 클라우드 데이터 저장
* 로컬 데이터와 Firestore 양방향 동기화
  - 로컬 → Firestore: 로컬에만 있는 데이터 업로드
  - Firestore → 로컬: 클라우드 데이터 다운로드 및 병합
* 사용자별 데이터 분리 저장 구조
* 설정 데이터 동기화 (인스타 공유 설정, 감정 설정 등)
* 자동 로그인 및 사용자 세션 관리

## 📁 패키지 구조
```
com.stopstone.musicplaylist/
├── base/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── database/
│   │   └── settings/
│   ├── model/
│   │   ├── dto/
│   │   ├── entity/
│   │   └── response/
│   ├── remote/
│   │   ├── api/
│   │   ├── datasource/
│   │   └── dto/
│   └── repository/
│       ├── common/
│       ├── emotion_setting/
│       ├── home/
│       ├── insta_share/
│       ├── my/
│       ├── notification/
│       ├── search/
│       └── user/
├── di/
│   └── worker/
├── domain/
│   ├── model/
│   ├── repository/
│   │   ├── common/
│   │   ├── emotion_setting/
│   │   ├── home/
│   │   ├── insta_share/
│   │   ├── my/
│   │   ├── notification/
│   │   ├── search/
│   │   └── user/
│   └── usecase/
│       ├── common/
│       ├── detail/
│       ├── emotion_setting/
│       ├── home/
│       ├── insta_share/
│       ├── login/
│       ├── my/
│       ├── notification/
│       ├── search/
│       ├── setting/
│       ├── splash/
│       └── user/
├── notification/
├── ui/
│   ├── common/
│   ├── detail/
│   ├── emotion_setting/
│   ├── home/
│   ├── insta_share_setting/
│   ├── login/
│   ├── music_memo/
│   ├── music_search/
│   ├── my/
│   ├── playlist/
│   ├── signature_list/
│   ├── signature_music/
│   └── user_setting/
└── util/
```

## 💡 배운 점
### 아키텍처 설계
* 클린 아키텍처 원칙을 실제 프로젝트에 적용하며 각 계층의 역할과 책임 이해
* MVVM 패턴으로 UI와 비즈니스 로직 분리의 장점 경험

### 비동기 처리
* Coroutines Flow를 활용한 데이터 스트림 처리

### 데이터 관리
* Room Database와 DataStore의 적절한 활용
* Repository 패턴을 통한 데이터 소스 추상화
* Clean Architecture를 통한 비즈니스 로직 분리
* Firebase Firestore를 통한 클라우드 데이터 동기화
* 로컬과 클라우드 데이터 병합 전략

### 백그라운드 작업 및 알림
* WorkManager를 활용한 주기적 작업 스케줄링
* Hilt EntryPoint를 통한 Worker 의존성 주입
* 알림 채널 관리 및 권한 처리

### 소셜 로그인 및 인증
* 카카오 SDK를 활용한 소셜 로그인 구현
* 사용자 세션 관리 및 자동 로그인

## 🔄 향후 개선 사항

### 1. 감정 분석 기능
* 월별/연도별 감정 통계 제공
* 선호 음악 장르 분석
* 감정 패턴 시각화 (그래프, 차트)
* 선호 아티스트 분석
