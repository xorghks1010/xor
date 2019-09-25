## DATABASE 생성 및 선택
CREATE DATABASE IF NOT EXISTS spring;
use spring;

# 테이블 생성
DROP TABLE IF EXISTS member;
CREATE TABLE IF NOT EXISTS member(
	id VARCHAR(20) PRIMARY KEY,
	name VARCHAR(10) NOT NULL,
	pass VARCHAR(20) NOT NULL,
	email VARCHAR(30) NOT NULL,
	mobile VARCHAR(13) NOT NULL,
	zipcode VARCHAR(5) NOT NULL,
	address1 VARCHAR(80) NOT NULL,
	address2 VARCHAR(60) NOT NULL,
	phone VARCHAR(13),
	email_get VARCHAR(1),
	reg_date TIMESTAMP NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

# 회원 정보 추가
INSERT INTO member VALUES('midas', '홍길동', '1234', 
	'midastop@naver.com', '010-1234-5678', '14409', 
	'경기 부천시 오정구 수주로 18 (고강동, 동문미도아파트)', '미도아파트 1동 513호', 
	'032-1234-5678', '1', '2016-06-06 12:10:30');
INSERT INTO member VALUES('admin', '이순신', '1234', 
	'midastop1@naver.com', '010-4321-8765', '08292', 
	'서울 구로구 구로중앙로34길 33-4(구로동, 영림빌딩)', '경영기술개발원 교육센터 1층 교무실', 
	'02-5678-4325', '0', '2016-05-11 11:20:50');
INSERT INTO member VALUES('servlet', '강감찬', '1234', 
	'midas@daum.net', '010-5687-5678', '06043', 
	'서울 강남구 강남대로146길 28 (논현동, 논현아파트)', '논현신동아파밀리에아파트 103동 302호', 
	'02-5326-5678', '1', '2016-06-05 12:10:30');

commit;
SELECT * FROM member;