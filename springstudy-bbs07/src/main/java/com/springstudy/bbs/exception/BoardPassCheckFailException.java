package com.springstudy.bbs.exception;

// 게시 글 수정, 삭제 시 비밀번호가 맞지 않으면 발생하는 예외처리 클래스 
public class BoardPassCheckFailException extends RuntimeException {
	public BoardPassCheckFailException(String message) {
		super(message);
	}
}