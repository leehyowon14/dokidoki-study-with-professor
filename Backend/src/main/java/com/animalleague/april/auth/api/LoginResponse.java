package com.animalleague.april.auth.api;

public record LoginResponse(UserSummaryResponse user, Object activeSession) {}
