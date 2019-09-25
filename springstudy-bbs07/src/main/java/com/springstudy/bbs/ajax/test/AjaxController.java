package com.springstudy.bbs.ajax.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springstudy.bbs.domain.Board;
import com.springstudy.bbs.service.BoardService;


/* HttpMessageConvert를 이용한 Ajax  
*
* HttpMessageConvert 인터페이스는 http 요청으로 들어오는
* 데이터(text, xml, json)를 자바 객체로 변화하거나 응답 본문의
* 데이터(자바 객체)를 text, xml, json으로 변환 해 주는 인터페이스 이다.
* 
* 아래에서 설명하는 @RequestBody 애노테이션이 메서드에 적용되어 있으면
* 스프링은 요청 본문의 데이터(파라미터)를  @RequestBody 애노테이션이
* 적용되어 있는 메서드의 파라미터로 변환하여 준다.
* 
* 요청 파라미터는 문자열 형태 이므로 HttpMessageConvert의 구현체인 
* StringHttpMessageConvert 클래스가 자바 String 객체로 변환해 준다.
* 또한 @ResponseBody 애노테이션이 적용되어 있는 메서드의 반환 타입이 
* String 이라면 스프링은 StringHttpMessageConvert 클래스를 이용해
* 메서드가 반환하는 String 객체를 응답 데이터의 본문으로 변환하여 준다.
* 
* 주요한 HttpMessageConvert 구현 클래스  
* - StringHttpMessageConverter
*   요청 본문의 문자열 데이터를 자바 객체로 변환해 주거나 자바 객체를
*   응답 본문의 문자열 데이터로 변환해 준다.
* 
* - Jaxb2RootElementHttpMessageConverter
*   요청 본문의 xml 형식의 데이터를 자바 객체로 변환해 주거나 자바 객체를
*   응답 본문의 xml 형식의 데이터로 변환해 준다. 
*   JAXB2 API는 자바 6부터 기본적으로 포함되어 있으므로 별도의 의존
*   설정이 필요 없다. Jaxb2RootElementHttpMessageConverter는
*   아래와 같이 데이터 변환 처리를 해 준다.
*   
*   xml 형식 -> @XmlRootElement 객체 또는 @XmlType 객체로 변환
*   @XmlRootElement가 적용된 객체 -> xml 형식의 문서로 변환
*      
*   지원하는 요청 컨텐츠 타입은 아래와 같다.
*   text/xml, application/xml, application/*+xml
* 
* - MappingJackson2HttpMessageConverter
*   요청 본문의 json 형식의 데이터를 자바 객체로 변환해 주거나 자바 객체를
*   응답 본문의 json 형식의 데이터로 변환해 준다. 
*   지원하는 요청 컨텐츠 타입은 아래와 같다.
*   application/json, application/*+json
*   
* - AllEncompassingFormHttpMessageConverter
*   폼 전송 형식의 요청 본문의 데이터를 MultiValueMap으로 변환해 주거나
*   MultiValueMap을 응답 본문의 데이터로 변환해 준다.    
*   지원하는 요청 컨텐츠 타입은 아래와 같다.
*   application/x-www-form-urlencoed
*   multipart/form-data
*   
*	http 요청과 응답 본문의 데이터와 자바 객체 사이의 변환을 처리하기
*	위해서는 HttpMessageConvert 타입의 클래스를 스프링 빈으로
*	등록해야 하지만 <mvc:annotation-driven />을 적용시키면
*	StringHttpMessageConverter를 포함해 다수의
*	HttpMessageConvert 구현체를 빈으로 등록해 준다.  
**/
@Controller
@RequestMapping("/ajax")
public class AjaxController {	
	
	@Autowired
	BoardService boardService;
	
	@RequestMapping(value={"/", "/index"})	
	public String index() {		
		return "ajax/ajaxIndex";
	}

	/* 스프링 mvc에서 xml이나 json 형식의 응답 데이터를 만들려면
	 * xml 또는 json 형식의 응답을 생성하는 뷰 클래스를 사용하거나
	 * HttpServletResponse 객체를 직접 사용해 필요한 응답 데이터를
	 * 생성할 수있다. 
	 * 스프링 mvc가 지원 하는 아래와 같은 애노테이션을 사용하면 text, xml,
	 * json 형식의 요청 데이터를 자바 객체로 변환해 주거나 자바 객체를 text,
	 * xml, json 형식으로 변환해 응답 본문에 추가해 준다.
	 * 
	 * @RequestBody 
	 * 클라이언트가 요청할 때 요청 본문으로 넘어오는 데이터를 자바 객체로 변환할
	 * 때 사용한다. 예를 들면 post 방식 요청에서 본문에 실어 넘어오는 파라미터를
	 * 자바의 String으로 변환하거나 json 형식의 데이터를 자바 객체로 변환하기
	 * 위해 사용한다. 
	 * 
	 * @ResponseBody
	 * 자바 객체를 json 형식이나 xml 형식의 문자열로 변환하여 응답 본문에 실어
	 * 보내기 위해 사용한다. 
	 * 컨트롤러 메서드에 @ResponseBody가 적용되면 메서드의 반환 값은 스프링
	 * mvc에 의해서 Http 응답 본문에 포함 된다.
	 **/
	
	/* StringHttpMessageConverter를 이용한 POST 방식의 요청 파라미터
	 * 
	 * 아래는 스프링 빈 설정 파일에 HttpMessageConvert 인터페이스를
	 * 구현한 StringHttpMessageConverter가 빈으로 등록되어 있어야
	 * 요청 본문으로 들어오는 파라미터를 자바 객체로 변환하거나 자바 객체를 
	 * 문자열 데이터로 변환해 응답 본문에 포함 시킬 수 있다.
	 * 하지만 스프링 빈 설정 파일에 <mvc:annotation-driven />이 적용
	 * 되었기 때문에 StringHttpMessageConverter를 포함한 다수의
	 * HttpMessageConverter 구현체를 빈으로 등록해 준다. 
	 * 
	 * 아래 메서드는 post 방식 요청에서 요청 본문에 포함되어 넘어오는
	 * 요청 파라미터를 이 메서드의 param 이라는 파라미터로 받아서  
	 * 응답 본문에 실어 클라이언트로 내려 보낸다.	 
	 * 
	 * 폼에서 아이디(name="id")에 admin, 비밀번호(name="pass")에
	 * 1234를 입력하고 전송을 하였다면 폼 전송방식이 post 이므로 요청 본문에
	 * 아래와 같은 요청 파라미터가 만들어져서 서버로 전송될 것이다.
	 * 
	 * id=admin&pass=1234
	 * 
	 * 요청 본문으로 받은 데이터를 스프링 mvc가 @RequestBody 애노테이션이
	 * 적용된 메서드의 파라미터(param)로 넘겨준다.
	 * 그리고  이 메서드에 @ResponseBody가 적용되어 있으므로 이 메서드가
	 * 반환하는 값은 스프링 mvc가 http 응답 본문에 포함 시켜준다. 
	 **/	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	@ResponseBody
	public String login(@RequestBody String param) {
		System.out.println("param : " + param);
		
		/* 클라이언트로 받은 문자열 데이터를 그대로 반환하고 있다.
		 * 메서드의 반환 타입에 @ResponseBody를 적용하고 문자열을 반환하면
		 * StringHttpMessageConverter에 의해서 문자열이 응답 본문으로
		 * 변환되어 클라이언트로 전송된다. 
		 **/		
		return param;
	}
		
	/* MappingJackson2HttpMessageConverter를 이용한 json 응답처리
	 *	 
	 * 요청 본문의 json 형식의 데이터를 자바 객체로 변환해	 주거나 자바 객체를
	 * 응답 본문의 json 형식의 데이터로 변환해 준다. 
	 * 지원하는 요청 컨텐츠 타입은 아래와 같다.
	 * application/json, application/*+json
	 * 
	 * 참고사이트 : https://github.com/FasterXML
	 * http://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/
	 * 
	 * 스프링이 제공하는 MappingJackson2HttpMessageConverter는 
	 * Jackson2 라이브러리를 이용해서 자바 객체와 json 데이터를 변환하기
	 * 때문에 pom.xml에서 "jackson-databind"으로 검색해 아래와 같이
	 * Jackson2 라이브러리 의존 설정을 해야 한다. 
	 * 그렇지 않으면 json 타입의 요청을 자바 객체로 변환할 수 없기 때문에
	 * 415 Unsupported Media Type 응답을 받게 된다.
	 * 
	 * <dependency>
	 * 		<groupId>com.fasterxml.jackson.core</groupId>
	 * 			<artifactId>jackson-databind</artifactId>
	 * 			<version>2.8.5</version>
	 * 	</dependency>
	 *
	 * 스프링 빈 설정 파일에 HttpMessageConvert 인터페이스를
	 * 구현한 MappingJackson2HttpMessageConverter가 빈으로
	 * 등록되어 있어야 응답 본문으로 들어오는 json 형식의 데이터를 자바 객체로
	 * 변환하거나 자바 객체를 json 형식의 데이터로 변환해 응답 본문에 포함 시킬
	 * 수 있다. 하지만 스프링 빈 설정 파일에 <mvc:annotation-driven />이
	 * 적용되었기 때문에 MappingJackson2HttpMessageConverter를
	 * 포함한 다수의 HttpMessageConverter 구현체를 빈으로 등록해 준다.
	 * 
	 * 아래 메서드는 post 방식 요청에서 요청 본문에 포함되어 넘어오는
	 * json 형식의 문자열 데이터를 스프링 mvc가 
	 * com.springstudy.bbs.ajax.test.JsonFormData 타입의
	 * 자바 객체로 변환하여 이 메서드의 파라미터로 넘겨준다.	 
	 * 
	 * 폼에서 아이디(name="id")에 admin, 비밀번호(name="pass")에
	 * 1234를 입력하고 전송을 하였다면 폼 전송방식이 post 이므로 요청 본문에
	 * 아래와 같은 json 데이터가 만들어져 서버로 전송될 것이다.
	 * 
	 * {"id":"admin","pass":"1234"}
	 * 
	 * 요청 헤더에서 Content-Type을 확인해 보면
	 * Content-Type	application/json; charset=utf-8 이다.
	 * 
	 * 요청 본문으로 받은 json 형식의 데이터를 스프링 mvc가 @RequestBody
	 * 애노테이션이 적용된 메서드의 파라미터(loginData) 타입으로 변환해 넘겨준다.
	 * 그리고  이 메서드에 @ResponseBody가 적용되어 있으므로 이 메서드가
	 * 반환하는 데이터 타입인 Map<String, Object> 객체를
	 * 스프링 mvc가 json 형식으로 변환하여 http 응답 본문에 포함 시켜준다. 
	 **/	
	@RequestMapping("/loginJson.json")
	@ResponseBody
	public Map<String, Object> loginJson(@RequestBody JsonFormData loginData) {
		
		String id = loginData.getId();
		String pass = loginData.getPass();
		int success = 0;
		String message = null;
		String greeting = null;		
		System.out.println("id : " + loginData.getId() 
			+ "pass : " + loginData.getPass());		
		
		if(id.equals("admin") && pass.equals("1234")) {	
			success = 1;
			message = "로그인 성공";
			greeting = "안녕하세요 " + id + "님!";
			
		} else {
			message = "로그인 실패";
					
			if(! id.equals("admin")) {
				greeting ="아이디가 맞지 않습니다.";
			} else {
				greeting = "비밀번호가 맞지 않습니다.";
			}
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("greeting", greeting);
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		/* MappingJackson2HttpMessageConverter에 의해서
		 * JsonLoginMessage 객체가 아래와 같이 json 형식으로 변환된다.
		 * 
		 * {
		 * 		"success":1,
		 * 		"greeting":"안녕하세요 admin님!",
		 * 		"message":"로그인 성공"
		 * }
		 **/
		return resultMap;
	}
		
	// 게시 글 리스트를 json 데이터로 응답하는 메서드
	@RequestMapping("boardList.ajax")
	@ResponseBody
	public List<Board> boardList() {
				
		/* MappingJackson2HttpMessageConverter에 의해서
		 * Board 객체가 아래와 같이 json 형식으로 변환된다.
		 * 
		
		 **/
		/* Service 클래스를 이용해 1 페이지에 해당하는 게시 글 리스트를 가져온다.
		 * 이 List<Board> 객체를 바로 반환하면 객체 배열의 json 데이터가 반환된다.
		 * [{"no": 1, "title": "안녕하세요", ...}, {...}, {...}]		 
		 **/
		return boardService.boardList();
	}
}
