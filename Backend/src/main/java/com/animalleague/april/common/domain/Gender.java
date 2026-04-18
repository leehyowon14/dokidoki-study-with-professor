package com.animalleague.april.common.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        return Arrays.stream(values())
            .filter(gender -> gender.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 gender 값입니다: " + value));
    }
}

