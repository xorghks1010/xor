<%--
	회원 가입시 아이디 중복검사 요청에 대한 처리 결과를 출력할 View 페이지
	이 페이지는 새창으로 실행되고 중복 아이디 체크를 할 수 있는 폼을 제공한다.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link type="text/css" href="resources/css/member.css" rel="stylesheet" />
<script type="text/javascript" src="resources/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="resources/js/member.js"></script>
<title>중복 아이디 체크</title>
</head>
<body>
	<div id="idCheckForm">
	<c:choose>
		<c:when test="${ overlap }" >
			<h3>사용할 수 없는 아이디</h3>
			<div class="memberInputText">
				입력하신 ${ id }는 이미 사용 중인 아이디 입니다.
			</div>
			<div class="memberInputText">다른 아이디를 선택해 주세요</div>		
			<form action="overlapIdCheck.mvc" name="idCheckForm" 
				method="post" id="idCheckForm">
				<div class="memberInputText">
					<span class="checkFormSpan">아이디 : </span>
					<input type="text" name="id" id="checkId" size="15"/>
					<input type="submit" value="중복확인" />
				</div>
			</form>
		</c:when>
		<c:otherwise>
			<h3>사용할 수 있는 아이디</h3>
			<div class="memberInputText">
				입력하신 ${ id }는 사용할 수 있는 아이디 입니다.</div>
			<div class="memberInputText">
				<input type="button" value="${ id }을(를) 아이디로 사용하기" 
					id="btnIdCheckClose" data-id-value="${ id }"/>
			</div>
		</c:otherwise>		
	</c:choose>
	</div>
</body>
</html>