package com.springstudy.bbs.exception;

// 회원 로그인 시에 비밀번호가 맞지 않으면 발생하는 예외 처리 클래스 
public class MemberPassCheckFailException extends RuntimeException {
	public MemberPassCheckFailException(String message) {
		super(message);
	}
}