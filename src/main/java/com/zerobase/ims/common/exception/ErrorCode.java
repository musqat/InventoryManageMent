package com.zerobase.ims.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // user
    ALREADY_REGISTER_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다."),

    // login, logout
    LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "아이디나 패스워드를 확인해주세요."),
    LOGOUT_FAIL(HttpStatus.BAD_REQUEST, "로그아웃에 실패하였습니다."),

    // inventory
    NOT_FOUND_INVENTORY(HttpStatus.BAD_REQUEST, "인벤토리를 찾지 못했습니다."),
    NOT_FOUND_INVENTORY_USER(HttpStatus.BAD_REQUEST, "유저를 확인해주세요."),
    NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리를 찾지 못했습니다." ),
    NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "제품을 찾지 못했습니다." ),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "권한이 없습니다."),

    //invite
    NOT_FOUND_INVITER(HttpStatus.BAD_REQUEST, "초대할 유저를 찾지 못했습니다." ),
    INVALID_INVITE_STATUS(HttpStatus.BAD_REQUEST, "초대 상태를 확인해주세요." ),

    //ALERT
    NOT_FOUND_ALERT(HttpStatus.BAD_REQUEST, "알람을 찾지 못했습니다." ),

    // token
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    TOKEN_EXPIRE(HttpStatus.BAD_REQUEST, "Token이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
