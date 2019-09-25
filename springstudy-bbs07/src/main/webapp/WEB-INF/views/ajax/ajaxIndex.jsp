<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link rel="stylesheet" type="text/css" href="../resources/css/index.css" />
<link rel="stylesheet" type="text/css" href="../resources/css/board.css" />
<link rel="stylesheet" type="text/css" href="../resources/css/member.css" />
<script src="../resources/js/jquery-3.2.1.min.js"></script>
<article>
<script>
	$(document).ready(function() {
		
		// 로그인 폼에서 입력된 데이터를 post 방식으로 요청하는 ajxa 메서드 
		$("#reqBodyData").click(function() {
			
			var data = $("#loginForm").serialize();
			$("#result").empty();	
			$.post("login", data, function(responseData, textStatus, xhr) {
				$("#result").html(responseData);
			});
		});
		
		/* 로그인 폼에 입력된 데이터를 json 방식으로 변환해 post 방식으로
		 * 요청하는 메서드 응답 데이터를 json 형식으로 받는다.
		 **/
		$("#jsonData").click(function() {
			$("#result").empty();
			var jsonObj = {
					id: $("#id").val(),
					pass: $("#pass").val()
			}
			
			$.ajax({
				type: "post",
				url: "loginJson.json",
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				/* json 데이터를 문자열 데이터로 변환해 전송해야 한다.
			 	 * 그렇지 않으면 json 형식으로 요청 본문에 추가되는 것이
			 	 * 아니라 [object Object] 문자열 형태로 요청 본문에
			 	 * 추가되기 때문에 서버에서 처리할 때 문제가 발생한다.
			 	 *
			 	 * JSON.stringify(Object) 함수는 자바스크립트 객체를
			 	 * json 형식의 문자열로 변화하고 JSON.parse("string")
			 	 * 함수는 json 형식의 문자열을 자바스크립트 객체로 변환해 준다.
			 	 **/
				data: JSON.stringify(jsonObj),					
				processData: false,
				success: function(responseData) {
					
					/* 응답 받은 아래 json 데이터에서 데이터를 추출한다. 
					 * { 
					 *		"success":1, 
					 *		"greeting":"안녕하세요 admin님!", 
					 *		"message":"로그인 성공"
					 *	}						 
					 * responseData[]
					 **/						
					var success = responseData.success;
					var greeting = responseData.greeting;
					var message = responseData.message;
					var result = "succcess : " + success 
						+ "<br/>message : " + message 
						+ "<br/>greeting : " + greeting;
					$("#result").html(result);
				},
				error: function(xhr, textStatus, errorThrown) {
					alert(textStatus + ", " + errorThrown);
				}
			});				
		});
		
		/* 게시 글 리스트를 post 방식으로 요청해 json 데이터로 받는 메서드
		 * 응답 데이터를 여러 개의 게시 글 정보가 들어있는 json 배열로 받는다.
		 **/
		$("#boardList").click(function() {
			$("#result").empty();
			
			$.post("boardList.ajax", 
				function(responseData, textStatus, xhr) {
								 
				 var divBody = $("<div class='body'>");
				 divBody.append("<h2>게시 글 리스트</h2>");
				 var tableTag = $("<table class='listTable'></table>");
				 var trTag = $(
						"<tr>"
						+	"<th class='listThNo'>NO</th>"
						+	"<th class='listThTitle'>제목</th>"
						+	"<th class='listThWriter'>작성자</th>"
						+	"<th class='listThRegDate'>작성일</th>"
						+	"<th class='listThReadCount'>조회수</th>"
					+"</tr>");
				 tableTag.append(trTag);
				
				// 게시 글 리스트를 순회하면서 테이블에 게시 글 정보를 추가
				$.each(responseData, function(index, item) {
					
					trTag = $("<tr class='listTr'></tr>");
					
					var tdTag = $("<td class='listTdNo'></td>");
					tdTag.html(item.no);
					trTag.append(tdTag);
					
					tdTag = $("<td class='listTdTitle'></td>");
					tdTag.html(item.title);
					trTag.append(tdTag);
					
					tdTag = $("<td class='listTdWriter'></td>");
					tdTag.html(item.writer);
					trTag.append(tdTag);
					
					tdTag = $("<td class='listTdRegDate'></td>");
					tdTag.html(item.regDate);
					trTag.append(tdTag);
					
					tdTag = $("<td class='listTdReadCount'></td>");
					tdTag.html(item.readCount);
					trTag.append(tdTag);					
					tableTag.append(trTag);					
				}); // end $.each(responseList, function(index, item) { })
				
				// 완성된 테이블을 div에 추가하고 페이지에 반영한다.
				divBody.append(tableTag);
				$("#result").append(divBody);
				
			}); // end $.post()
			
		}); // end $("#boardList").click(function() { })
	});
</script>
	<h2>스프링 ajax 지원</h2>	
	<form name="login" method="post" id="loginForm">
		아이디 : <input type="text" name="id" id="id"/><br/>
		비밀번호 : <input type="password" name="pass" id="pass"/><br/><br/>
	</form>	
		<!-- 
			StringHttpMessageConverter를 이용한 POST 방식의 요청 파라미터  
		-->		
		<input type="button" 
			value="@RequestBody 로그인" id="reqBodyData"/><br/>		
		<!-- 
			MappingJackson2HttpMessageConverter를 이용한 json 응답처리 
		-->
		<input type="button" value="JSON요청JSON응답 - 로그인" id="jsonData"/>
		<br/>		
		<!-- 게시 글 리스트 가져오기 -->
		<input type="button" value="게시 글 리스트 - json" id="boardList"><br/>	
	
	<div id="result"></div>
</article>
</html>
