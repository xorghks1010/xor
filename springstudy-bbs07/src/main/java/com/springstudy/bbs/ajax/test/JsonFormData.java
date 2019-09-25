package com.springstudy.bbs.ajax.test;

// 폼으로 부터 들어오는 json 타입을 객체로 변환할 클래스
public class JsonFormData {	
		
	private String id;
	private String pass;
	
	public JsonFormData() {}
	public JsonFormData(String id, String pass) {
		this.id = id;
		this.pass = pass;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}	
}
