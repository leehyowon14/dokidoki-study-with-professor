package com.animalleague.april.auth.application;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String loginId, String name) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
