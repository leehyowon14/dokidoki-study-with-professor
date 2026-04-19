package com.animalleague.april.common.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ScriptLineKind {
    NARRATION("narration"),
    DIALOGUE("dialogue"),
    STAGE_DIRECTION("stage_direction"),
    INNER_MONOLOGUE("inner_monologue");

    private final String value;

    ScriptLineKind(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static ScriptLineKind fromValue(String value) {
        return Arrays.stream(values())
            .filter(kind -> kind.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 script line kind 값입니다: " + value));
    }
}

