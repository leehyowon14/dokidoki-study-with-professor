package com.animalleague.april.auth.api;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalleague.april.auth.application.AuthService;
import com.animalleague.april.auth.application.AuthenticatedUser;
import com.animalleague.april.auth.domain.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserEnvelope> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(
            request.name(),
            request.loginId(),
            request.password(),
            request.examEndDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new UserEnvelope(UserSummaryResponse.from(user)));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        User user = authService.login(request.loginId(), request.password());
        AuthenticatedUser principal = new AuthenticatedUser(user.getId(), user.getLoginId(), user.getName());

        UsernamePasswordAuthenticationToken authentication =
            UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        httpRequest.getSession(true);
        httpRequest.changeSessionId();
        HttpSession session = httpRequest.getSession(false);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return new LoginResponse(UserSummaryResponse.from(user), null);
    }
}
