package com.springstudy.bbs.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.springstudy.bbs.domain.Member;
import com.springstudy.bbs.exception.MemberNotFoundException;
import com.springstudy.bbs.exception.MemberPassCheckFailException;
import com.springstudy.bbs.service.MemberService;

// 스프링 MVC의 컨트롤러임을 선언하고 있다.
@Controller

/* 스프링은 데이터를 세션 영역에 저장할 수 있도록 @SessionAttributes("모델이름")
 * 애노테이션을 제공하고 있다. 클래스 레벨에 @SessionAttributes("모델이름")와
 * 같이 애노테이션과 모델 이름을 지정하고 아래 login(), joinInfo() 메서드와 같이 
 * Controller 메서드에서 @SessionAttributes에 지정한 모델이름과 동일한 
 * 이름으로 모델에 객체를 추가하면 이 객체를 세션 영역에 저장해 준다.
 * 
 * 회원 가입 처리를 위한 단계를 살펴보면 약관동의, 실명인증 , 회원 기본정보 입력,
 * 회원 추가정보 입력 등과 같이 여러 화면으로 구성되어 있다. 이렇게 여러 단계를
 * 거쳐서 회원이 입력한 정보를 임시로 저장해야 할 경우 일반적으로 세션 영역에 저장하게
 * 되는데 이 때 @SessionAttributes("모델이름") 애노테이션을 사용해 임시로
 * 사용할 데이터를 세션 영역에 저장할 수 있다.
 * 클래스 레벨에 @SessionAttributes("모델이름") 애노테이션과 모델 이름을
 * 지정하고 아래 joinInfo() 메서드와 같이 Controller 메서드에서 같은 이름을
 * 지정해 모델에 추가하면 이 객체를 세션 영역에 저장하게 된다. 그리고 임시로 사용할
 * 객체의 사용이 끝나면 아래 logout(), joinResult() 메서드와 같이 SessionStatus의
 * setComplete() 메서드를 이용해 데이터를 세션 영역에서 삭제하면 된다.
 * 
 * @SessionAttributes()를 사용해 member와 m 두 개의 모델 이름을 지정했다. 
 **/
@SessionAttributes({"member", "m"})
public class MemberController {
	
	private MemberService memberService;
	
	/* @Autowired annotation을 사용해 MemberService 구현체를 셋터 주입하고 있다. 
	 * 스프링이 기본 생성자를 통해 이 클래스의 인스턴스를 생성한 후 setter 주입
	 * 방식으로 MemberService 구현체를 주입해 준다. 셋터 주입은 반드시 기본 생성자가
	 * 존재해야 하지만 이 클래스에는 다른 생성자가 존재하지 않으므로 컴파일러에 의해
	 * 자동으로 기본 생성자가 만들어 진다.
	 **/
	@Autowired
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	
	/* @RequestMapping의 ()에 method=RequestMethod.Post를 지정해
	 * "/login"으로 들어오는 POST 방식의 요청을 처리할 수 있도록 설정하고 있다.
	 * 
	 * 요청을 처리한 결과를 뷰에 전달하기 위해 사용하는 것이 모델이다.
	 * 컨트롤러는 요청을 처리한 결과 데이터를 모델에 담아 뷰로 전달하고 뷰는
	 * 모델로 부터 데이터를 읽어와 클라이언트로 보낼 결과 페이지를 만들게 된다.
	 *   
	 * 스프링은 컨트롤러에서 모델에 데이터를 담을 수 있는 다양한 방법을 제공하는데
	 * 아래와 같이 파라미터에 Model을 지정하는 방식이 많이 사용된다. 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터에 Model
	 * 을 지정하면 스프링이 이 메서드를 호출하면서 Model 타입의 객체를 넘겨준다.
	 * 우리는 Model을 받아 이 객체에 결과 데이터를 담기만 하면 뷰로 전달된다.
	 * 	
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터에 
	 * @RequestParam 애노테이션에 파라미터 이름을 지정하면 
	 * 이 애노테이션이 앞에 붙은 매개변수에 파라미터 값을 바인딩 시켜준다.
	 * 
	 * @RequestParam 애노테이션에 사용할 수 있는 속성은 아래와 같다.
	 * value : HTTP 요청 파라미터의 이름을 지정한다.
	 * required : 요청 파라미터가 필수인지 설정하는 속성으로 기본값은 true 이다.
	 * 			이 값이 true인 상태에서 요청 파라미터의 값이 존재하지 않으면
	 * 			스프링은 Exception을 발생시킨다.
	 * defaultValue : 요청 파라미터가 없을 경우 사용할 기본 값을 문자열로 지정한다.
	 * 
	 * @RequestParam(value="id" required="false" defaultValue="")
	 * 
	 * @RequestParam 애노테이션은 요청 파라미터 값을 읽어와 메서드의 
	 * 파라미터 타입에 맞게 변환해 준다.
	 * 스프링은 요청 파라미터의 값으로 변환할 수 없는 경우 400 에러를 발생시킨다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드에 요청 파라미터
	 * 이름과 메서드의 파라미터 이름이 같은 경우 @RequestParam 애노테이션을
	 * 지정하지 않아도 스프링으로부터 요청 파라미터를 받을 수 있다. 
	 **/	 
	@RequestMapping(value="/login", method=RequestMethod.POST)	
	public String login(Model model, @RequestParam("id") String id, 
			@RequestParam("pass") String pass, 
			HttpSession session, HttpServletResponse response) 
					throws ServletException, IOException {
		
		int result = memberService.login(id, pass);
		
		if(result == -1) {			
			throw new MemberNotFoundException(id + "는 존재하지 않는 아이디 입니다.");			
			
		} else if(result == 0) {
			throw new MemberPassCheckFailException("비밀번호가 맞지 않습니다.");
		}		
		
		Member member = memberService.getMember(id);
		session.setAttribute("isLogin", true);		
		
		/* 클래스 레벨에 @SessionAttributes("member") 애노테이션을
		 * 지정하고 컨트롤러의 메서드에서 아래와 같이 동일한 이름으로 모델에
		 * 추가하면 스프링이 세션 영역에 데이터를 저장해 준다.
		 **/ 
		model.addAttribute("member", member);
		System.out.println("member.name : " + member.getName());

		/* 클라이언트 요청을 처리한 후 리다이렉트 해야할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를 사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * 로그인이 성공하면 게시 글 리스트로 리다이렉트 된다.
		 **/		
		return "redirect:/boardList";
	}
	
	/* @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입
	 * 
	 * 1. 메서드의 파라미터 타입으로 지정할 수 있는 객체와 애노테이션 
	 * @RequestMapping 애노테이션이 적용된 컨트롤러 메서드의 파라미터에
	 * 아래와 같은 객체와 애노테이션을 사용할 수 있도록 지원하고 있다.
	 * 
	 * - HttpServletRequest, HttpServletResponse
	 *   요청/응답을 처리하기 위한 서블릿 API
	 *   
	 * - HttpSession 
	 *   HTTP 세션을 위한 서블릿 API  
	 * 
	 * - org.springframework.ui.Model, ModelMap, java.util.Map
	 *   뷰에 모델 데이터를 전달하기 위한 모델 객체
	 *   
	 * - 커맨드 객체(VO, DTO)
	 *   요청 데이터를 저장할 객체
	 * 
	 * - Errors, BindingResult    
	 *   검증 결과를 저장할 객체로 커맨드 객체 바로 뒤에 위치 시켜야 한다.
	 *   
	 * - @RequestParam
	 *   HTTP 요청 파라미터의 값을 메서드의 파라미터로 매핑하기 위한 애노테이션  
	 * 
	 * - @RequestHeader
	 *   HTTP 요청 해더의 값을 파라미터로 받기 위한 애노테이션
	 *   
	 * - @RequestCookie
	 *   Cookie 데이터를 파라미터로 받기 위한 애노테이션
	 *   
	 * - @RequestVariable
	 *   RESTful API 방식의 파라미터를 받기 위한 경로 변수 설정 애노테이션
	 *   
	 * - @RequestBody
	 *   요청 몸체의 데이터를 자바 객체로 변환하기 위한 애노테이션
	 *   String이나 JSON으로 넘어오는 요청 몸체의 데이터를 자바 객체로
	 *   변환하기 위한 사용하는 애노테이션 이다.
	 *   
	 * - Writer, OutputStream
	 *   응답 데이터를 직접 작성할 때 메서드의 파라미터로 지정해 사용한다.
	 *   
	 * 2. 메서드의 반환 타입으로 지정할 수 있는 객체와 애노테이션
	 * - String
	 *   뷰 이름을 반환할 때 메서드의 반환 타입으로 지정
	 * 
	 * - void
	 *   컨트롤러의 메서드에서 직접 응답 데이터를 작성할 경우 지정
	 * 
	 * - ModelAndView
	 *   모델과 뷰 정보를 함께 반환해야 할 경우 지정
	 *   이전의 컨트롤는 스프링이 지원한는 Controller 인터페이스를
	 *   구현해야 했는데 이때 많이 사용하던 반환 타입이다.
	 * 
	 * - 자바 객체 
	 *   메서드에 @ResponseBody가 적용된 경우나 메서드에서 반환되는
	 *   객체를 JSON 또는 XML과 같은 양식으로 응답을 변환 할 경우에 사용한다. 
	 **/
	@RequestMapping("/logout")
	public String logout(SessionStatus sessionStatus, HttpSession session) {	
		
		/* 세션 영역에서 객체를 삭제
		 * 세션 영역에서만 삭제되고 모델에는 삭제되지 않는다.
		 * 세션을 다시 시작하지 않기 때문에 세션이 계속해서 유지된다.
		 **/
		//sessionStatus.setComplete();
		
		// 현재 세션을 종료하고 새로운 세션을 시작한다.
		session.invalidate();
		
		/* 클라이언트 요청을 처리한 후 리다이렉트 해야할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를  사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * 로그아웃 되면 게시 글 리스트로 리다이렉트 된다./
		 **/
		return "redirect:/boardList";
	}
	
	/* 회원가입 폼, 수정 폼 에서 들어오는 요청을 처리하는 메서드
	 * 아래는 "/joinInfo"로 들어오는 GET 방식 요청을 처리하는 메서드를 지정한
	 * 것이다. method 속성을 생략하면 기본 값은 RequestMethod.GET 이다.
	 * 
	 * 실제 memberJoinForm.jsp에서 폼을 전송할 때 POST 방식으로 폼을 전송
	 * 했지만 아래의 jointInfo() 메서드에서 정상적으로 처리된다.
	 * method 속성이 생략되면 스프링은 GET 방식 요청을 먼저 적용해 처리 하지만
	 * POST 방식의 요청도 처리해 준다.
	 * 
	 * 만약 아래에서 method=RequestMethod.GET 를 명시적으로 지정했거나
	 * /joinInfo 요청을 처리하는 메서드가 존재하지 않는다면 아래와 같이 405
	 * 익셉션이 발생하게 된다.
	 * 
	 * HTTP Status 405 - Request method 'POST' not supported
	 * 
	 * 스프링이 자동으로 처리해 주긴 하지만 GET 방식과 POST 방식을 명확하게
	 * 처리하는 것이 바람직한 코딩 방법이다.
	 * 
	 * 스프링은 폼으로부터 전달된 파라미터를 객체로 처리 할 수 있는 아래와 같은
	 * 방법을 제공하고 있다. 아래와 같이 요청 파라미터를 전달받을 때 사용하는 
	 * 객체를 커맨드 객체라고 부르며 이 커맨트 객체는 자바빈 규약에 따라 프로퍼티에
	 * 대한 setter를 제공하도록 작성해야 한다. 그리고 파라미터 이름이 커맨드 객체의
	 * 프로퍼티와 동일하도록 폼 컨트롤의 name 속성을 지정해야 한다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 Controller 메서드에 커맨드 객체를
	 * 파라미터로 지정하면 커맨드 객체의 프로퍼티와 동일한 이름을 가진 요청 
	 * 파라미터의 데이터를 스프링이 자동으로 설정해 준다. 이때 스프링은 자바빈
	 * 규약에 따라 적절한 setter 메서드를 사용해 값을 설정한다.
	 * 
	 * 커맨드 객체의 프로퍼티와 일치하는 파라미터 이름이 없다면 기본 값으로 설정된다.
	 * 또한 프로퍼티의 데이터 형에 맞게 적절히 형 변환 해 준다. 형 변환을 할 수 없는
	 * 경우 스프링은 400 에러를 발생 시킨다. 예를 들면 프로퍼티가 정수 형 일때 매칭
	 * 되는 값이 정수형으로 형 변환 할 수 없는 경우 400 에러를 발생 시킨다.
	 *
	 * 커맨드 객체는 스프링에 의해 클래스 이름에서 첫 자를 소문자한 카멜 케이싱된
	 * 이름으로 뷰에 전달할 model에 자동 추가된다.
	 * 예를 들면 클래스 이름이 MemberServiceRequest라면 model에 저장되는
	 * 이름은 클래스 이름의 첫 자를 소문자한 memberServiceRequest가 된다.
	 * 위와 같이 클래스 이름이 길어 model에 추가되는 커맨드 객체의 이름을 변경해야
	 * 할 경우 아래와 같이 커맨드 객체 앞에 @ModelAttribute() 애노테이션을
	 * 사용해 model에서 사용할 이름을 지정하면 된다.
	 * 
	 * @ModelAttribute("m") MemberServiceRequest member
	 *  
	 * 아래의 Member 객체에 @ModelAttribute("m")를 적용시키면 에러가 발생한다.
	 * 그 이유는 클래스 레벨에 @SessionAttributes({"member", "m"})를 지정하고
	 * Controller 메서드의 파라미터에 @ModelAttribute("m")를 이용해 동일한 이름을
	 * 지정하게 되면 세션 영역에서 m이라는 이름을 가진 데이터를 찾는데 /joinInfo로
	 * 요청이 들어올 때 세션에는 m이라는 이름을 가진 속성이 없기 때문에 에러가 발생한다.
	 **/	
	@RequestMapping("/joinInfo")
	public String joinInfo(Model model, Member member,
			String pass1, String emailId, String emailDomain,
			String mobile1, String mobile2, String mobile3,
			String phone1, String phone2, String phone3,
			@RequestParam(value="emailGet", required=false, 
				defaultValue="false")boolean emailGet) {
		System.out.println("/joinInfo");
		
		member.setPass(pass1);
		member.setEmail(emailId + "@" + emailDomain);
		member.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
				
		if(phone2.equals("") || phone3.equals("")) {			
			member.setPhone("");				
		} else {			
			member.setPhone(phone1 + "-" + phone2 + "-" + phone3);
		}				
		member.setEmailGet(Boolean.valueOf(emailGet));
		
		/* 클래스 레벨에 @SessionAttributes({"member", "m"}) 
		 * 애노테이션을 지정하고 컨트롤러의 메서드에서 아래와 같이 동일한 
		 * 이름으로 모델에 추가하면 스프링이 세션 영역에 데이터를 저장해 준다.
		 **/ 
		model.addAttribute("m", member);	
		return "member/memberInfo";
	}
	
	/* 회원이 입력한 정보를 확인한 후 가입완료를 클릭하여 들어오는 요청을 처리하는 메서드
	 * 
	 * 클래스 레벨에 @SessionAttributes({"member", "m"})를 지정하고
	 * Controller 메서드에서 member, m이라는 이름으로 모델에 저장하면
	 * 이 데이터는 세션 영역의 속성에 저장된다. Controller 메서드의 파라미터에
	 * 아래와 같이 Controller 메서드의 파라미터에 @ModelAttribute("m")를
	 * 지정하면 스프링은 세션 영역에서 m이라는 이름을 가진 데이터를 찾아 member
	 * 변수에 바인딩 시켜 준다. 만약 세션 영역의 속성에 m이라는 이름을 가진 
	 * 데이터가 존재하지 않으면 Exception이 발생한다.
	 **/
	@RequestMapping("/joinResult")
	public String joinResult(SessionStatus sessionStatus,
			@ModelAttribute("m") Member member) {
		
		System.out.println("joinResult : " + member.getName());
		memberService.addMember(member);
		
		/* 세션 영역에서 객체를 삭제
		 * 세션 영역에서만 삭제되고 모델에는 삭제되지 않는다.
		 * 세션을 다시 시작하지 않기 때문에 세션이 계속해서 유지된다.
		 **/
		sessionStatus.setComplete();
		
		// 현재 세션을 종료하고 새로운 세션을 시작한다.
		//session.invalidate();
		
		return "redirect:boardList";
	}
	
	// 회원가입 폼에서 들어오는 중복 아이디 체크 요청을 처리하는 메서드
	@RequestMapping("/overlapIdCheck")	
	public String overlapIdCheck(Model model, String id) {		
		
		// 회원 아이디 중복 여부를 받아온다.
		boolean overlap = memberService.overlapIdCheck(id);
		
		// model에 id와 회원 아이디 중복 여부를 저장 한다. 
		model.addAttribute("id", id);
		model.addAttribute("overlap", overlap);
		
		/* 회원 가입 폼에서 아이디 중복확인 버튼을 클릭하면 새창으로 뷰가 보이게
		 * 해야 하므로 뷰 이름을 반환 할 때 "forward:" 접두어를 사용했다. 
		 * "forwrad:" 접두어가 있으면 뷰 리졸버 설정에 지정한 prefix, suffix를
		 * 적용하지 않고 "forwrad:" 뒤에 붙인 뷰 페이지로 포워딩 된다. 
		 **/
		return "forward:WEB-INF/views/member/overlapIdCheck.jsp";
	}	

	// 회원 정보 수정 폼 요청을 처리하는 메서드
	@RequestMapping("/memberUpdateForm")
	public String updateForm(Model model, String id) {
		
		/* 일반적으로 회원 정보 수정은 로그인이 완료된 후에 이루어 진다.
		 * 회원 로그인 시에 이미 세션 영역에 Member 객체를 저장하도록
		 * 구현되어 있으므로 별도로 모델을 만들 필요는 없다. 
		 **/
		Member member = memberService.getMember(id);
		model.addAttribute("member", member);
		
		return "member/memberUpdateForm";
	}	
	
	// 회원 수정 폼에서 들어오는 요청을 처리하는 메서드
	@RequestMapping("/memberUpdateInfo")
	public String memberUpdateInfo(Model model, Member member,
			String pass1, String emailId, String emailDomain,
			String mobile1, String mobile2, String mobile3,
			String phone1, String phone2, String phone3,
			@RequestParam(value="emailGet", required=false, 
				defaultValue="false")boolean emailGet) {
		System.out.println("/memberUpdateInfo ");
		
		member.setPass(pass1);
		member.setEmail(emailId + "@" + emailDomain);
		member.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
				
		if(phone2.equals("") || phone3.equals("")) {			
			member.setPhone("");				
		} else {			
			member.setPhone(phone1 + "-" + phone2 + "-" + phone3);
		}				
		member.setEmailGet(Boolean.valueOf(emailGet));
		
		/* 클래스 레벨에 @SessionAttributes({"member", "m"}) 
		 * 애노테이션을 지정하고 컨트롤러의 메서드에서 아래와 같이 동일한 
		 * 이름으로 모델에 추가하면 스프링이 세션 영역에 데이터를 저장해 준다.
		 **/ 
		model.addAttribute("m", member);	
		return "member/memberUpdateInfo";
	}
	
	/* 회원이 수정한 정보를 확인한 후 수정완료를 클릭하여 들어오는 요청을 처리하는 메서드
	 * 
	 * 클래스 레벨에 @SessionAttributes({"member", "m"})를 지정하고
	 * Controller 메서드에서 member, m이라는 이름으로 모델에 저장하면
	 * 이 데이터는 세션 영역의 속성에 저장된다. Controller 메서드의 파라미터에
	 * 아래와 같이 Controller 메서드의 파라미터에 @ModelAttribute("m")를
	 * 지정하면 스프링은 세션 영역에서 m이라는 이름을 가진 데이터를 찾아 member
	 * 변수에 바인딩 시켜 준다. 만약 세션 영역의 속성에 m이라는 이름을 가진 
	 * 데이터가 존재하지 않으면 Exception이 발생한다.
	 **/
	@RequestMapping("/memberUpdateResult")
	public String updateResult(Model model,
			HttpSession session,
			SessionStatus sessionStatus,
			@ModelAttribute("m") Member member) {
		
		System.out.println("updateResult : " + member.getId());
		memberService.updateMember(member);
		
		return "redirect:boardList";
	}
	
	/* 등록되지 않은 회원 아이디로 로그인을 시도할 때 발생하는 Exception 처리 메서드
	 * 이 Controller에서 MemberNotFoundException이 발생하면 이 메서드가 
	 * 실행된다. MemberNotFoundException은 회원이 로그인 할 때 등록되지
	 * 않은 회원 아이디면 MemberNotFoundException을 발생시킨다.
	 **/
	@ExceptionHandler(MemberNotFoundException.class)
	public String memberNotFound(Model model) {
		model.addAttribute("title", "존재하지 않는 아이디");		
		return "errors/controllerException";
	}
	
	/* 회원이 로그인 할 때 비밀번호가 맞지 않으면 발생하는 Exception 처리 메서드 
	 * 이 Controller에서 MemberPassCheckFailException이 발생하면 
	 * 이 메서드가 실행된다. MemberPassCheckFailException은 회원이
	 * 로그인 할 때 비밀번호가 맞지 않으면 MemberNotFoundException을
	 * 발생시킨다.
	 **/
	@ExceptionHandler(MemberPassCheckFailException.class)
	public String memberPassCheckFail(Model model) {
		model.addAttribute("title", "비밀번호가 맞지 않음");
		return "errors/controllerException";
	}	
	
	/* Exception 발생 테스트 메서드 - 요청 URI에 따라 강제 예외 발생 메서드
	 * http://localhost:8080/springstudy-bbs06/internal 과 같이 테스트 한다.
	 **/
	@RequestMapping({"/internal", "/badRequest", "/notFound", 
			"/runtime", "/methodNotSupported"})
	public String internalException(HttpServletRequest request) throws Exception {
		int index = request.getRequestURL().lastIndexOf("/");
		String command = request.getRequestURL().substring(index);
		
		if(command.equals("/internal")) {			
			int i = 100 / 0;
			
		} else if(command.equals("/badRequest")) {
			throw new TypeMismatchException("잘못된 요청", String.class);
			
		} else if(command.equals("/notFound")) {
			throw new NoSuchRequestHandlingMethodException(request);
		
		} else if(command.equals("/runtime")) {
			throw new RuntimeException("요청을 처리하는 과정에서 에러 발생");
		
		} else if(command.equals("/methodNotSupported")) {
			throw new HttpRequestMethodNotSupportedException("요청 방식이 잘못됨");
		
		}
		
		/* 반환되는 뷰 이름이 없어도 이 컨트롤러에서 익셉션이 발생하면 
		 * ControllerAdviceExceptionHandler 클래스의 @ExceptionHandler
		 * 애노테이션이 적용된 메서드가 호출되고 그 메서드에서 반환되는 뷰 페이지로
		 * 이동하게 된다.
		 **/		
		return "";
	}
}
