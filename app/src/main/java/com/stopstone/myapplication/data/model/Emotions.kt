package com.stopstone.myapplication.data.model

enum class Emotions(val displayName: String) {
    HAPPY("행복"),
    SAD("슬픔"),
    ANGRY("분노"),
    JOYFUL("기쁨"),
    ANXIOUS("불안"),
    CALM("평온"),
    EXCITED("흥분"),
    BORED("지루함"),
    CONFIDENT("자신감"),
    FEARFUL("두려움"),
    GRATEFUL("감사"),
    FRUSTRATED("좌절"),
    HOPEFUL("희망"),
    LONELY("외로움"),
    LOVING("사랑"),
    JEALOUS("질투"),
    REGRETFUL("후회"),
    SURPRISED("놀람"),
    CONFUSED("혼란"),
    SATISFIED("만족");

    companion object {
        fun fromDisplayName(displayName: String): Emotions? {
            return values().find { it.displayName == displayName }
        }
    }
}