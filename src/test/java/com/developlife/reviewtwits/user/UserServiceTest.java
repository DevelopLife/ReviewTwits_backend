package com.developlife.reviewtwits.user;

import com.developlife.reviewtwits.ApiTest;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.AccountIdAlreadyExistsException;
import com.developlife.reviewtwits.exception.AccountIdNotFoundException;
import com.developlife.reviewtwits.exception.AccountPasswordWrongException;
import com.developlife.reviewtwits.exception.PasswordVerifyException;
import com.developlife.reviewtwits.message.request.RegisterUserRequest;
import com.developlife.reviewtwits.service.UserService;
import com.developlife.reviewtwits.type.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author ghdic
 * @since 2023/02/24
 */
public class UserServiceTest extends ApiTest {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RegisterUserRequest registerUserRequest = UserSteps.회원가입요청_생성();
    private final RegisterUserRequest registerAdminRequest = UserSteps.회원가입요청_어드민_생성();
    @Autowired
    public UserServiceTest(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setting() {
        // 일반유저, 어드민유저 회원가입 해두고 테스트 진행
        userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        userService.register(registerAdminRequest, UserSteps.어드민유저권한_생성());
    }

    @Test
    void 회원등록_성공() {
        final User user = userService.getUser(registerUserRequest.accountId());

        // 입력한 정보로 가입한 유저가 없는 경우 확인
        assertThat(user).isNotNull();
        // 입력정보 제대로 들어갔나 확인
        assertThat(registerUserRequest.nickname().equals(user.getNickname())).isTrue();
        // 비밀번호 해시화 확인
        assertThat(passwordEncoder.matches(registerUserRequest.accountPw(), user.getAccountPw())).isTrue();
    }

    @Test
    void 회원등록_실패() {
        assertThrows(AccountIdAlreadyExistsException.class, () -> {
            userService.register(registerUserRequest, UserSteps.일반유저권한_생성());
        });

        assertThrows(PasswordVerifyException.class, () -> {
            userService.register(UserSteps.회원가입요청_비밀번호규칙_불일치(), UserSteps.일반유저권한_생성());
        });
    }

    @Test
    void 비밀번호규칙확인() {
        // 비밀번호 규칙: 6자 이상 1개 이상의 알파벳, 1개 이상의 숫자, 1개 이상의 특수문자 @$!%*#?&
        // 비밀번호 규칙 확인 - 성공
        UserSteps.규칙이맞는비밀번호들().stream().forEach(password -> {
            assertThat(UserService.passwordVerify(password)).isTrue();
        });
        // 비밀번호 규칙 확인 - 알파벳 x
        // 비밀번호 규칙 확인 - 숫자 x
        // 비밀번호 규칙 확인 - 특수문자 x
        // 비밀번호 규칙 확인 - 알파벳 대소문자 구분
        // 비밀번호 규칙 확인 - 비밀번호 길이 경계테스트
        UserSteps.규칙이틀린비밀번호들().stream().forEach(password -> {
            assertThat(UserService.passwordVerify(password)).isFalse();
        });
    }

    @Test
    void 로그인() {
        final User user_success = userService.login(UserSteps.로그인요청_생성_성공());

        // 로그인 성공
        assertThat(user_success).isNotNull();
        // 로그인 실패 - 비밀번호 불일치
        assertThrows(AccountPasswordWrongException.class, () -> {
            userService.login(UserSteps.로그인요청_생성_비밀번호불일치());
        });
        // 로그인 실패 - 아이디 존재x
        assertThrows(AccountIdNotFoundException.class, () -> {
            userService.login(UserSteps.로그인요청_생성_아이디불일치());
        });
    }

    @Test
    void 권한부여() {
        userService.grantedAdminPermission(registerUserRequest.accountId());
        User user = userService.getUser(registerUserRequest.accountId());
        assertThat(user.getRoles().contains(UserRole.ADMIN)).isTrue();
    }

    @Test
    void 권한압수() {
        userService.confiscatedAdminPermission(registerAdminRequest.accountId());
        User user = userService.getUser(registerAdminRequest.accountId());
        assertThat(user.getRoles().contains(UserRole.ADMIN)).isFalse();
    }
}
