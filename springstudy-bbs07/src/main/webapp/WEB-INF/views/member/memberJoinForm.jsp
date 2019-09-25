<%-- 회원가입 폼 요청 처리 결과를 출력할 View 페이지 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 
	새로운 5자리 우편번호로 회원 주소를 입력 받기 위해 daum.net에서
	제공하는 우편번호 찾기 API를 사용하였다.
	참고 사이트 : http://postcode.map.daum.net/guide
-->    
<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
<article>
	<div id="memberJoinForm">
	<h3 id="joinFormTitle">회원 정보 입력</h3>	
	<form action="joinInfo" name="joinForm" method="post"
		id="joinForm">
		
		<%-- 회원 아이디 중복 검사를 했는지의 정보를 hidden 필드로 저장 --%>
		<input type="hidden" name="isIdCheck" id="isIdCheck" value="false"/>
		<div id="memberInputDefault">			
			<div class="memberInputText">
				<span class="memberSpan">* 이&nbsp;&nbsp;름 : </span>				
				<!-- 
					세션 영역에 저장된 m 라는 속성이 존재하면 텍스트 필드에
					그 정보가 출력되고 존재하지 않으면 아무것도 출력되지 않는다.
					회원이 입력한 자신의 정보를 확인하고 수정할 내용이 있으면 수정하기
					버튼을 클릭해 다시 이 회원 가입 폼으로 이동 하도록 구현하였다.
					회원이 입력한 정보를 확인하는 요청을 처리하는 Controller의 joinInfo()
					메서드에서 회원 가입 폼으로부터 넘어온 파라미터를 받아 세션에
					저장했기 때문에 아래와 같이 EL을 이용해 세션 영역의 속성에
					접근해 데이터가 존재하면 폼 컨트롤의 값으로 출력되도록 했다.
				-->
				<input type="text" name="name" size="15" style="ime-mode: active"
					id="name" value="${ sessionScope.m.name }" />
			</div>
			<!-- 
				아이디와 비밀번호를 영문 대소문자와 숫자만 입력되도록 style의 
				ime-mode: disabled를 적용하여 설정
				아래에서 벤더프리픽스를 적용하였지만  
				ime-mode는 IE에는 적용이 잘 되고 chrome 에서는 않됨
				그래서 input 태그에 pattern 속성도 적용해 보았으나 제대로 
				동작하지 않아서 jQuery를 이용해 keyup 이벤트에서 정규표현식을
				사용해 영문 대소문자, 숫자가 아닌 문자는 입력되지 않도록 했다. 
			 -->					
			<div class="memberInputText">
				<span class="memberSpan">* 아이디 : </span>
				<input type="text" name="id" size="15"  id="userId"
					style="-webkit-ime-mode: disabled; -moz-ime-mode: disabled; 
						-ms-ime-mode: disabled; ime-mode: disabled;"
					value="${ sessionScope.m.id }"/>
				<input type="button" value="중복확인" id="btnOverlapId"/>
			</div>
			<!-- input type=password 속성은 기본적으로 ime-mode가 영문 모드이다. -->							
			<div class="memberInputText">
				<span class="memberSpan">* 비밀번호 : </span>
				<input type="password" name="pass1" id="pass1" 
					pattern="[A-Za-z0-9]*" class="imeMode" size="15"/>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 비밀번호 확인 : </span>
				<input type="password" name="pass2" id="pass2" size="15"/>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 우편번호 : </span>
				<input type="text" name="zipcode" size="6" 
					maxlength="5" readonly id="zipcode"
					value="${ sessionScope.m.zipcode }"/>&nbsp;
				<input type="button" value="우편번호 검색" id="btnZipcode"/>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 자택주소 : </span>
				<input type="text" name="address1" size="60" readonly id="address1" 
					value="${ sessionScope.m.address1 }"/>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">상세주소 : </span>
				<input type="text" name="address2" size="60" id="address2" 
					value="${ sessionScope.m.address2 }"/>
			</div>
			<!-- 
				메일 아이디와 도메인은 한글이 없으므로 member.css에서 
				ime-mode: disabled를 적용하여 영문만 입력하도록 설정
				아래에서 벤더프리픽스를 적용하였지만  
				ime-mode는 IE에는 적용이 잘 되고 chrome 에서는 않됨
				그래서 jQuery를 이용해 keyup 이벤트에서 정규표현식을 사용해
				영문 대소문자, 숫자가 아닌 문자는 입력되지 않도록 했다. 
				그래서 input 태그에 pattern 속성도 적용해 보았으나 제대로 
				동작하지 않아서 jQuery를 이용해 keyup 이벤트에서 정규표현식을
				사용해 영문 대소문자, 숫자가 아닌 문자는 입력되지 않도록 했다. 
			 -->
			<div class="memberInputText">
				<span class="memberSpan">* 이 메 일 : </span>
				<input type="text" name="emailId" size="10" id="emailId"
					class="imeMode" value="${ sessionScope.m.email.split('@')[0] }"/>
				@ <input type="text" name="emailDomain" size="10" id="emailDomain" 					
					class="imeMode" value="${ sessionScope.m.email.split('@')[1] }"/>
				<select name="selectDomain" id="selectDomain">
					<option>직접입력</option>
					<option>네이버</option>
					<option>다음</option>
					<option>한메일</option>
					<option>구글</option>										
				</select>
			</div>
			<div class="memberInputText">
				<span class="memberSpan">* 휴 대 폰 : </span>
				<select name="mobile1" id="mobile1">
					<option ${ sessionScope.m.mobile.split('-')[0] == 010 ? "selected" : "" }>
						010</option>
					<option ${ sessionScope.m.mobile.split('-')[0] == 011 ? "selected" : "" }>
						011</option>
					<option ${ sessionScope.m.mobile.split('-')[0] == 016 ? "selected" : "" }>
						016</option>
					<option ${ sessionScope.m.mobile.split('-')[0] == 017 ? "selected" : "" }>
						017</option>
					<option ${ sessionScope.m.mobile.split('-')[0] == 018 ? "selected" : "" }>
						018</option>
					<option ${ sessionScope.m.mobile.split('-')[0] == 019 ? "selected" : "" }>
						019</option>
				</select>-					
				<input type="text" name="mobile2" size="3" maxlength="4" id="mobile2"
					value="${ sessionScope.m.mobile.split('-')[1] }"/>-
				<input type="text" name="mobile3" size="3" maxlength="4" id="mobile3"
					value="${ sessionScope.m.mobile.split('-')[2] }"/>
			</div>
		</div>
		<div id="memberInputOption">
			<div class="memberInputText">
				<span class="memberSpan">메일 수신여부 : </span>
				<input type="radio" name="emailGet" value="true"
					${ sessionScope.m.emailGet ? "checked" : "" }/>수신함
				<input type="radio" name="emailGet" value="false"
					${ sessionScope.m.emailGet ? "" : "checked" }/>수신않함
			</div>
			<div class="memberInputText">
				<span class="memberSpan"> 자택전화 : </span>
				<select name="phone1">
					<option ${ sessionScope.m.phone.split('-')[0] == 02 ? "selected" : "" }>
						02</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 031 ? "selected" : "" }>
						031</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 032 ? "selected" : "" }>
						032</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 033 ? "selected" : "" }>
						033</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 041 ? "selected" : "" }>
						041</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 042 ? "selected" : "" }>
						042</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 043 ? "selected" : "" }>
						043</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 044 ? "selected" : "" }>
						044</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 051 ? "selected" : "" }>
						051</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 052 ? "selected" : "" }>
						052</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 053 ? "selected" : "" }>
						053</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 054 ? "selected" : "" }>
						054</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 055 ? "selected" : "" }>
						055</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 061 ? "selected" : "" }>
						061</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 062 ? "selected" : "" }>
						062</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 063 ? "selected" : "" }>
						063</option>
					<option ${ sessionScope.m.phone.split('-')[0] == 064 ? "selected" : "" }>
						064</option>
				</select>-					
				<input type="text" name="phone2" size="3" maxlength="4"
					value="${ sessionScope.m.phone.split('-')[1] }"/>-
				<input type="text" name="phone3" size="3" maxlength="4"
					value="${ sessionScope.m.phone.split('-')[2] }"/>
			</div>
		</div>
		<div class="formButton">
			<input type="reset" value="다시쓰기"/>
			<input type="submit" value="가입하기" />
		</div>			
	</form>	
	</div>
</article>