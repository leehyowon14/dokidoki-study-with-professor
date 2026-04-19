package com.animalleague.april.auth.application;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalleague.april.auth.domain.User;
import com.animalleague.april.auth.infrastructure.UserRepository;
import com.animalleague.april.common.api.ApiException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupPolicy signupPolicy;
    private final LoginPolicy loginPolicy;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        SignupPolicy signupPolicy,
        LoginPolicy loginPolicy
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.signupPolicy = signupPolicy;
        this.loginPolicy = loginPolicy;
    }

    @Transactional
    public User signup(String name, String loginId, String password, LocalDate examEndDate) {
        signupPolicy.validate(name, loginId, password, examEndDate);

        if (userRepository.existsByLoginId(loginId)) {
            throw duplicateLoginIdException();
        }

        User user = User.create(loginId, name, passwordEncoder.encode(password), examEndDate);
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            if (userRepository.existsByLoginId(loginId)) {
                throw duplicateLoginIdException();
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public User login(String loginId, String password) {
        loginPolicy.validate(loginId, password);

        User user = userRepository.findByLoginId(loginId)
            .orElseThrow(() ->
                new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_CREDENTIALS",
                    "로그인 ID 또는 비밀번호가 올바르지 않습니다."
                )
            );

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "로그인 ID 또는 비밀번호가 올바르지 않습니다."
            );
        }

        return user;
    }

    private ApiException duplicateLoginIdException() {
        return new ApiException(
            HttpStatus.CONFLICT,
            "DUPLICATE_LOGIN_ID",
            "이미 사용 중인 로그인 ID입니다."
        );
    }
}
