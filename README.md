### db 초기 코드
- SQL 폴더 -> init.sql

# 구현 목록

### 1.헤더

- 로고
  - 메인 페이지로 이동
- 회원
  - 글쓰기 버튼 제공
  - 아이디 버튼 제공
    - 내 벨로그 / 임시글/ 설정 / 로그아웃
- 비회원
  - 로그인 버튼 제공

<br/>

### 2. 메인페이지 (/)

- 출간 & 공개된 게시물 리스트 보여주기
- 불러오기 버튼
  - 비동기를 통해서 추가적으로 게시물을 가져옴
- 정렬
  - 최신순, 조회순으로 정렬 가능
- 게시글의 제목 & 내용
  - 상세페이지 이동
- 게시글의 작성자
  - 작성자의 개인 블로그로 이동

<br/>

### 3. 회원가입 (/userreg)

- ID / PW / PW check / NAME / EMAIL 입력
- ID & EMAIL
  - api를 통해 패턴 및 중복 체크 진행
- 입력을 안한 항목 또는 형식이 맞지 않는 입력
  - 회원가입 버튼이 눌리지 않게 구현

<br/>

### 4. 웰컴 페이지 (/welcome)

- 회원가입 완료후 이동되는 페이지
- 가입 완료 문구 출력
- 메인페이지 또는 로그인페이지로 이동하는 버튼 제공

<br/>

### 5. 로그인 (/login)

- ID / PW 입력
- 성공시 메인페이지(/) 로 이동
- 실패시 에러 메세지 출력

<br/>

### 6. 새 글 작성페이지 (/post/write)

- 제목 / 태그 / 출간 여부 / 내용 입력
- 제목
  - 필수 요소로 입력없이 제출하면 에러 문구 출력
- 태그
  - 공백으로 구분하여 구분
- 출간 여부
  - 출간
    - 출간 설정 페이지로 이동
  - 임시글
    - 임시글로 저장후, 메인페이지로 이동
- 내용
  - 필수 요소로 입력 없이 제출하면 에러 문구 출력
  - CKEditor 이용해서 구현

<br/>

### 7. 출간 설정 페이지 (/post/publish)

- 메인 이미지 / 공개 여부 / 시리즈 입력
- 시리즈 추가하기
  - 추가하기 기능을 통해 새로운 시리즈 추가 가능
- 출간하기 버튼
  - 출간 완료시, 메인페이지로 이동

<br/>

### 8. 임시 저장 글 페이지 (/saves)

- 임시글로 지정한 내 게시글을 보여줌
- 수정
  - 게시글 작성 페이지로 이동해 이어서 작성
- 삭제
  - 해당 임시글 삭제 처리
- <br/>

### 9. 게시글 상세 페이지 (/@{username}/{postId})

#### 9-1. 게시글 본문
  
- 제목 / 작성자 / 태그 / 시리즈 / 조회수 / 작성일 / 내용 보여줌
- 작성자 버튼
  - 해당 유저의 블로그 페이지로 이동
- 시리즈 버튼
  - 해당 시리즈 상세 페이지로 이동
- 수정 버튼
  - 작성자에게만 보여짐
  - 수정
    - 글 작성 페이지로 이동해서 수정
  - 삭제
    - 글 삭제
- 조회수
  - 작성자 외 유저에 한에서만 조회수 증가

#### 9-2. 댓글

- 입력
  - 최대 200자 작성 가능
  - 비회원이 등록하기 버튼 누룰 시 로근인 페이지로 이동
- 댓글 요소
  - 작성자 / 내용 / 작성일 / 대댓글 보기 / 대댓글 등록하기
- 작성자만 수정, 삭제 버튼 보여줌
- 

<br/>

---

## 프로젝트 요구사항 체크리스트

### 1. 회원가입
- [x] 회원 가입폼 구현
- [x] ID, Email 중복 체크 API 구현
- [x] 회원 등록 기능 구현
- [x] 회원 가입 후 로그인 폼으로 이동

### 2. 로그인
- [x] 로그인 폼 구현
- [x] 로그인 기능 구현
- [x] 로그인 성공 후 메인 페이지로 이동 (/)
- [x] 로그인 실패 후 다시 로그인 폼으로 이동 (오류 메시지 출력)
- [x] Spring Security를 이용한 로그인 구현 (Form Login, _JWT Login_, OAuth2 로그인)

### 3. 사이트 상단
- [x] 사이트 로고 좌측 상단에 배치
- [x] 로그인 링크 또는 사용자 정보 우측 상단에 표시
    - [x] 로그인하지 않았을 경우 로그인 링크 표시
    - [x] 로그인했을 경우 사용자 이름 표시
    - [x] 사용자 이름 클릭 시 설정, 사용자 블로그 이동 링크, 임시저장글 목록 보기, 로그아웃 표시

### 4. 로그아웃
- [x] 로그아웃 기능 구현

### 5. 메인 페이지 (/)
- [x] 블로그 글 목록 보기 (최신 순, 조회수 높은 순, 즐겨찾기 순)
  - 최신순, 조회순만 구현
- [x] 페이징 처리 또는 무한 스크롤 구현
- [ ] 제목, 내용, 사용자 이름으로 검색 기능 구현

### 6. 블로그 글 쓰기
- [x] 블로그 제목, 내용, 사진 등을 입력하여 글 작성
- [x] "출간하기" 버튼 클릭 시 블로그 썸네일, 공개 유무, 시리즈 설정 후 글 등록
- [x] "임시저장" 버튼 클릭 시 글 임시 저장
  - 라디오 버튼으로 출간, 임시저장 선택하도록 구현

### 7. 임시 글 저장 목록 보기
- [x] 로그인 후 임시 글 저장 목록 보기 링크 표시
- [x] 임시 글 저장 목록에서 글 제목 클릭 시 글 수정 가능
  - 버튼으로 구현
- [x] 임시 글 수정 페이지에서 "임시저장", "출간하기" 버튼 구현

### 8. 특정 사용자 블로그 글 보기 (/@사용자아이디)
- [x] 사용자 정보 보기
  - 프로필, 블로그 이름, 블로그 설명
- [ ] 사용자가 쓴 글 목록 보기 (최신 순, 조회수 높은 순, 즐겨찾기 순)
- [x] 페이징 처리 또는 무한 스크롤 구현
- [ ] 사용자의 태그 목록 보기 (태그당 글의 수 표시)
- [ ] 제목, 내용으로 검색 기능 구현

### 9. 시리즈 목록 보기
- [x] 시리즈 목록 보기 구현
- [x] 벨로그의 시리즈 기능을 확인해보세요. 글쓰고 출간하기를 하면 보여짐

### 10. 시리즈 제목 클릭시 시리즈에 포함된 블로그 글 목록 보여주기
- [x] 시리즈에 속한 블로그 글 목록 보기

### 11. 블로그 글 상세 보기
- [x] 메인 페이지에서 제목 클릭 시 블로그 글 상세 보기
- [x] 특정 사용자 블로그에서 제목 클릭 시 블로그 상세 보기
- [x] 시리즈에 속한 블로그 글 목록에서 제목 클릭 시 블로그 글 상세 보기

### 12. 사용자 정보 보기
- [x] 로그인한 사용자 이름 클릭 시 사용자 정보 보기
- [x] 사용자 이름, 이메일 출력
- [x] 회원 탈퇴 링크 제공

### 13. 회원 탈퇴
- [x] 회원 탈퇴 폼 구현
- [x] 회원 탈퇴 확인 후 회원 정보 삭제

### 14. 댓글 목록 보여주기
- [x] 블로그 글 상세보기 하단에 댓글 목록 표시
- [x] 댓글과 대댓글 최신 순으로 표시
  - 기본 댓글 =>  최신순
  - 대댓글 => 오래된 순으로 정령
- [x] 댓글 최대 5개 표시 및 페이징 처리

### 15. 댓글 달기
- [x] 블로그에 댓글 달기 기능 구현
- [x] 댓글에 대댓글 달기 기능 구현

### 16. 블로그 글 좋아요 기능
- [ ] 블로그 글에 좋아요 기능 구현

### 17. 추가 기능
- [ ] 벨로그 사이트 분석 및 추가 기능 구현
- [ ] 벨로그와 비슷하거나 더 편리하게 구현
- [ ] 프론트 개발 (React.js 등) 학습 및 팀 프로젝트에서 프론트 개발 시도