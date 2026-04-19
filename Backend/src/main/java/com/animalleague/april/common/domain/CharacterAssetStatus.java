package com.animalleague.april.common.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CharacterAssetStatus {
    PENDING("pending"),
    READY("ready");

    private final String value;

    CharacterAssetStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static CharacterAssetStatus fromValue(String value) {
        return Arrays.stream(values())
            .filter(status -> status.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 characterAssetStatus 값입니다: " + value));
    }
}

