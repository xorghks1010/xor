package com.springstudy.bbs.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.springstudy.bbs.dao.BoardDao;
import com.springstudy.bbs.domain.Board;
import com.springstudy.bbs.domain.FileName;
import com.springstudy.bbs.domain.Reply;

// 이 클래스가 서비스 계층(비즈니스 로직)의 컴포넌트(Bean) 임을 선언하고 있다.
@Service
public class BoardServiceImpl implements BoardService {
	
	// 한 페이지에 보여 줄 게시 글의 수를 상수로 선언
	private static final int PAGE_SIZE = 10;
	
	/* 한 페이지에 보여질 페이지 그룹의 수를 상수로 선언
	 * [이전] 1 2 3 4 5 6 7 8 9 10 [다음]	
	 **/
	private static final int PAGE_GROUP = 10;
	
	/* 인스턴스 필드에 @Autowired annotation을 사용하면 접근지정자가 
	 * private이고 setter 메서드가 없다 하더라도 문제없이 주입 된다.
	 * 하지만 우리는 항상 setter 메서드를 준비하는 습관을 들일 수 있도록 하자.
	 * 
	 * setter 주입 방식은 스프링이 기본 생성자를 통해 이 클래스의 인스턴스를
	 * 생성한 후 setter 주입 방식으로 BoardDao 타입의 객체를 주입하기 때문에  
	 * 기본 생성자가 존재해야 하지만 이 클래스에 다른 생성자가 존재하지 않으므로
	 * 컴파일러에 의해 기본 생성자가 만들어 진다.
	 **/
	@Autowired
	private BoardDao boardDao;
	
	@Autowired
	private TransactionTemplate transactionTemplate;
	
	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}
	
	/* BoardDao를 이용해 게시판 테이블에서 한 페이지에 해당하는 게시 글
	 * 리스트와 페이징 처리에 필요한 데이터를 Map 객체로 반환 하는 메소드
	 **/
	@Override
	public Map<String, Object> boardList(
			int pageNum, String type, String keyword) {
		
		// 요청 파라미터의 pageNum을 현재 페이지로 설정
		int currentPage = pageNum;
				
		/* 요청한 페이지에 해당하는 게시 글 리스트의 첫 번째 행의 값을 계산
		 * MySQL에서 검색된 게시 글 리스트의 row에 대한 index는 0부터 시작한다.
		 * 현재 페이지가 1일 경우 startRow는 0, 2페이지일 경우 startRow는 10이 된다.
		 * 
		 * 예를 들어 3페이지에 해당하는 게시 글 리스트를 가져 온다면 한 페이지에 보여줄
		 * 게시 글 리스트의 수가 10개로 지정되어 있으므로 startRow는 20이 된다. 
		 * 즉 아래의 공식에 의해 startRow(20) = (3 - 1) * 10;
		 * 첫 번째 페이지 startRow = 0, 두 번째 페이지 startRow = 10이 된다.
		 **/		
		int startRow = (currentPage - 1) * PAGE_SIZE;		
		int listCount = 0;
		
		/* 요청 파라미터에서 type이나 keyword가 비어 있으면 일반 
		 * 게시 글 리스트를 요청하는 것으로 간주하여 false 값을 갖게 한다.
		 * Controller에서 type이나 keyword의 요청 파라미터가 없으면
		 * 기본 값을 "null"로 지정했기 때문에 아래와 같이 체크했다.
		 **/
		boolean searchOption = (type.equals("null") 
				|| keyword.equals("null")) ? false : true; 
		
		/* 맵핑 구문 안에서 동적쿼리를 사용해 type이 없으면 전체 게시 글의
		 * 수를 반환하고, type이 존재하면 제목이나 내용 또는 작성자를 기준으로
		 * 검색어가 포함된 게시 글 수를 반환한다.
		 **/
		listCount = boardDao.getBoardCount(type, keyword);		
		System.out.println("listCount : " + listCount + ", type : " 
					+ type + ", keyword : " + keyword);
		
		/* 게시 글 리스트가 하나 이상 존재하면 요청한 페이지(currentPage)에 해당하는
		 * 게시 글 리스트를 DB로 부터 읽어 Board 객체를 저장하는 ArrayList에 저장한다. 
		 **/
		if(listCount > 0) {
			
			/* Oracle에서는 페이징 처리를 위해 의사컬럼인 ROWNUM을 사용했지만
			 * MySQL은 검색된 데이터에서 특정 행 번호부터 지정한 개수 만큼 행을 읽어오는
			 * LIMIT 명령을 제공하고 있다. LIMIT의 첫 번째 매개변수에 가져올 데이터의
			 * 시작 행을 지정하고 두 번째 매개변수에 가져올 데이터의 개수를 지정하면 된다.
			 **/
			List<Board> boardList = boardDao.boardList(
					startRow, PAGE_SIZE, type, keyword);
			
			/* 페이지 그룹 이동 처리를 위해 전체 페이지를 계산 한다.
			 * [이전] 11 12 13...   또는   ... 8 9 10 [다음] 과 같은 페이징 처리
			 * 전체 페이지 = 전체 게시 글 수 / 한 페이지에 표시할 게시 글 수가 되는데 
			 * 이 계산식에서 나머지가 존재하면 전체 페이지 수는 전체 페이지 + 1이 된다.
			 **/	
			int pageCount = 
					listCount / PAGE_SIZE + (listCount % PAGE_SIZE == 0 ? 0 : 1);
			
			/* 페이지 그룹 처리를 위해 페이지 그룹별 시작 페이지와 마지막 페이지를 계산
			 * 페이지 그룹 별 시작 페이지 : 1, 11, 21, 31...
			 * 첫 번째 페이지 그룹에서 페이지 리스트는 1 ~ 10이 되므로 currentPage가
			 * 1 ~ 10 사이에 있으면 startPage는 1이 되고 11 ~ 20 사이면 11이 된다.
			 * 
			 * 정수형 연산의 특징을 이용해 startPage를 아래와 같이 구할 수 있다.
			 * 아래 연산식으로 계산된 결과를 보면 현재 그룹의 마지막 페이지일 경우
			 * startPage가 다음 그룹의 시작 페이지가 나오게 되므로 삼항 연자자를
			 * 사용해 현재 페이지가 속한 그룹의 startPage가 되도록 조정 하였다.
			 * 즉 currentPage가 10일 경우 다음 페이지 그룹의 시작 페이지가 되므로
			 * 삼항 연산자를 사용하여 PAGE_GROUP으로 나눈 나머지가 0이면
			 * PAGE_GROUP을 차감하여 현재 그룹의 시작 페이지가 되도록 하였다.
			 **/
			int startPage = (currentPage / PAGE_GROUP) * PAGE_GROUP + 1
					- (currentPage % PAGE_GROUP == 0 ? PAGE_GROUP : 0);		
						
			// 현재 페이지 그룹의 마지막 페이지 : 10, 20, 30...
			int endPage = startPage + PAGE_GROUP - 1;
			
			/* 위의 식에서 endPage를 구하게 되면 endPage는 항상 PAGE_GROUP의
			 * 크기만큼 증가(10, 20, 30 ...) 되므로 맨 마지막 페이지 그룹의 endPage가
			 * 정확하지 못할 경우가 발생하게 된다. 다시 말해 전체 페이지가 53페이지라고
			 * 가정하면 위의 식에서 계산된 endPage는 60 페이지가 되지만 실제로 
			 * 60페이지는 존재하지 않는 페이지이므로 문제가 발생하게 된다.
			 * 그래서 맨 마지막 페이지에 대한 보정이 필요하여 아래와 같이 endPage와
			 * pageCount를 비교하여 현재 페이지 그룹에서 endPage가 pageCount 보다
			 * 크다면 pageCount를 endPage로 지정 하였다. 즉 현재 페이지 그룹이
			 * 마지막 페이지 그룹이면 endPage는 전체 페이지 수가 되도록 지정한 것이다.
			 **/
			if(endPage > pageCount) {
				endPage = pageCount;
			}
						
			/* View 페이지에서 필요한 데이터를 Map에 저장한다.
			 * 현재 페이지, 전체 페이지 수, 페이지 그룹의 시작 페이지와 마지막 페이지
			 * 게시 글 리스트의 수, 한 페이지에 보여 줄 게시 글 리스트의 데이터를 Map에
			 * 저장해 컨트롤러로 전달한다.
			 **/
			Map<String, Object> modelMap = new HashMap<String, Object>();		
			
			modelMap.put("boardList", boardList);
			modelMap.put("pageCount", pageCount);
			modelMap.put("startPage", startPage);
			modelMap.put("endPage", endPage);
			modelMap.put("currentPage", currentPage);
			modelMap.put("listCount", listCount);
			modelMap.put("pageGroup", PAGE_GROUP);
			modelMap.put("searchOption", searchOption);
			
			// 검색 요청이면 type과 keyword를 모델에 저장한다.
			if(searchOption) {
				
				/* IE에서 링크로 요청 시 파라미터에 한글이 포함되면 IE는 URLEncoding을
				 * 하지 않고 서버로 전송하는데 톰캣 7.06x 버전에서 정상적으로 동작하던 것이
				 * 7.07x 버전에서는 Invalid character found in the request target
				 * 이라는 에러가 발생한다. 이 문제는 java.net 패키지의 URLEncoder
				 * 클래스를 이용해 수동으로 URLEncoding을 해주면 해결할 수 있다.
				 * 크롬 브라우저는 링크로 요청 시 파라미터에 한글이 포함되어 있으면 브라우저 
				 * 주소창에는 한글 그대로 표시되지만 UTF-8로 URLEncoding을 해준다.
				 **/
				try {
					modelMap.put("keyword", URLEncoder.encode(keyword, "utf-8"));
				} catch (UnsupportedEncodingException e) {					
					e.printStackTrace();
				}
				modelMap.put("word", keyword);
				modelMap.put("type", type);
			}
			
			return modelMap;			
		} else {
			return null;
		}
	}

	/* ajax 용 테스트 메서드
	 **/
	@Override
	public List<Board> boardList() {
		return boardDao.boardList(0, 10, "null", "null");
	}
	
	/* @Transactional 애노테이션을 이용한 트랜잭션 처리는 아주 간단하다.
	 *	트랜잭션을 적용할 클래스나 메서드에 @Transactional()만 기술하고
	 * 이 애노테이션의 () 안에 트랜잭션 관련 속성을 설정하기만 하면 된다.
	 * 이 애노테이션에 지정할 수 있는 트랜잭션 관련 속성은 아래와 같다.
	 * 
	 * ▶ propargation : 트랜잭션 전파 속성을 지정한다.
	 *   트랜잭션을 시작할 때 새로운 트랜잭션을 시작할 지 기존에 이미 시작된 트랜잭션을
	 *   사용할지 아니면 트랜잭션을 적용하지 않을지 등을 지정하는 속성으로 트랜잭션
	 *   전파 범위(트랜잭션의 경계)는 아래와 같은 값을 지정할 수 있다.   
	 *   propargation= Propagation.REQUIRED와 같이 지정한다.
	 *
	 *   - REQUIRED -> Default
	 *   트랜잭션이 필요하다는 것을 의미 하며 현재 진행 중인 트랜잭션 안에서 실행
	 *   되면 기존의 트랜잭션을 사용하고 진행중인 트랜잭션이 없으며 새로운 트랜잭션을
	 *   시작한다.
	 *   
	 *   - SUPPORTS
	 *   트랜잭션이 필요하지 않지만 현재 진행중인 트랜잭션이 존재하면 기존 트랜
	 *   잭션을 사용하고 진행 중인 트랜잭션이 없어도 익셉션은 발생되지 않는다.
	 *   
	 *   - MANDATORY
	 * 	  트랜잭션이 필요하다는 것을 의미 하지만 REQUIRED와는 달리 현재 진행
	 *   중인 트랜잭션이 존재하지 않으면 익셉션이 발생한다.
	 *   
	 *   - REQUIRES_NEW
	 *   현재 진행중인 트랜잭션이 존재하면 기존 트랜잭션을 현재 상태에서 대기
	 *   시키고 새로운 트랜잭션을 시작한다. 새로운 트랜잭션이 종료되어야 기존의
	 *   트랜잭션이 이어서 시작 된다. 이 옵션을 적용하면 항상 새로운 트랜잭션으로
	 *   시작한다.
	 *   
	 *   - NOT_SUPPORTED
	 *   트랜잭션이 필요하지 않음을 의미하며 현재 진행중인 트랜잭션 안에서 실행
	 *   되면 진행 중인 트랜잭션을 현재 상태에서 대기 시키고 메서드가 종료된 후에
	 *   기존의 트랜잭션을 재기한다.
	 *   
	 *   - NEVER
	 *   트랜잭션이 필요하지 않음을 의미하며 현재 진행중인 트랜잭션이 존재하면
	 *   익셉션이 발생한다.
	 *   
	 *   - NESTED
	 *   현재 진행중인 트랜잭션이 존재하면 기존 트랜잭션에 중첩된 트랜잭션에서
	 *   실행된다. 기존 트랜잭션이 존재하지 않으면 PROPAGATION_REQUIRED
	 *   동일하다. JDBC 3.0 드라이버를 사용할 때 적용된다.
	 *    
	 * ▶ isolation : 트랜잭션 격리 속성을 지정한다.
	 *   트랜잭션이 병행되어 실행될 때 데이터 접근에 대한 격리 수준을 지정하는 속성
	 *   isolation=Isolation.DEFAULT와 같이 지정한다.
	 * 	
	 *   - DEFAULT -> Default
	 *   데이터베이스가 제공하는 기본 값을 사용한다.
	 *   
	 *   - READ_UNCOMMITTED
	 *   다른 트랜잭션에 의해 변경되고 아직 커밋되지 않은 데이터를 읽어 올 수 있다.
	 *   
	 *   - READ_COMMITTED
	 *   다른 트랜잭션에 의해 커밋된 데이터를 읽어 올 수 있다.
	 *   
	 *   - REPEATABLE_READ
	 *   트랜잭션에서 여러 번 데이터를 읽어 올 때 다른 트랜잭션에서 데이터를 변경
	 *   해도 같은 값을 읽어 온다.
	 *   
	 *   - SERIALIZABLE
	 *   실행중인 트랜잭션을 순서대로 처리해 동시에 동일한 데이터에 접근할 수
	 *   없도록 격리 시킨다.
	 *   
	 * ▶ rollback-for : 트랜잭션을 롤백할 예외 타입을 지정한다.
	 * 	  트랜잭션이 적용된 메서드가 실행될 때 예외가 발생하면 트랜잭션을 롤백할
	 * 	  예외 타입을 지정한다. 예외 타입이 한 개가 아니라면 콤마(,)를 사용해
	 * 	  여러 개의 예외 타입을 지정할 수 있다. 예외 타입을 지정할 때는 패키지를
	 * 	  포함한 완전한 클래스 이름을 지정해도 되고 클래스 이름만 지정해도 된다.
	 *   rollbackFor= { Exception.class }와 같이 배열로 지정한다.
	 * 
	 * ▶ no-rollback-for : 트랜잭션을 롤백하지 않을 예외 타입을 지정한다.
	 * 	  트랜잭션이 적용된 메서드가 실행될 때 예외가 발생하더라도 롤백하지 않을
	 * 	  예외 타입을 지정한다. 콤마(,)로 구분해 여러 개의 예외 타입을 지정할 수 있다.
	 *   noRollBackFor : { AccountNotFoundException. class}와
	 *   같이 배열로 지정한다.
	 * 
	 * ▶ readOnly : 트랜잭션이 읽기전용 인지 지정하는 속성
	 * 	  읽기 전용 트랜잭션 여부를 지정하는 속성, 기본 값은 false로 읽고 쓰기
	 * 	  트랜잭션 이다. 
	 *   readOnly=false와 같이 지정한다.
	 *
	 * ▶ timeout : 트랜잭션 타임아웃 속성
	 * 	  트랜잭션 타임아웃 값을 초 단위로 지정, 기본 값은 -1로 타임아웃이 없다.
	 * 
	 * 	스프링프레임워크는 기본적으로 RuntimeException 및 Error에 대해서만
	 * 	롤백 처리를 수행한다. 그래서 rollback-for나 no-rollback-for를 지정해
	 * 	정교하게 예외를 제어할 수 있다.
	 **/
	@Transactional(propagation=Propagation.REQUIRED,
			isolation=Isolation.DEFAULT,
			rollbackFor= {Exception.class, RuntimeException.class})
	
	/* BoardDao를 이용해 게시판 테이블에서
	 * no에 해당하는 게시 글 을 읽어와 반환하는 메서드 
 	 * isCount == true 면 게시 상세보기, false 면 게시 글 수정 폼 요청임 
	 **/
	@Override
	public Board getBoard(int no, boolean isCount) {		
		return boardDao.getBoard(no, isCount);
	}
	
	// 게시 글 번호에 해당하는 댓글 리스트를 반환하는 메서드
	public List<Reply> replyList(int no) {
		return boardDao.replyList(no);
	}
	
	// 코드기반 트랜잭션 메서드
	@Override
	public Board getBoardCode(int no, boolean isCount) {
		
		/* 스프링이 지원하는 트랜잭션 기능을 코드에서 직접 처리 하려면 아래와 같이
		 * TransactionTemplate 클래스의 execute() 메서드를 이용한다.
		 * execute() 메서드는 그 내부에서 PlaformTransactionManager를
		 * 이용해 트랜잭션을 시작하고 매개변수로 넘겨 받은 TransactionCallback
		 * 객체의 doInTransaction() 메서드를 호출한다. doInTransaction()
		 * 메서드가 정상적으로 완료되면 execute() 메서드는 트랜잭션을 커밋하고 
		 * doInTransaction() 메서드가 반환한 데이터를 받아 그대로 반환한다. 
		 * 우리가 처리할 트랜잭션 코드를 doInTransaction() 메서드 안에 기술하면
		 * 된다. doInTranscation() 메서드 내부에서 RuntimeException이
		 * 발생하면 execute() 메서드는 PlaformTransactionManager를
		 * 이용해 트랜잭션을 롤백하고 예외를 전파 시킨다. doInTransaction()
		 * 메서드는 throws를 이용해 예외를 정의하지 않았기 때문에 Error 계열
		 * 예외와 RuntimeException만 트랜잭션 처리가 가능하다.
		 * 만약 Checked 계열 예외를 처리하고 싶다면 doInTransaction()
		 * 메서드 안에서 try{ }catch{ } 구문을 이용해 예외를 처리하면 된다.
		 * Checked 계열 예외가 발생하면 catch 블록안에서 매개변수로 넘겨 받은
		 * TransactionStatus 객체의 setRollbackOnly() 메서드를 호출해
		 * 트랜잭션을 롤백하면 된다. 이 때 반환되는 타입은 메서드가 정상적으로
		 * 실행이 완료될 때 반환할 타입과 예외가 발생되면 반환할 타입 모두를
		 * 적용해야 하므로 Object 타입을 지정하는 것이 좋다.
		 **/
		return transactionTemplate.execute(
			new TransactionCallback<Board>() {
			@Override
			public Board doInTransaction(
					TransactionStatus transactionStatus) {
				
				Board board = boardDao.getBoard(no, isCount);
				
				/* TransactionTemplate은 doInTransaction() 메서드 안에서
				 * RuntimeException이 발생하면 PlatformTransactionManager의
				 * rollback() 메서드를 호출해 트랜잭션을 롤백 시킨다.
				 **/
				
				/* 트랜잭션을 테스트 할 강제 예외 발생
				 * 위의 로직까지 계좌이체 처리가 완료된 상태에서 예외가 발생하면
				 * 트랜잭션은 rollback 되어 계좌는 이체 전의 상태로 돌아간다.
				 * 아래와 같이 강제 예외를 발생할 경우 return은 주석 처리할 것
				 * 
				 * 코드기반 트랜잭션은 doInTransaction() 메서드 안에서
				 * 하나의 커넥션을 가지고 작업하기 때문에 아래 예외가 발생하면
				 * 트랜잭션이 롤백 된다.
				 **/				
				// throw new RuntimeException("게시 글 읽은 횟수 증가 실패");
				
				// 처리가 완료되면 게시 글 정보를 반환한다.
				return board;
			}	
		});		
	}
	
	// BoardDao를 이용해 새로운 게시 글을 추가하는 메서드
	@Override
	public void insertBoard(Board board) {			
			// 파일 업로드가 완료되면 BoardDao를 이용해 게시 글을 DB에 저장한다.
			boardDao.insertBoard(board);
	
	}
	
	/* 게시 글 수정, 삭제 시 비밀번호 입력을 체크하는 메서드
	 * 
	 * - 게시 글의 비밀번호가 맞으면 : true를 반환
	 * - 게시 글의 비밀번호가 맞지 않으면 : false를 반환
	 **/
	public boolean isPassCheck(int no, String pass) {	
		return boardDao.isPassCheck(no, pass);
	}
	
	// BoardDao를 이용해 게시 글을 수정하는 메서드
	@Override
	public void updateBoard(Board board) {
		boardDao.updateBoard(board);
	}

	// BoardDao를 이용해 no에 해당하는 게시 글을 삭제하는 메서드
	@Override
	public void deleteBoard(int no) {
		boardDao.deleteBoard(no);
	}
		
	@Transactional
	/* @Transactional 애노테이션을 이용한 트랜잭션 처리 메서드
	 * 다중 파일 정보를 files 테이블에 저장하는 메서드 
	 */
	@Override
	public void insertBoardMulti(
						String filePath,
						Board board, 
						MultipartFile multipartFile, 
						MultipartFile[] multiFiles) throws Exception {		
		
		/* 업로드한 Multipart 데이터(파일)에 접근하기
		 * 스프링은 MultipartResolver를 사용해 멀티파트 데이터에
		 * 접근할 수 있는 아래와 같은 다양한 방법을 제공하고 있다.
		 * 
		 * - MultipartFile 인터페이스를 이용한 접근
		 * - @RequestParam 애노테이션을 이용한 접근
		 * - MultipartHttpServletRequest를 이용한 접근
		 * - 커맨드 객체를 이용한 접근
		 *   커맨드 클래스에 MultipartFile 타입의 프로퍼티가 있어야 한다. 
		 * - 서블릿 3의 Part를 이용한 접근
		 * 
		 * 이 예제는 MultipartFile을 이용한 파일 업로드 방법을
		 * 소개하고 있다.
		 **/		
		if(!multipartFile.isEmpty()) { // 업로드된 파일 데이터가 존재하면
			
			/* UUID(Universally Unique Identifier, 범용 고유 식별자)
			 * 소프트웨어 구축에 쓰이는 식별자의 표준으로 네트워크 상에서 서로 모르는
			 * 개체들을 식별하고 구별하기 위해서 사용된다. UUID 표준에 따라 이름을
			 * 부여하면 고유성을 완벽하게 보장할 수는 없지만 실제 사용상에서 중복될 
			 * 가능성이 거의 없다고 인정되기 때문에 실무에서 많이 사용되고 있다.
			 * 
			 * 파일 이름의 중복을 막고 고유한 파일 이름으로 저장하기 위해 java.util
			 * 패키지의 UUID 클래스를 이용해 랜덤한 UUID 값을 생성한다.
			 **/
			UUID uid = UUID.randomUUID();
			String saveName = 
					uid.toString() + "_" + multipartFile.getOriginalFilename();
			
			File file = new File(filePath, saveName);
			System.out.println("insertBoardMulti - newName : " + file.getName());			
			
			// 업로드 되는 파일을 upload 폴더로 저장한다.
			multipartFile.transferTo(file);
			
			// 업로드된 파일 명을 Board 객체에 저장한다.
			board.setFile1(saveName);
		}
		
		/* BoardService 클래스를 이용해
		 * 폼에서 넘어온 게시 글 정보를 게시 글 테이블에 추가한다.
		 **/
		boardDao.insertBoard(board);
		
		/* BoardMapper에서 게시 글 추가하는 맵핑 구문을 아래와 같이 작성했다.
		 *
		 * 	<insert id="insertBoard" parameterType="Board"
		 * 		useGeneratedKeys="true" keyProperty="no">
		 * 
		 * 테이블에 하나의 레코드를 INSERT 할때 자동으로 증가되는 컬럼이나
		 * Sequence를 사용하는 컬럼의 값을 읽어와야 할 때도 있다.
		 * 보통 자동 증가되는 컬럼의 값은 데이터가 INSERT 된 후에 읽어오고
		 * Sequence일 경우 INSERT 이전에 값을 읽어와야 한다.
		 * 이렇게 INSERT 작업을 하면서 생성된 키의 값을 읽어와야 할 경우
		 * 아래와 같이 useGeneratedKeys="true"를 지정하고 자동 생성된
		 * 키의 값을 설정할 자바 모델 객체의 프로퍼티 이름을 keyProperty에
		 * 지정하면 Board 객체의 no 프로퍼티에 값을 설정해 준다.
		 **/
		System.out.println("insert No : " + board.getNo());	
		
		// 다중 파일 업로드 처리
		ArrayList<FileName> fileNames = new ArrayList<FileName>();	
		
		// Exception 강제 발생		
		//throw new RuntimeException("RuntimeException 발생");
				
		for(int i = 0; i < multiFiles.length; i++) {
			
			MultipartFile uploadFile = multiFiles[i];
			
			// 다중 업로드된 파일 데이터가 존재하면
			if(!uploadFile.isEmpty()) { 

				UUID uid = UUID.randomUUID();
				String saveName = 
						uid.toString() + "_" + uploadFile.getOriginalFilename();
				
				File file = new File(filePath, saveName);
				System.out.println("multiNewName : " + file.getName());			
				
				// 업로드 되는 파일을 upload 폴더로 저장한다.
				uploadFile.transferTo(file);
				
				/* FileName 객체를 생성해 파일 하나의 정보를 저장하여
				 * 리스트에 담는다. 
				 **/
				FileName fileName = new FileName(saveName, board.getNo());
				fileNames.add(fileName);
			}
		}		
		
		/* 다중 파일 정보가 비어있지 않으면 BoardService 클래스를
		 * 이용해 폼에서 넘어온 다중 파일 정보를 files 테이블에 추가한다.
		 **/
		if(! fileNames.isEmpty()) {
			boardDao.insertFiles(fileNames);
		}
	}
	
	/* 코드기반 트랜잭션 처리 메서드
	 * 다중 파일 정보를 files 테이블에 저장하는 메서드 
	 **/
	@Override
	public Object insertBoardMultiCode(			
						String filePath,
						Board board, 
						MultipartFile multipartFile, 
						MultipartFile[] multiFiles) throws Exception {		
		
		/* 스프링이 지원하는 트랜잭션 기능을 코드에서 직접 처리 하려면 아래와 같이
		 * TransactionTemplate 클래스의 execute() 메서드를 이용한다.
		 * execute() 메서드는 그 내부에서 PlaformTransactionManager를
		 * 이용해 트랜잭션을 시작하고 매개변수로 넘겨 받은 TransactionCallback
		 * 객체의 doInTransaction() 메서드를 호출한다. doInTransaction()
		 * 메서드가 정상적으로 완료되면 execute() 메서드는 트랜잭션을 커밋하고 
		 * doInTransaction() 메서드가 반환한 데이터를 받아 그대로 반환한다. 
		 * 우리가 처리할 트랜잭션 코드를 doInTransaction() 메서드 안에 기술하면
		 * 된다. doInTranscation() 메서드 내부에서 RuntimeException이
		 * 발생하면 execute() 메서드는 PlaformTransactionManager를
		 * 이용해 트랜잭션을 롤백하고 예외를 전파 시킨다. doInTransaction()
		 * 메서드는 throws를 이용해 예외를 정의하지 않았기 때문에 Error 계열
		 * 예외와 RuntimeException만 트랜잭션 처리가 가능하다.
		 * 만약 Checked 계열 예외를 처리하고 싶다면 doInTransaction()
		 * 메서드 안에서 try{ }catch{ } 구문을 이용해 예외를 처리하면 된다.
		 * Checked 계열 예외가 발생하면 catch 블록안에서 매개변수로 넘겨 받은
		 * TransactionStatus 객체의 setRollbackOnly() 메서드를 호출해
		 * 트랜잭션을 롤백하면 된다. 이 때 반환되는 타입은 메서드가 정상적으로
		 * 실행이 완료될 때 반환할 타입과 예외가 발생되면 반환할 타입 모두를
		 * 적용해야 하므로 Object 타입을 지정하는 것이 좋다.
		 **/
		return transactionTemplate.execute(
			new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(
						TransactionStatus transactionStatus) {
				
				Object obj = null;
				
				try {
					/* 업로드한 Multipart 데이터(파일)에 접근하기
					 * 스프링은 MultipartResolver를 사용해 멀티파트 
					 * 데이터에 접근할 수 있는 아래와 같은 다양한 방법을
					 * 제공하고 있다.
					 * 
					 * - MultipartFile 인터페이스를 이용한 접근
					 * - @RequestParam 애노테이션을 이용한 접근
					 * - MultipartHttpServletRequest를 이용한 접근
					 * - 커맨드 객체를 이용한 접근
					 *   커맨드 클래스에 MultipartFile 타입의 프로퍼티가
					 *   있어야 한다. 
					 * - 서블릿 3의 Part를 이용한 접근
					 * 
					 * 이 예제는 MultipartFile을 이용한 파일 업로드 방법을
					 * 소개하고 있다.
					 **/		
					if(!multipartFile.isEmpty()) { // 업로드된 파일 데이터가 존재하면
						
						/* UUID(Universally Unique Identifier, 범용 고유 식별자)
						 * 소프트웨어 구축에 쓰이는 식별자의 표준으로 네트워크
						 * 상에서 서로 모르는 개체들을 식별하고 구별하기 위해서
						 * 사용된다. UUID 표준에 따라 이름을 부여하면 고유성을
						 * 완벽하게 보장할 수는 없지만 실제 사용상에서 중복될 
						 * 가능성이 거의 없다고 인정되기 때문에 실무에서 많이
						 * 사용되고 있다.
						 * 
						 * 파일 이름의 중복을 막고 고유한 파일 이름으로 저장하기
						 * 위해 java.util 패키지의 UUID 클래스를 이용해 
						 * 랜덤한 UUID 값을 생성한다.
						 **/
						UUID uid = UUID.randomUUID();
						String saveName = uid.toString() + "_" 
								+ multipartFile.getOriginalFilename();
						
						File file = new File(filePath, saveName);
						System.out.println("insertBoardMultiCode - newName : " 
									+ file.getName());			
						
						// 업로드 되는 파일을 upload 폴더로 저장한다.					
						multipartFile.transferTo(file);
						
						// 업로드된 파일 명을 Board 객체에 저장한다.
						board.setFile1(saveName);
					}
					
					/* BoardService 클래스를 이용해
					 * 폼에서 넘어온 게시 글 정보를 게시 글 테이블에 추가한다.
					 **/
					boardDao.insertBoard(board);
					
					/* BoardMapper에서 게시 글 추가하는 맵핑 구문을
					 * 아래와 같이 작성했다.
					 *
					 * 	<insert id="insertBoard" parameterType="Board"
					 * 		useGeneratedKeys="true" keyProperty="no">
					 * 
					 * 테이블에 하나의 레코드를 INSERT 할때 자동으로 증가되는
					 * 컬럼이나 Sequence를 사용하는 컬럼의 값을 읽어와야
					 * 할 때도 있다. 보통 자동 증가되는 컬럼의 값은 데이터가 
					 * INSERT 된 후에 읽어오고 Sequence일 경우 INSERT
					 * 이전에 값을 읽어와야 한다. 이렇게 INSERT 작업을 하면서
					 * 생성된 키의 값을 읽어와야 할 경우 아래와 같이 
					 * useGeneratedKeys="true"를 지정하고 자동 생성된
					 * 키의 값을 설정할 자바 모델 객체의 프로퍼티 이름을 keyProperty에
					 * 지정하면 Board 객체의 no 프로퍼티에 값을 설정해 준다.
					 **/
					System.out.println("Code insert No : " + board.getNo());	
					
					// 다중 파일 업로드 처리
					ArrayList<FileName> fileNames = new ArrayList<FileName>();	
					
					// Exception 강제 발생		
					//throw new RuntimeException("RuntimeException 발생");
							
					for(int i = 0; i < multiFiles.length; i++) {
						
						MultipartFile uploadFile = multiFiles[i];
						
						// 다중 업로드된 파일 데이터가 존재하면
						if(!uploadFile.isEmpty()) {
							
							UUID uid = UUID.randomUUID();
							String saveName = uid.toString() + "_" 
									+ uploadFile.getOriginalFilename();
							
							File file = new File(filePath, saveName);
							System.out.println(
									"Code multiNewName : " + file.getName());			
							
							// 업로드 되는 파일을 upload 폴더로 저장한다.
							uploadFile.transferTo(file);
							
							/* FileName 객체를 생성해 파일 하나의 정보를 저장하여
							 * 리스트에 담는다. 
							 **/
							FileName fileName = 
									new FileName(saveName, board.getNo());
							fileNames.add(fileName);
						}
					}		
					
					/* 다중 파일 정보가 비어있지 않으면 BoardService 클래스를
					 * 이용해 폼에서 넘어온 다중 파일 정보를 files 테이블에 추가한다.
					 **/
					if(! fileNames.isEmpty()) {
						boardDao.insertFiles(fileNames);
					}
				}catch(Exception e) {
					transactionStatus.setRollbackOnly();
					obj = e;
					e.printStackTrace();
				}
				
				return obj;
			}
		});
	}
	
	// 추천/땡큐 정보를 업데이트하고 갱신된 추천/땡큐를 가져오는 메서드
	public Map<String, Integer> recommend(int no, String recommend) {
		
		boardDao.updateRecommend(no, recommend);
		Board board = boardDao.getRecommend(no);
		
		Map<String, Integer> map = new HashMap<String, Integer>(); 
		map.put("recommend", board.getRecommend());
		map.put("thank", board.getThank());
		return map;
	}
	
	// BoardDao를 이용해 게시 글 번호에 해당하는 댓글을 등록하는 메서드
	public void addReply(Reply reply) {
		boardDao.addReply(reply);
	}
	
	// BoardDao를 이용해 댓글을 수정하는 메서드
	public void updateReply(Reply reply) {
		boardDao.updateReply(reply);
	}

	// BoardDao를 이용해 댓글 번호에 해당하는 댓글을 삭제하는 메서드
	public void deleteReply(int no) {
		boardDao.deleteReply(no);
	}
}
