package com.animalleague.april.common.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DialogueTriggerType {
    FIRST_VISIT("first_visit"),
    DAILY_FIRST_VISIT("daily_first_visit"),
    RETURN_TO_SCHOOL("return_to_school"),
    STUDY_START("study_start"),
    STUDY_RESUME("study_resume"),
    STUDY_HIDDEN_RETURN("study_hidden_return"),
    CHEER_UP("cheer_up"),
    STUDY_END("study_end"),
    EVENT_RESULT("event_result"),
    FINAL_RESULT("final_result");

    private final String value;

    DialogueTriggerType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static DialogueTriggerType fromValue(String value) {
        return Arrays.stream(values())
            .filter(type -> type.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 triggerType 값입니다: " + value));
    }
}

