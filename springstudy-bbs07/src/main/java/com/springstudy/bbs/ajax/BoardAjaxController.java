package com.springstudy.bbs.ajax;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springstudy.bbs.domain.Reply;
import com.springstudy.bbs.service.BoardService;

@Controller
public class BoardAjaxController {
	
	@Autowired
	BoardService boardService;
	
	/* 추천/탱큐 요청을 처리하는 메서드 
	 *
	 * 스프링 mvc에서 xml이나 json 형식의 응답 데이터를 만들려면
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
	@RequestMapping("/recommend.ajax")
	@ResponseBody
	public Map<String, Integer> recommend(int no, String recommend) {
		
		/* 위에서도 언급했지만 @RequestMapping 애노테이션이 적용된 
		 * Controller 메서드에 @ResponseBody 애노테이션이 적용되면
		 * 반환 타입이 String 일 경우 HttpMessageConverter를 사용해
		 * String 객체를 직렬화 하고 반환 타입이 위와 같이 Map이거나 자바
		 * 객체인 경우 MappingJackson2HttpMessageConverter를
		 * 사용해 JSON 형식으로 변환한다.
		 * 
		 * Service 클래스에서 맵에 저장할 때 아래와 같이 저장하였다.
		 * 
		 * map.put("recommend", board.getRecommend());
		 * map.put("thank", board.getThank()); 
		 * 
		 * 이 데이터는 다음과 같이 JSON 형식으로 변환되어 응답된다.
		 * 
		 * { "recommend": 15, "thank": 26 }
		 **/		
		return boardService.recommend(no, recommend);		
	}
	
	// 댓글 쓰기 요청을 처리하는 메서드
	@RequestMapping("/replyWrite.ajax")
	@ResponseBody
	public List<Reply> addReply(Reply reply) {
		
		// 새로운 댓글을 등록한다.
		boardService.addReply(reply);
		
		/* 댓글 쓰기가 완료되면 새롭게 추가된 댓글을 포함해서 게시 글 
		 * 상세보기에 다시 출력해야 하므로 갱신된 댓글 리스트를 가져와 반환한다.
		 * 
		 * 아래는 게시 글 번호에 해당하는 댓글이 List<Reply>로 반환되기
		 * 때문에 스프링은 MappingJackson2HttpMessageConverter를
		 * 사용해 다음과 같이 객체 배열 형태의 JSON 형식으로 변환되어 응답된다.
		 * 
		 * [
		 *  {bbsNo: 100, no: 27, regDate: 1516541138000, 
		 * 		replyContent: "저도 동감이여..", replyWriter: "midas"},
		 *  {bbsNo: 100, no: 20, ... }, 
		 *    ...
		 *  {bbsNo: 100, no: 1, regDate: 1462682672000, 
		 *  	replyContent: "항상 감사합니다...", replyWriter: "midas"}
		 * ]
		 **/
		return boardService.replyList(reply.getBbsNo());
	}
	
	// 댓글 수정 요청을 처리하는 메서드	
	@RequestMapping("/replyUpdate.ajax")
	@ResponseBody
	public List<Reply> updateReply(Reply reply) {
		
		// 새로운 댓글을 등록한다.
		boardService.updateReply(reply);
		
		// 새롭게 갱신된 댓글 리스트를 가져와 반환한다.
		return boardService.replyList(reply.getBbsNo());
	}
	
	// 댓글 삭제 요청을 처리하는 메서드
	@RequestMapping("/replyDelete.ajax")
	@ResponseBody
	public List<Reply> deleteReply(int no, int bbsNo) {
		
		// 새로운 댓글을 등록한다.
		boardService.deleteReply(no);
		
		// 새롭게 갱신된 댓글 리스트를 가져와 반환한다.
		return boardService.replyList(bbsNo);
	}
}
