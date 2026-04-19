package com.animalleague.april.auth.api;

import java.time.LocalDate;
import java.util.UUID;

import com.animalleague.april.auth.domain.User;

public record UserSummaryResponse(UUID id, String name, String loginId, LocalDate examEndDate) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
            user.getId(),
            user.getName(),
            user.getLoginId(),
            user.getExamEndDate()
        );
    }
}
