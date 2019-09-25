package com.springstudy.bbs.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/* HandlerInterceptor는 요청 경로마다 접근 제어를 다르게 하거나 특정 URL에 접근할 때
 * 공통적으로 처리해야 할 것이 있을 때 주로 사용한다. 앞의 프로젝트에서는 회원이 로그인
 * 상태인지 컨트롤러 메서드마다 세션을 체크해 로그인 상태를 체크 했기 때문에 동일한
 * 코드의 중복을 피할 수 없었다. 하지만 스프링프레임워크가 제공하는 HandlerInterceptor를
 * 구현해 애플리케이션에 적용하면 중복 코드를 줄일 수 있다. 이처럼 여러 요청 경로 또는
 * 여러 컨트롤러에서 공통으로 적용해야 할 기능을 구현할 때 HandlerInterceptor를
 * 많이 사용한다.
 * 
 * HandlerInterceptor를 사용하면 다음과 같은 시점에 공통 기능을 적용할 수 있다.
 * 
 * 1. 컨트롤러 실행 전
 * 2. 컨트롤러 실행 후 - 아직 뷰를 생성하지 않았다.
 * 3. 뷰가 생성되고 클라이언트로 전송된 후
 * 
 * HandlerInterceptor 인터페이스에 정의된 추상 메서드는 모두 3개이며 전체를
 * 구현하지 않아도 될 때는 HandlerInterceptor를 구현하였지만 메서드의 내용이
 * 없는 HandlerInterceptorAdapter를 상속 받아 필요한 메서드만 구현 할 수 있다.
 * 
 * HandlerInterceptor를 구현하고 동작시키기 위해서는 SpringMVC 설정에
 * Bean으로 등록하고 <interceptors> 태그를 사용해 이 인터셉터가 동작할 
 * 경로 매핑을 해야 제대로 동작한다.
 **/
// 접속자가 로그인 상태인지 체크하는 인터셉터
public class LoginCheckInterceptor implements HandlerInterceptor {
	
	/* preHandle() 메서드는 클라이언트의 요청이 들어오고 컨트롤러가 실행되기
	 * 전에 공통으로 적용할 기능을 구현할 때 사용한다.
	 * 예를 들면 특정 요청에 대해 로그인이 되어 있지 않으면 컨트롤러를 실행하지
	 * 않거나 컨트롤러를 실행하기 전에 그 컨트롤러에서 필요한 정보를 생성해
	 * 넘겨 줄 필요가 있을 때 preHandler() 메서드를 이용해 구현하면 된다.
	 * 이 메서드가 false를 반환하면 다음으로 연결된 HandlerInterceptor
	 * 또는 컨트롤러 자체를 실행하지 않게 할 수 있다. 
	 **/
	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		// 세션에 isLogin란 이름의 데이터가 없으면 로그인 상태가 아님
		if(request.getSession().getAttribute("isLogin") == null) {
			response.sendRedirect("loginForm");
			return false;
		}
		return true;
	}

	/* postHandle() 메서드는 클라이언트 요청이 들어오고 컨트롤러가 정상적으로
	 * 실행된 이후에 공통적으로 적용할 추가 기능이 있을 때 주로 사용한다.
	 * 만약 컨트롤러 실행 중에 예외가 발생하게 되면 postHandle() 메서드는 
	 * 호출되지 않는다. 
	 **/
	@Override
	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}
	
	/* afterCompletion() 메서드는 클라이언트의 요청을 처리하고 뷰를 생성해
	 * 클라이언트로 전송한 후에 호출된다. 클라이언트 실행 중에 예외가 발생하게 되면
	 * 이 메서드 4번째 파라미터로 예외 정보를 받을 수 있다. 예외가 발생하지 않으면
	 * 4번째 파라미터는 null을 받게 된다.
	 **/
	@Override
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
