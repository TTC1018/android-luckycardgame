# Week1 - LuckyCardGame: 박동현

## 목차
1. [게임 보드 만들기](#Task-1)
2. [럭키 카드 클래스 구현하기](#Task-2)
3. [카드 나눠주기](#Task-3)
4. [게임 로직 구현하기](#Task-4)

## Task 1
* `ConstraintLayout` 활용으로 최대한 단일 계층으로 구현

### 1-1. 전체 padding 및 margin 구성
- `Guideline` 사용하여 상하좌우 간격 설정
- 화면 별 크기 대응위해 `layout_constraintGuide_percent`로 설정

### 1-2. 5개의 회색 View 구현
- 배경을 갖는 `TextView`로 선언
- 글자 정렬은 `textAlignment`에 `viewStart` 설정
- 화면 크기 고려하여 `layout_constraintHeight_percent`로 높이 설정

### 1-3. 최상단(노란색) 및 최하단(진회색) View 구현
#### a. 공통 구현
- 배경만 가지는 `View` 로 선언
- 배경은 둥근 `radius` 적용한 `drawable` 사용

#### b. 최하단 진회색 View 동적 크기 변경
- 4, 5번째 `TextView`에 `Barrier` 설정
- 4번째는 `INVISIBLE`로, 5번째는 `GONE`으로 처리하면 요구사항에서 보이는 만큼 View 크기 확장

## Task 2
### AnimalType 클래스 구현
- `enum class`로 선언 (DOG, CAT, COW)
- 유니코드 저장할 내부 변수로 `val emoji: String` 선언

### Card 클래스 구현
- 내부 변수로 `val type: AnimalType`을 가짐
- 숫자를 저장하는 변수로는 `val num: Int` 선언
- 콘솔 출력이 용이하도록 `toString()` 오버라이드
- 생성자 파라미터 미전달 시, 랜덤으로 선택되도록 Default값 설정

### 출력 테스트
- `Log` 함수로 Logcat에서 확인  
<img width="973" alt="image" src="https://github.com/TTC1018/android-luckycardgame/assets/39405316/4b04cd59-c7dc-4b68-ad97-8ff346ee87bb">

## Task 3
### User 클래스 구현
- `userId: Int`: 유저의 고유 아이디 (현재는 단순히 순서로 설정되어 있음)
- `cards: List<Card>`: 카드 목록. `var` 변수지만, `List` 타입으로 직접적인 수정은 불가능

### LuckyGame 클래스 구현
#### 변수
- `cards: Array<Array<User>>`: 레포지토리에서 생성된 전체 카드 목록을 저장. 생성자 파라미터
- `userCount: Int`: 전체 인원 수를 저장. 기본 값 3으로 설정됨
- `users: Array<User>`: 전체 유저를 저장. `MAX_USER`(과제 요구사항에서는 5) 만큼의 유저를 보관
- `shuffledCards: List<Card>`: 섞인 카드들을 참조하는 변수
- `leftCards: List<Card>`: 유저들에게 나눠주고 남은 카드를 참조하는 변수

#### 함수
- `reGame(userCount: Int)`: 카드를 다시 섞고 유저들에게 카드 목록을 설정. `LuckyGame` 클래스의 함수 대부분을 호출.
- `shuffleAllCards(userCount: Int)`: 전체 카드 목록을 복사하고 섞음
- `clearAllHands()`: 모든 `User`의 카드 목록을 비움. `cards` 변수에 `emptyList()`를 할당
- `makeUserWithCards()`: 섞은 카드를 각 `User`에게 나눠줌. 남은 카드 목록도 `leftCards`에 할당

#### companion object
- 유저 별 카드 개수, 남은 카드 수를 참고할 수 있는 `Map`을 선언
- `cardCountMap`: 유저 별 나눠주어야 하는 카드 개수를 저장
- `leftCountMap` : 유저 별로 나눠주고 남은 카드 개수를 저장

### 카드 목록 출력
#### 카드 가로 길이 설정
- 최상위 `ConstraintLayout`의 `width` 값의 일정 비율로 카드 가로 길이를 설정

#### 카드 겹치기
- `RecyclerView.ItemDecoration`을 활용하여 카드간 간격 조정
- 간격 또한 최상위 `ConstraintLayout`의 `width` 값 고려해서 일정 비율로 설정
- 최하단 남은 카드 간격은 다른 계산 방식으로 `ItemDecoration` 설정

- **발생 이슈**
    * 카드 목록 갱신할 때마다 카드 가로 길이가 들쭉날쭉해지는 것을 확인
    * `ViewHolder`내의 `init` 블록 `doOnAttach` 에서 가로 길이를 설정하고 있었는데, 몇몇 ViewHolder는 그대로 재활용되다보니 이전 크기의 카드가 그대로 남게됨.
    * 우선은 ListAdapter의 `areItemsTheSame`과 `areContentsTheSame`을 모두 `false`를 리턴하게 하여 무조건 RecyclerView 전체를 갱신하도록 변경

## Task 4
### LuckyGame 추가 기능 구현
* `sortUserCardsByNum()`: 사용자의 카드를 번호 오름차순으로 정렬
* `sortLeftCardsByNum()`: 남은 카드를 번호 오름차순으로 정렬
* `checkUserHasTripleCards(userId: Int): Boolean`: 특정 User ID를 갖는 사용자가 같은 번호 카드 3장을 갖는지 확인
* `findTripleCardsUsers(): List<Int>`: 전체 사용자 중 같은 번호 카드 3장을 갖는 모든 사용자를 `List`에 담아 리턴
* `compareTwoUsersCardWithLeftCard(`  
  `	userOneId: Int,`  
  `	userTwoId: Int,`
  `	leftCard: Card = leftCards.random()`
  `): Boolean`: 특정 User ID를 갖는 두 사용자의 최소/최대 카드 번호와 랜덤으로 선택된 남은 카드가 같은 번호를 갖는지 확인하고 `Boolean`으로 결과를 리턴

### 테스트 케이스 작성
* 테스트 케이스 네이밍
  * 메소드명_기존상태_호출뒤예상결과

* 작성한 테스트 케이스 목록
  * 나눠준 카드와 남는 카드를 합쳤을 때 타입별로 12장씩 존재하는지
  * 참여자가 3명인 경우에 12번 카드가 존재하지 않는지
  * 이전에 나눠준 카드들과 다른 카드 목록을 갖는 유저가 한명이라도 있는지(카드가 섞였는지)
  * 나눠주고 남은 카드 수가 올바른지
  * 각 사용자가 동일한 카드 수를 갖는지
  * 유저 별 카드를 번호 기준으로 정렬 후에 비내림차순인지
  * 남은 카드를 번호 기준으로 정렬 후에 비내림차순인지
  * 같은 번호 카드 세장을 가진 유저가 있는지
  * 두 유저가 최소/최대 번호가 같은 카드를 가질 때, 랜덤 카드를 포함해 셋이 비교시 같은지 판단할 수 있는지