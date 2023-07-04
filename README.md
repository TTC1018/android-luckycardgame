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

## Task 4
