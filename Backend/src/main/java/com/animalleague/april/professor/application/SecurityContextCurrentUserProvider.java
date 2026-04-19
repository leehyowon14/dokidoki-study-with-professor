package com.animalleague.april.professor.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

    @Override
    public UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        UUID principalId = extractPrincipalId(authentication.getPrincipal());
        if (principalId != null) {
            return principalId;
        }

        return UUID.nameUUIDFromBytes(authentication.getName().getBytes(StandardCharsets.UTF_8));
    }

    private UUID extractPrincipalId(Object principal) {
        if (principal == null) {
            return null;
        }

        if (principal instanceof UUID uuid) {
            return uuid;
        }

        if (principal instanceof CharSequence sequence) {
            try {
                return UUID.fromString(sequence.toString());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        for (String methodName : List.of("id", "getId")) {
            UUID resolved = invokeUuidAccessor(principal, methodName);
            if (resolved != null) {
                return resolved;
            }
        }

        return null;
    }

    private UUID invokeUuidAccessor(Object principal, String methodName) {
        try {
            Method method = principal.getClass().getMethod(methodName);
            Object result = method.invoke(principal);
            if (result instanceof UUID uuid) {
                return uuid;
            }
            if (result instanceof CharSequence sequence) {
                return UUID.fromString(sequence.toString());
            }
            return null;
        } catch (NoSuchMethodException ignored) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException exception) {
            return null;
        }
    }
}
