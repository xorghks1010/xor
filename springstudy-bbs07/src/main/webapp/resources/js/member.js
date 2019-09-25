$(function() {
	
	/* 회원 가입 폼, 회원정보 수정 폼에서 폼 컨트롤에서 키보드 입력을 체크해
	 * 유효한 값을 입력 받을 수 있도록 keypress와 keyup 이벤트 처리를 했다. 
	 **/
	$("#userId").on("keypress", inputCheck);
	$("#userId").on("keyup", inputCharReplace);
	$("#pass1").on("keypress", inputCheck);
	$("#pass1").on("keyup", inputCharReplace);
	$("#pass2").on("keypress", inputCheck);
	$("#pass2").on("keyup", inputCharReplace);
	$("#emailId").on("keypress", inputCheck);
	$("#emailId").on("keyup", inputCharReplace);
	$("#emailDomain").on("keypress", inputEmailDomainCheck);
	$("#emailDomain").on("keyup", inputEmailDomainReplace);
	
	/* 회원 가입 폼에서 아이디 중복확인 버튼이 클릭되면
	 * 아이디 중복을 확인할 수 있는 새 창을 띄워주는 함수
	 **/ 
	$("#btnOverlapId").on("click", function() {
		var id = $("#userId").val();
		url="overlapIdCheck?id=" + id;
		
		if(id.length == 0) {
			alert("아이디를 입력해주세요");
			return false;
		}
		
		if(id.length < 5) {
			alert("아이디는 5자 이상 입력해주세요.");
			return false;
		}
		
		window.open(url, "idCheck", "toolbar=no, location=no, " 
				+  "status=no, memubar=no, width=400, height=200");
	});

	/* 새 창으로 띄운 아이디 찾기 폼에서
	 * 아이디 중복확인 버튼이 클릭되면 유효성 검사를 하는 함수	 
	 **/
	$("#idCheckForm").on("submit", function() {
		var id = $("#checkId").val();	
		
		if(id.length == 0)  {
			alert("아이디를 입력해주세요");
			return false;
		}
		
		if(id.length < 5) {
			alert("아이디는 5자 이상 입력해주세요.");
			return false;
		}
	});
	
	/* 새 창으로 띄운 아이디 중복확인 창에서 "아이디 사용 버튼"이 클릭되면 
	 * 새 창을 닫고 입력된 아이디를 부모창의 회원가입 폼에 입력해 주는 함수
	 **/
	$("#btnIdCheckClose").on("click", function() {
		var id = $(this).attr("data-id-value");
		opener.document.joinForm.id.value = id;
		opener.document.joinForm.isIdCheck.value = true;
		window.close();
	});
	
	/* 회원 가입 폼과 회원정보 수정 폼에서 "우편번호 검색" 버튼의 클릭 이벤트 처리
	 * findZipcode() 함수는 다음 우편번호 API를 사용해 우편번호를 검색하는 함수로
	 * 두 페이지에서 사용되어 중복된 코드가 발생하므로 아래에 별도의 함수로 정의하였다.
	 */
	$("#btnZipcode").click(findZipcode);
	
	// 이메일 입력 셀렉트 박스에서 선택된 도메인을 설정하는 함수 
	$("#selectDomain").on("change", function() {
		var str = $(this).val();
		
		if(str == "직접입력") {	
			$("#emailDomain").val("");
			$("#emailDomain").prop("readonly", false);
		} else if(str == "네이버"){	
			$("#emailDomain").val("naver.com");			
			$("#emailDomain").prop("readonly", true);
			
		} else if(str == "다음") {		
			$("#emailDomain").val("daum.net");
			$("#emailDomain").prop("readonly", true);
			
		} else if(str == "한메일"){	
			$("#emailDomain").val("hanmail.net");
			$("#emailDomain").prop("readonly", true);
			
		} else if(str == "구글") {		
			$("#emailDomain").val("gmail.com");
			$("#emailDomain").prop("readonly", true);
		}
	});
	
	// 회원 가입 폼이 서브밋 될 때 이벤트 처리 - 폼 유효성 검사
	$("#joinForm").on("submit", function() {
		
		/* joinFormChcek() 함수에서 폼 유효성 검사를 통과하지 못하면
		 * false가 반환되기 때문에 그대로 반환하면 폼이 서브밋 되지 않는다.
		 **/ 
		return joinFormCheck();
	});
	
	/* 회원정보 수정 폼에서 "비밀번호 확인" 버튼이 클릭될 때 이벤트 처리
	 * 회원정보 수정 폼에서 기존 비밀번호가 맞는지를 Ajax 통신을 통해 확인한다.
	 **/
	$("#btnPassCheck").click(function() {
		var oldId = $("#userId").val();
		var oldPass = $("#oldPass").val();
		
		if($.trim(oldPass).length == 0) {
			alert("기존 비밀번호가 입력되지 않았습니다.\n기존 비밀번호를 입력해주세요");
			return false;
		}
		var data = "id=" + oldId + "&pass="+oldPass;
		$.post("passCheck.ajax", data, function(resultData, status, xhr) {			
			console.log(resultData.result + (Boolean(resultData.result) == true));
			if(resultData.result) {
				alert("비밀번호가 확인되었습니다.\n비밀번호를 수정해 주세요");
				$("#btnPassCheck").prop("disabled", true);
				$("#pass1").focus();
			}else {
				alert("비밀번호가 맞지 않습니다.");
			}
		});
	});	
	
	// 회원정보 수정 폼에서 기존 비밀번호가 맞는지를 Ajax 통신을 통해 확인하는 함수
	function passCheckAjax() {
		var oldId = $("#userId").val();
		var oldPass = $("#oldPass").val();
		
		if($.trim(oldPass).length == 0) {
			alert("기존 비밀번호가 입력되지 않았습니다.\n기존 비밀번호를 입력해주세요");
			return false;
		}
		var data = "id=" + oldId + "&pass="+oldPass;
		$.post("passCheck.ajax", data, function(resultData, status, xhr) {
			
			if(resultData.result) {
				alert("비밀번호가 확인되었습니다.\n비밀번호를 수정해 주세요");
				$("#btnPassCheck").prop("disabled", true);
				$("#pass1").focus();
			}else {
				alert("비밀번호가 맞지 않습니다.");
			}
		});
	}
		
	/* 회원 정보 수정 폼에서 비밀번호 확인 후에 기존 비밀번호가 다시 수정되면 비밀번호
	 * 확인 버튼을 다시 활성화 시켜서 submit 될 때 비밀번호 확인을 다시 하도록 한다.
	 **/
	$("#oldPass").change(function() {
		$("#btnPassCheck").prop("disabled", false);
	});
	
	// 회원정보 수정 폼에서 수정하기 버튼이 클릭되면 유효성 검사를 하는 함수
	$("#memberUpdateForm").on("submit", function() {
		
		/* 회원정보 수정 폼에서 "비밀번호 확인" 버튼이 disabled 상태가 아니면
		 * 기존 비밀번호를 확인하지 않았기 때문에 확인하라는 메시지를 띄운다.
		 **/
		if(! $("#btnPassCheck").prop("disabled")) {
			alert("기존 비밀번호를 확인해야 비밀번호를 수정할 수 있습니다.\n"
				+ "기존 비밀번호를 입력하고 비밀번호 확인 버튼을 클릭해 주세요");
			return false;
		}
		
		/* joinFormChcek() 함수에서 폼 유효성 검사를 통과하지 못하면
		 * false가 반환되기 때문에 그대로 반환하면 폼이 서브밋 되지 않는다.
		 **/ 
		return joinFormCheck();
	});
});

/* 회원 아이디, 비밀번호, 비밀번화 확인, 이메일 아이디, 이메일 도메인 폼 
 * 컨트롤에서 사용자가 입력한 값이 영문 대소문자, 숫자 인지를 체크하는 함수
 **/
function inputCheck() {
	
	/* 회원 정보 입력(수정) 폼의 아이디 입력란에 onkeypress="inputIdCheck()"로
	 * 이벤트 처리를 연결했다. 고전 이벤트 모델의 인라인 이벤트 모델로 이벤트를 처리할 경우
	 * 이벤트 핸들러 안에서 window 객체의 event 속성으로 접근할 수 있다.
	 * 여기서는 onkeypress 이벤트 속성에 inputIdCheck() 함수를 등록했기 때문에
	 * window 객체의 event 속성으로 KeyBoardEvent 객체를 구할 수 있고 
	 * 이 객체의 keyCode 속성으로 어떤 키가 눌렸는지 알아낼 수 있다.
	 **/	
	if(! (event.keyCode >= 48 && event.keyCode <= 57 ||
			event.keyCode >= 65 && event.keyCode <= 90 ||
			event.keyCode >= 97 && event.keyCode <= 122)) {
		
		alert(this);
		
		alert("아이디는 영문 대소문자와 숫자만 사용할 수 있습니다.");
		
		/* 키보드의 키가 눌려 발생한 이벤트로 입력된 문자가 반환되는 것을 막기 위해
		 * false를 지정하면 입력된 문자가 입력 상자에 입력되는 것을 방지한다.
		 * chrome, ie 10이하에서는 제대로 동작, ie 11에서 적용이 않됨
		 * 아래와 같이 return false를 적용하면 chrome, ie 7 ~ 11까지
		 * 영문 대소문자와 숫자만 입력될 수 있도록 할 수 있다. 
		 **/
		//event.returnValue = false;
		return false;
	}
}

/* 회원 아이디, 비밀번호, 비밀번화 확인, 이메일 아이디 폼 컨트롤에
 * 사용자가 입력한 값이 영문 대소문자, 숫자 만 입력되도록 수정하는 함수
 **/
function inputCharReplace() {
	var regExp = /[^A-Za-z0-9]/gi;	
	if(regExp.test($(this).val())) {
		alert("영문 대소문자, 숫자만 입력할 수 있습니다.");
		$(this).val($(this).val().replace(regExp, ""));
	}
}

/* 이 메일 도메인 입력 폼 컨트롤에 사용자가 입력한 값이 
 * 영문 대소문자, 숫자, 점(.)인지를 체크하는 함수 
 **/
function inputEmailDomainCheck() {
	if(! (event.keyCode >= 48 && event.keyCode <= 57 ||			
			event.keyCode >= 97 && event.keyCode <= 122 ||
			event.keyCode == 46)) {
		alert("이메일 도메인은 영문 소문자, 숫자, 점(.)만 입력할 수 있습니다.");
		
		/* 키보드의 키가 눌려 발생한 이벤트로 입력된 문자가 반환되는 것을 막기 위해
		 * false를 지정하면 입력된 문자가 입력 상자에 입력되는 것을 방지한다.
		 * chrome, ie 10이하에서는 제대로 동작, ie 11에서 적용이 않됨
		 * 아래와 같이 return false를 적용하면 chrome, ie 7 ~ 11까지
		 * 영문 대소문자와 숫자만 입력될 수 있도록 할 수 있다. 
		 **/
		//event.returnValue = false;
		return false;
	}
}

/* 이 메일 도메인 입력 폼 컨트롤에 사용자가 입력한 값이 
 * 영문 대소문자, 숫자, 점(.)만 입력되도록 수정하는 함수 
 **/ 
function inputEmailDomainReplace() {
	var regExp = /[^a-z0-9\.]/gi;	
	if(regExp.test($(this).val())) {
		alert("이메일 도메인은 영문 소문자, 숫자, 점(.)만 입력할 수 있습니다.");
		$(this).val($(this).val().replace(regExp, ""));
	}
}

/* 회원 가입 폼과 회원정보 수정 폼의 유효성 검사를 하는 함수
 * 두 페이지에서 처리하는 코드가 중복되어 하나의 함수로 정의하였다. 
 **/
function joinFormCheck() {
	var name = $("#name").val();
	var id = $("#userId").val();
	var pass1 = $("#pass1").val();
	var pass2 = $("#pass2").val();
	var zipcode = $("#zipcode").val();
	var address1 = $("#address1").val();
	var emailId = $("#emailId").val();
	var emailDomain = $("#emailDomain").val();
	var mobile2 = $("#mobile2").val();
	var mobile3 = $("#mobile2").val();
	var isIdCheck = $("#isIdCheck").val();
	
	if(name.length == 0) {		
		alert("이름이 입력되지 않았습니다.\n이름을 입력해주세요");
		return false;
	}	
	if(id.length == 0) {		
		alert("아이디가 입력되지 않았습니다.\n아이디를 입력해주세요");
		return false;
	}		
	if(isIdCheck == 'false') {		
		alert("아이디 중복 체크를 하지 않았습니다.\n아이디 중복 체크를 해주세요");
		return false;
	}	
	if(pass1.length == 0) {		
		alert("비밀번호가 입력되지 않았습니다.\n비밀번호를 입력해주세요");
		return false;
	}
	
	if(pass2.length == 0) {		
		alert("비밀번호 확인이 입력되지 않았습니다.\n비밀번호 확인을 입력해주세요");
		return false;
	}
	if(pass1 != pass2) {
		alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		return false;
	}	
	if(zipcode.length == 0) {		
		alert("우편번호가 입력되지 않았습니다.\n우편번호를 입력해주세요");
		return false;
	}	
	if(address1.length == 0) {		
		alert("주소가 입력되지 않았습니다.\n주소를 입력해주세요");
		return false;
	}	
	if(emailId.length == 0) {		
		alert("이메일 아이디가 입력되지 않았습니다.\n이메일 아이디를 입력해주세요");
		return false;
	}	
	if(emailDomain.length == 0) {		
		alert("이메일 도메인이 입력되지 않았습니다.\n이메일 도메인을 입력해주세요");
		return false;
	}	
	if(mobile2.length == 0 || mobile3.length == 0) {		
		alert("휴대폰 번호가 입력되지 않았습니다.\n휴대폰 번호를 입력해주세요");
		return false;
	}
}

/* 우편번호 찾기 - daum 우편번소 찾기 API 이용
 * 회원 가입 폼과 회원정보 수정 폼에서 "우편번호 검색" 버튼이 클리되면 호출되는 함수
 **/
function findZipcode() {	
	new daum.Postcode({
		oncomplete: function(data) {
			var fullAddr = ""; // 최종 주소 변수
			var extraAddr = ""; // 조합형 주소 변수
			
			// 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
			// 사용자가 도로명 주소를 선택했을 경우
			if(data.userSelectedType === "R") { 
				fullAddr = data.roadAddress;
				
			} else { // 사용자가 지번 주소를 선택했을 경우(J)
				// 모두 도로명 주소를 적용했다.
				fullAddr = data.roadAddress;
				//fullAddr = data.jibunAddress;
			}
			
			// 사용자가 선택한 주소가 도로명 타입일때 조합한다.
			if(data.userSelectedType === "R") {
				//법정동명이 있을 경우 추가한다.
				if(data.bname !== "") {
					extraAddr += data.bname;
				}
				
				// 건물명이 있을 경우 추가한다.
				if(data.buildingName != "") {
					extraAddr += (extraAddr !== "" ? ", " 
							+ data.buildingName : data.buildingName);
				}
				
				// 조합형 주소가 있으면 조합형 주소를 ()로 묶어서 최종 주소에 추가한다.
				fullAddr += (extraAddr !== "" ? "(" + extraAddr + ")" : "");
			}	
			
			// 우편번호와 주소 정보를 해당 입력상자에 출력한다.
			$("#zipcode").val(data.zonecode);
			$("#address1").val(fullAddr);
			
			// 커서를 상세주소 입력상자로 이동한다.
			$("#address2").focus();
		}
	}).open();
}
