package com.springstudy.bbs.ajax;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springstudy.bbs.service.MemberService;

// Ajax 요청을 처리하는 컨트롤러
@Controller
public class AjaxProcessController {
	
	@Autowired
	private MemberService memberService;
	
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
	 *
	 *
	 * MappingJackson2HttpMessageConverter를 이용한 json 응답처리
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
	 * @RequestMapping 애노테이션이 적용된 Controller의 메서드에
	 * 아래와 같이 @ResponseBody를 적용하고 Map 객체를 반환하면
	 * MappingJackson2HttpMessageConverter에 의해서 JSON 
	 * 형식으로 변환된다.
	 **/
	@RequestMapping("/passCheck.ajax")
	@ResponseBody
	public Map<String, Boolean> memberPassCheck(String id, String pass) {
		
		boolean result = memberService.memberPassCheck(id, pass);
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("result", result);
		
		/* MappingJackson2HttpMessageConverter에 의해서
		 * Map 객체가 아래와 같이 json 형식으로 변환된다.
		 * 
		 * {
		 * 		"result": 0 또는 "result": 2
		 * }
		 **/
		return map;
	}
}
