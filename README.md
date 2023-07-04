# Week1 - LuckyCardGame: 박동현

## 목차
1. [게임 보드 만들기](#1.-게임-보드-만들기)
2. [럭키 카드 클래스 구현하기](#2.-럭키-카드-클래스-구현하기)
3. [카드 나눠주기](#3.-카드-나눠주기)
4. [게임 로직 구현하기](#4.-게임-보드-만들기)


## 1. 게임 보드 만들기
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


## 2. 럭키 카드 클래스 구현하기

## 3. 카드 나눠주기

## 4. 게임 로직 구현하기