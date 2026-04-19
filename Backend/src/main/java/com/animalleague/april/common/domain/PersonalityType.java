package com.animalleague.april.common.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PersonalityType {
    GENTLE("gentle"),
    TSUNDERE("tsundere"),
    ENGLISH_MIX("english_mix"),
    SHY("shy");

    private final String value;

    PersonalityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static PersonalityType fromValue(String value) {
        return Arrays.stream(values())
            .filter(type -> type.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 personalityType 값입니다: " + value));
    }
}

