package com.springstudy.bbs.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/* 컨트롤러에서 발생하는 공통 Exception 처리 클래스
 * @ControllerAdvice 애노테이션의 () 안에 Exception 처리 범위를 지정하였다.
 * 아래는 com.springstudy.bbs 패키지를 포함한 그 하위 패키지의 Controller에서
 * Exception이 발생하게 되면 그 Controller 클래스에 정의된 @ExceptionHandler 
 * 애노테이션이 적용된 메서드의 Exception 타입을 체크해 그 타입 및  하위 타입의
 * Exception이 지정된 메서드가 실행된다. 
 **/
@ControllerAdvice("com.springstudy.bbs")
public class ControllerAdviceExceptionHandler {
	
	/* Spring MVC 지원 Exception 처리
	 * 1. @ExceptionHandler 애노테이션을 이용한 Exception 처리
	 * 2. @ControllerAdvice 애노테이션을 이용한 공통 Exception 처리
	 * 3. @ResponseStatus 애노테이션을 이용한 Exception 처리
	 * 
	 * @ExceptionHandler 애노테이션은 컨트롤러에서 Exception을 처리하기
	 * 위한 메서드에 적용할 수 있고 @ControllerAdvice가 적용된 공통 Exception
	 * 처리 클래스의 메서드에도 적용할 수 있다. 컨트롤러와 @ControllerAdvice
	 * 애노테이션이 적용된 클래스에 같은 타입의 Exception이 설정되었다면 컨트롤러의
	 * Exception 처리 메서드가 우선 적용된다.
	 * 
	 * 컨트롤러에서 Exception이 발생하면 스프링프레임워크는 HandlerExceptionResolver를
	 * 통해 Exception을 처리한다. HandlerExceptionResolver을 상속한 클래스는
	 * 여러 종류가 있으며 Spring MVC 설정에서 <mvc:annotation-driven>에 의해
	 * ExceptionHandlerExceptionResolver가 등록된다. 이 클래스는
	 * @ExceptionHandler 애노테이션이 적용된 메서드를 이용해 Exception을 처리하는
	 * 기능을 제공한다. 아래는 HandlerExceptionResolver가 적용되는 순서이다.
	 * 
	 * 1. DispatcherServlet은 컨트롤러에서 Exception이 발생했을 때 제일 먼저 
	 *    ExceptionHandlerExceptionResolver를 이용해 Exception을 처리한다. 
	 *    ExceptionHandlerExceptionResolver는 @ExceptionHandler가
	 *    적용된 메서드를 통해 Exception을 처리하고 발생된 Exception에 해당하는 
	 *    @ExceptionHandler가 적용된 메서드가 없으면
	 *    ExceptionHandlerExceptionResolver는 Exception을 처리하지 않는다.
	 *    
	 * 2. ExceptionHandlerExceptionResolver에서 Exception 처리가 되지 못하면
	 *    DispatcherServlet은 DefaultHandlerExceptionResolver를 통해 
	 *    Exception을 처리한다.  
	 *    
	 * 3. DefaultHandlerExceptionResolver를 통해 Exception이 처리되지 못하면
	 *    DispatcherServlet은 ResponseStatusHandlerExceptionResolver를
	 *    통행 Exception을 처리한다. 이 클래스를 통해 Exception이 처리되지 못하면 
	 *    마지막으로 톰캣에 의해서 Exception이 처리된다. 이때 web.xml에 정의된 Exception
	 *    처리가 실행된다.
	 **/
	@ExceptionHandler(RuntimeException.class)
	public String runtimeExceptionHandler(Model model) {		
		model.addAttribute("title", "서버에서 처리중 에러 발생");
		return "errors/runtimeException";
	}
	
	// Not Found(404)
	@ExceptionHandler(NoSuchRequestHandlingMethodException.class)
	public String notFoundException(Model model) {
		model.addAttribute("title", "요청한 페이지를 찾을 수 없습니다.");
		return "errors/commonException";
	}
	
	// Bad Request(400)
	@ExceptionHandler(TypeMismatchException.class)
	public String bedRequestException(Model model) {
		model.addAttribute("title", "요청 파라미터가 잘못 되었습니다."); 
		return "errors/commonException";
	}
	
	// Bad Request(405)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public String methodNotSupported(Model model) {
		model.addAttribute("title", "지원하지 않는 요청 방식"); 
		return "errors/runtimeException";
	}
}
