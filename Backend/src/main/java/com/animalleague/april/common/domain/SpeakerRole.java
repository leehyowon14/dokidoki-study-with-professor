package com.animalleague.april.common.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SpeakerRole {
    PROFESSOR("professor"),
    PROTAGONIST("protagonist");

    private final String value;

    SpeakerRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SpeakerRole fromValue(String value) {
        return Arrays.stream(values())
            .filter(role -> role.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 speakerRole 값입니다: " + value));
    }
}

