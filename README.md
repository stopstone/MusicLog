# GithubSearch

MisicLog 프로젝트는 Spotify API를 활용하여 하루에 하나씩 음악을 지정하고, 메모하는 안드로이드 앱입니다.
이를 통해 MVVM 아키텍쳐, Retorofit에 대해 활용하였으며 RoomDB의 설계를 고민하였습니다.

## 활용 기술

- 언어: Kotlin
- 아키텍처: MVVM (Model-View-ViewModel)
  - Repository Pattern 적용
  - UseCase를 통해 Repository와 ViewModel의 의존성 감소
  - ![image](https://github.com/user-attachments/assets/0ca27179-b1f5-4c26-b9c6-ef86521111b5)

- Jetpack 컴포넌트:
  - ViewModel
  - Navigation Component
  - DataStore
- 비동기 처리: Coroutines, Flow
- 상태 관리: StateFlow
- 의존성 주입: Dagger Hilt
- 네트워크: Retrofit2, OkHttp3
- JSON 파싱: Gson
- 이미지 로딩: Glide
- 로컬 데이터 저장: Room, DataStore
  
## 프로젝트 구조


## 주요 기능 및 화면
1. Spotify API 음악 검색
   - 검색창에 가수, 음악 제목 입력하여 검색할 수 있습니다.
   - 검색 결과는 RecyclerView를 활용하여 표시됩니다.
   - 각 항목에는 앨범 커버 이미지, 음악 제목, 가수가 표시됩니다.
     
2. 음악 저장
   - 리스트의 항목을 클릭하면 음악을 저장할 수 있습니다.
   - 저장된 음악은 Room DB를 통해 관리됩니다.
   - 1일 1음악을 등록할 수 있으며, 가장 최근에 등록한 음악만 저장하고 있습니다.
     
3. 달력 형태로 저장된 음악 표시
   - 달력형태로 저장된 음악의 앨범 커버 이미지를 표시합니다.
   - 앨범 커버 이미지를 클릭 시 상세페이지로 이동합니다.
  
4. 등록된 음악 상세 페이지
   - 등록된 음악의 간단한 메모를 남길 수 있습니다.
     
5. 유사한 음악 추천
   - Spotify API를 통해 등록된 음악에서 유사한 음악을 요청합니다.
