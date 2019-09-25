package com.springstudy.bbs.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.springstudy.bbs.domain.Member;


 // 이 클래스가 데이터 액세스(데이터 저장소) 계층의 컴포넌트임을 선언한다. 
@Repository
public class MemberDaoImpl implements MemberDao {
	
	/* mybatis-spring 모듈은 MyBatis의 SqlSession 기능과 스프링 DB 지원 기능을
	 * 연동해 주는 SqlSessionTemplate 클래스를 제공한다. SqlSessionTemplate은
	 * SqlSession을 구현해 스프링 연동 부분을 구현 하였기 때문에 우리가 만드는 DAO에서
	 * SqlSessionTemplate 객체를 사용해 SqlSession에 정의된 메서드를 사용할 수 있다.
	 * 
	 * SqlSession과 SqlSessionTemplate는 같은 역할을 담당하고 있지만 트랜잭션
	 * 처리에서 다른 부분이 있다. SqlSession은 commit(), rollback() 메서드를
	 * 명시적으로 호출해 트랜잭션을 처리 하지만 SqlSessionTemplate은 스프링이
	 * 트랜잭션을 처리할 수 있도록 구현되어 있기 때문에 별도로 commit(), rollback()
	 * 메서드를 호출할 필요가 없다.
	 **/	
	private SqlSessionTemplate sqlSession;
	
	/* src/main/resources/repository/mappers/MemberMapper.xml에
	 * 정의한 Mapper namespace를 상수로 정의
	 **/
	private final String NAME_SPACE = "com.springstudy.mappers.MemberMapper";
	
	@Autowired
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	/* member 테이블에서 회원 정보를 조회해 아래와 같이 로그인을 처리하는 메서드
	 *  - 로그인 성공이면 : 1을 반환
	 *  - 비밀번호가 다르면 : 0을 반환
	 *  - id가 존재하지 않으면 : -1을 반환 
	 **/
	@Override
	public int login(String id, String pass) {

		/* MemberMapper.xml에서 맵핑 구문을 작성하고 아래와 같이 SqlSession
		 * 객체의 메서드를 호출하면서 맵핑 설정을 지정하게 되면 이 메서드 안에서
		 * PreparedStatement 객체를 생성하고 PreparedStatement 객체에
		 * 필요한 파라미터가 설정된다.
		 * 
		 * SqlSessionTemplate 객체의 select(), selectOne(), selectList()
		 * 메서드를 호출하면 PreparedStatement 객체의 executeQuery() 메서드를
		 * 실행하고 쿼리를 발행한 결과인 ResultSet 객체에서 데이터를 읽어와 모델 
		 * 클래스인 Member 객체를 생성하고 이 객체에 값을 설정하게 된다.
		 * 
		 * MemberMapper.xml의 아래와 같은 맵핑 구문에 의해서 ResultSet 객체에서
		 * 값을 읽어와 모델 클래스인 Member 객체를 생성하고 값을 설정한다.
		 * 
		 *	<select id="login" resultType="Member">
		 *		SELECT pass FROM member WHERE id = #{id}
		 *	</select>
		 * 		 
		 * SqlSession과 SqlSessionTemplate는 같은 역할을 담당하고 있지만 트랜잭션 
		 * 처리에서 다른 부분이 있다. SqlSession은 commit(), rollback() 메서드를
		 * 명시적으로 호출해 트랜잭션 처리를 하지만 SqlSessionTemplate은 스프링이 
		 * 트랜잭션을 처리할 수 있도록 구현되어 있기 때문에 별도로 commit(), rollback()
		 * 메서드를 호출할 필요가 없다.
		 * 
		 * SqlSessionTemplate 객체의 메서드를 호출할 때 첫 번째 인수에 
		 * repository/mappers/MemberMapper.xml 맵퍼 파일에서
		 * mapper 요소의 namespace 속성에 지정한 값 
		 * com.springstudy.mappers.MemberMapper와
		 * mapper 요소 안에 지정한 <select>, <insert>, <update>, <delete>
		 * 요소의 id 속성에 지정한 값을 패키지 형태로 지정하면 된다. 만약 SQL
		 * 파라미터를 지정해야 한다면 두 번째 인수에 필요한 파라미터를 지정하면
		 * 되는데 파라미터가 여러 개일 경우 Map 객체에 저장해 두 번째 인수로
		 * 지장하면 된다. 
		 * 
		 * 아래와 같이 SqlSessionTemplate의 메서드가 호출되면
		 * repository/mappers/MemberMapper.xml 맵퍼 파일에서
		 * mapper 요소의 namespace 속성에 지정한 
		 * com.springstudy.mappers.MemberMapper인 맵퍼가
		 * 선택되고 그 하부에 <select> 요소의 id 속성에 지정한 login인
		 * 맵핑 구문이 선택되어 MyBatis 내부에서 JDBC 코드로 변환되어 실행된다.
		 * 
		 * 매핑 구문의 resultType 속성에 Member를 지정했기 때문 Member 객체가
		 * 반환된다. id가 존재하지 않으면 검색된 결과가 없으므로 null이 반환 된다. 
		 **/
		Member m = sqlSession.selectOne(NAME_SPACE + ".login", id);
		int result = -1;		
		
		// id가 존재하지 않으면
		if(m == null) {
			return result;
		} 
		
		// 로그인 성공
		if(m.getPass().equals(pass)) {
			result = 1;
		
		// 비밀번호가 틀리면	
		} else {
			result = 0;
		}
		return result;
	}
	
	// member 테이블에서 id에 해당하는 회원 정보를 읽어오는 메서드 
	@Override
	public Member getMember(String id) {
		
		/* 아래와 같이 SqlSessionTemplate의 메서드가 호출되면
		 * repository/mappers/MemberMapper.xml 맵퍼 파일에서
		 * mapper 요소의 namespace 속성에 지정한 
		 * com.springstudy.mappers.MemberMapper인 맵퍼가
		 * 선택되고 그 하부에 <select> 요소의 id 속성에 지정한 getMember인
		 * 맵핑 구문이 선택되어 MyBatis 내부에서 JDBC 코드로 변환되어 실행된다.
		 * 
		 * 매핑 구문에 resultType 속성에 Member를 지정했기 때문 Member 객체가
		 * 반환된다. id가 존재하지 않으면 검색된 결과가 없으므로 null이 반환 된다.
		 **/
		return sqlSession.selectOne(NAME_SPACE + ".getMember", id);
	}
	
	// 회원 정보를 회원 테이블에 저장하는 메서드
	@Override
	public void addMember(Member member) {
		sqlSession.insert(NAME_SPACE + ".addMember", member);
	}
	
	// 회원 정보 수정 시에 기존 비밀번호가 맞는지 체크하는 메서드
	public boolean memberPassCheck(String id, String pass) {
		boolean result = false;
		
		// memberPassCheck 맵핑 구문을 호출하면서 회원 아이디를 파라미터로 지정했다.
		String dbPass = sqlSession.selectOne(
				NAME_SPACE + ".memberPassCheck",	id);

		// 비밀번호가 맞으면 true가 반환된다.
		if(dbPass.equals(pass)) {
			result = true;	
		}
		return result;
	}
	
	// 회원 정보를 DAO를 이용해 회원 테이블에서 수정하는 메서드
	public void updateMember(Member member) {
		sqlSession.update(NAME_SPACE + ".updateMember",	member);
	}
}
