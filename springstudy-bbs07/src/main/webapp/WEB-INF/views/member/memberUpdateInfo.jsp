<%-- 회원정보 수정 폼에서 요청한 데이터를 받아 화면에 출력하는 View 페이지 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<article>
	<div id="memberJoinForm">
	<h3 id="joinFormTitle">회원 정보 수정 확인</h3>
		<div id="memberInputDefault">			
			<div class="memberInputText">
				<span class="memberSpan">* 이&nbsp;&nbsp;름 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.name }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 아이디 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.id }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 비밀번호 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.pass }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 우편번호 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.zipcode }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 자택주소 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.address1 }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">상세주소 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.address2 }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 이 메 일 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.email }</span>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 휴 대 폰 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.mobile }</span>
			</div>
		</div>
		<div id="memberInputOption">
			<div class="memberInputText">
				<span class="memberSpan">메일 수신여부 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.emailGet ? 
					"메일 받음" : "메일 받지 않음" }</span></div>
			<div class="memberInputText">
				<span class="memberSpan"> 자택전화 : </span>
				<span class="inputInfoSpan">${ sessionScope.m.phone }</span>
			</div>
		</div>
		<div class="formButton">
			<input type="button" value="수정하기"
				onclick="document.location.href='memberUpdateForm?id=${sessionScope.m.id}'"/>
			<input type="submit" value="수정완료" 
				onclick="document.location.href='memberUpdateResult'"/>
		</div>						
	</div>	
</article>