package com.animalleague.april.unit.professor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

import com.animalleague.april.professor.application.SecurityContextCurrentUserProvider;

class SecurityContextCurrentUserProviderUnitTest {

    private final SecurityContextCurrentUserProvider provider = new SecurityContextCurrentUserProvider();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentUserIdReturnsUuidPrincipalAsIs() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(userId, "pw", "ROLE_USER"));

        assertThat(provider.currentUserId()).isEqualTo(userId);
    }

    @Test
    void currentUserIdReturnsUuidFromPublicAccessor() {
        UUID userId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken(new PublicPrincipalWithId(userId), "pw", "ROLE_USER")
        );

        assertThat(provider.currentUserId()).isEqualTo(userId);
    }

    @Test
    void currentUserIdFallsBackToAuthenticationNameWhenPrincipalIsNotResolvable() {
        String username = "alice";
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken(new Object(), "pw", "ROLE_USER") {
                @Override
                public String getName() {
                    return username;
                }
            }
        );

        assertThat(provider.currentUserId())
            .isEqualTo(UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void currentUserIdReturnsNullForAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
            new AnonymousAuthenticationToken(
                "key",
                "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
            )
        );

        assertThat(provider.currentUserId()).isNull();
    }

    public static final class PublicPrincipalWithId {
        private final UUID id;

        private PublicPrincipalWithId(UUID id) {
            this.id = id;
        }

        public UUID getId() {
            return id;
        }
    }
}
