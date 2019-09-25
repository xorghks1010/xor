package com.springstudy.bbs.exception;

// 회원 로그인 시에 아이디가 존재하지 않으면 발생하는 예외처리 클래스
public class MemberNotFoundException extends RuntimeException {
	public MemberNotFoundException(String message) {
		super(message);
	}
}
