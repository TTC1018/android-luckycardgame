# Week1 - LuckyCardGame: 박동현

## 목차
1. [게임 보드 만들기](#Task-1)
2. [럭키 카드 클래스 구현하기](#Task-2)
3. [카드 나눠주기](#Task-3)
4. [게임 로직 구현하기](#Task-4)
5. [게임규칙 추가하기](#Task-5)
6. [결과 화면 만들기](#Task-6)

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

## Task 5
### 카드 뒤집기 구현
* `Card` 클래스에 뒤집힘 상태 정의 (`var flipped: Boolean`)
* ClickListener를 선언, 실제로 model의 값도 함께 갱신하고 databinding으로 연동

#### OnFlipCardListener
* `onFlipCard(userId: Int, cardPos: Int)`: 특정 유저의 카드를 뒤집는 함수. 현재 뒤집을 수 있는 카드라면 뒤집는다. (가장 왼쪽 혹은 오른쪽이면)
* `LuckyGame` 및 `MainActivityViewModel` 이 상속받아 함수 정의

#### OnCardClickListener
* `onCardClick(userId: Int, pos: Int)`: 카드 ViewHolder를 클릭했을 때 실행하는 함수. `onFlipCard`와 adapter의 `notify...`을 호출

### 승자 판단 구현
#### `Card` 클래스에 매칭된 카드임을 구분하는 속성값 선언
* `matched: Boolean`: 동일한 카드 3장 매칭될 경우 `true`로 변경

#### `LuckyGame` 클래스에 게임 종료 상태 변수 및 승자 변수 선언
* `endFlag: Boolean`: 게임이 끝난 경우 `true`로 변경됨
* `winners: Set<Int>`: 승자의 `userId`가 담김. 승자가 없는 경우 비어 있음

#### 턴 개념 추가
**변수**  
* `chanceCounter: IntArray`: 3으로 초기화되어 있으며, 카드를 뒤집을 때마다 `userId`에 맞는 위치가 1씩 줄어듦. 턴이 끝나면 다시 3으로 초기화
* `flippedCounter: Array<Map<Int, Int>>`: `userId`별로 현재까지 뒤집은 카드의 번호를 기록하는 변수

**함수**  
* `resetRound(userCount: Int)`: `flippedCounter`에 기록된 뒤집힌 카드 정보를 초기화
* `clearTurn(userCount: Int)`: `chanceCounter`에 기록된 카드 뒤집기 기회를 3으로 초기화
* `checkPicked(userId: Int, cardIdx: Int)`: 유저 카드 목록의 `cardIdx`에 위치한 카드와 동일한 번호를 갖는 뒤집힌 3장의 카드가 존재하면, 해당 카드들의 인덱스를 리턴
* `isEndOfTurn(): Boolean`: 모든 유저가 카드 뒤집기 기회를 다 소모했거나, 가능한 뒤집을 카드가 없을 때 `true` 리턴
* `checkTurnContinue(): Set<Int>`: 게임이 아예 종료되는지를 판단하고, 승자들의 `userId`를 `Set`에 담아 리턴

#### `LuckyGame` 클래스에 승자 판단 함수 선언
* `findWinner(): Set<Int>`: 7 카드 보유자 혹은 합/차가 7이 되는 카드 보유자들을 `Set`에 담아 리턴
* `findSevenSumOwners(): Collection<Int>`: 합/차가 7이 되는 카드 보유자들을 조합으로 찾음
* `findSevenOwner(): Collection<Int>`: 7카드 세장을 보유한 유저들을 찾음


## Task 6
### Main 화면 - 결과 화면 연결
* `registerForActivityResult`로 연결: deprecated된 `startActivityForResult` 대신 활용.  
승자 확인 화면의 Result로 `userCount`를 전달받아 그에 맞게 게임을 리셋 (근데 이제보니 `LuckyGame`에 저장된 변수라서 굳이 전달받을 필요가 있나 싶긴 하다.)
* `startResult: ActivityResultLauncher`: ViewModel의 `endFlag`가 `true`가 되면, 승자가 존재하는지 확인 후에 존재한다면 `startResult`의 `launch` 함수로 GameResultActivity`로 이동

### 승자 판단 기능 추가 구현
* `matchedCardsOfUsers: MutableMap<Int, Set<Int>`: 유저 별 3장 매칭된 카드의 번호를 기록

### 결과 화면 구현 - `GameResultActivity` & `GameResultComposable`
* `GameResultViewModel`: View가 화면에 그려지기 전에 `LuckyGame`의 게임 결과 관련 데이터를 참조하여 `StateFlow`에 보관
* `onBackPressedDispatcher`: deprecated된 `onBackPressed`를 대신해 사용. 결과화면에서 백버튼으로 Main 화면으로 가지 못하게 차단.
* 이외 `GameResultActivity`의 레이아웃은 Compose로 구현