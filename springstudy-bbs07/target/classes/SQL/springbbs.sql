## DATABASE 생성 및 선택
CREATE DATABASE IF NOT EXISTS spring;
use spring;

-- 기존의 springbbs 테이블을 삭제하고 추천, 땡큐를 저장할 컬럼을 추가해 다시 생성한다.
-- 게시글 번호, 제목, 이메일, 내용, 글쓴이, 날짜, 조회수, 비밀번호, 파일정보, 추천, 땡큐
-- no, title, email, content, writer, reg_date, read_count, pass, file, recommend, thank
DROP TABLE IF EXISTS springbbs;
CREATE TABLE IF NOT EXISTS springbbs(
  no INTEGER AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(50) NOT NULL,
  writer VARCHAR(10) NOT NULL,  
  content VARCHAR(1000) NOT NULL,
  reg_date TIMESTAMP NOT NULL,
  read_count INTEGER(5) NOT NULL,
  pass VARCHAR(10) NOT NULL,
  file1 VARCHAR(100),
  recommend INTEGER DEFAULT 0,
  thank INTEGER DEFAULT 0
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 아래를 5번 입력한다.
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('공지 사항 입니다.','관리자','안녕하세요\r\n이번에 어쩌구 저쩌구 해서리...\r\n\r\n\r\r\n\n이렇게 운영계획과 약관을 바꾸게 되었습니다.\r\n\r\n회원님들의 양해를 구하며 사이트 이용해 참고 하시기 바랍니다.\r\n\r\n','2017-12-01 01:44:58', 15, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('공지 사항 잘 읽었습니다.','회원1','아~ 관리자님~\r\n\r\n공지사항 잘 읽었습니다.\r\n\r\n그런데 궁금한 것이 있어 게시글 남깁니다.\r\n\r\n이렇게 저렇게 해서리... 궁금합니다.\r\n\r\n답변 부탁 드립니다.','2017-12-01 05:43:38', 53, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('관심을 가져 주셔서...','관리자','안녕하세요\r\n\r\n관리자 입니다.\r\n\r\n이번에 변경된 회원 약관에 대해 관심을 가져 주셔서 감사합니다.\r\n\r\n사이트를 운영하다 보니.. \r\n\r\n어쩔수 없이 어쩌구 저쩌구 해서 약관이 변경되었습니다.\r\n\r\n그럼 좋은 하루 되시길 바랍니다.','2017-12-01 05:44:32', 21, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('저두 궁금했었는데','회원2','안녕하세요\r\n\r\n저두 궁금했었는데...\r\n\r\n님께서 질문을 해 주셔서 고맙습니다.\r\n\r\n즐공하시고 행복한 하루 되세요..^_^','2017-12-01 05:44:32', 26, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('당연히 회원이면 관심을 가져야죠^_^','회원1','안녕하세요\r\n\r\n당연히 회원이니까 약관 변경에 관심을 가져야죠\r\n\r\n아무튼 오늘도 좋은 하루 보내세요','2017-12-01 05:44:32', 10, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('많은 회원님들께서 관심을 가져주셔서 감사합니다.','관리자','안녕하세요\r\n관리자 입니다.\r\n\r\n많은 회원분들께서 이번 약관 변경에 관심을 가져 주셔서 정말 고맙습니다.\r\n\r\n앞으로 더욱 발전하는 사이트가 되겠습니다.\r\n\r\n그럼 언제나 행복하세요.^_^','2017-12-01 16:30:41', 102, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('사이트 발전에 관심이 많은 사람입니다.','회원3','안녕하삼~ 관리자님~\r\n\r\n사이트 발전에 관심이 많은 사람으로서 지금 보다 나은 사이트를 위해\r\n운영계획과 약관을 변경하는 것은 잘 된 일이라 생각합니다.\r\n\r\n그럼 수고 많이 하삼~','2017-12-01 05:03:52', 35, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('정말 정말 감사합니다.','관리자','안녕하세요\r\n\r\n관리자 입니다.\r\n\r\n회원님의 많은 관심에 몸둘바를 모르겠습니다.\r\n\r\n언제나 해복하고 건강하시기 바라겠습니다.','2017-12-01 05:44:32', 19, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('네 잘 알겠습니다.','회원4','안녕하세요...\r\n\r\n역쉬~ 관리자님은 부지런 하십니다.\r\n\r\n하하하~ 사이트가 팍팍 발전해 나가는 것이 보이는 것 같습니다.\r\n\r\n관리자님의 끊임없는 노력에 박수를 보냅니다.\r\n\r\n그럼 수고하세요','2017-12-01 03:37:44', 41, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('내용을 줄 바꿈하지 않아서...','관리자','안녕하세요..\r\n\r\n님께서 작성한 글이 줄 바꿈되지 않아서..\r\n\r\n테이블이 늘어나내요\r\n\r\nㅋㅋㅋ','2017-12-01 05:04:23', 52, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('저두요~','회원5','저두 그게 궁금했는데...\r\n\r\nㅋㅋㅋ 님께서 대신 해주시네요...^_^','2017-12-01 05:44:32', 22, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('당연하죠~','회원6','안녕하세요 관리자님~\r\n\r\n회원이니까 관심을 갖는건 당연하죠..\r\n\r\n아무튼 수고가 많으시네요..','2017-12-01 05:44:32', 11, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('별 말씀을 다하시네요','회원1','안녕하세요 관리자님~\r\n\r\n회원이면 당연히... 관심을 가져야져..\r\n\r\n헤헤헤\r\n\r\n수고하세요','2017-12-01 05:44:32', 13, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('회원이면 당연한 것을...','회원3','ㅋㅋㅋ\r\n\r\n님들도 다 같은 생각을 가지고 계시군요\r\n\r\n뭐 같은 회원이니...\r\n\r\n아무튼 모두들 행복하삼~','2017-12-01 05:44:32', 18, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('그러게요','회원이아닙니다.','아~ \r\n\r\n줄 바꿈 하지 않아도 자동으로 될 줄 알았죠..^_^\r\n\r\n관리자님이 프로그램 잘 해주셔서... 줄 바꿈 해주삼~\r\n\r\n그럼 이만','2017-12-01 04:59:15', 25, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('감사합니다.','관리자','안녕하세요\r\n\r\n답변글 감사합니다.\r\r\n\r\n\n그럼','2017-12-01 03:40:43', 23, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('아 줄 바꿈 문제 해결 했습니다.','관리자','안녕하세요 관리자 입니다.\r\n\r\n먼저 엔터키를 치지않고 글 을 입력하면 줄 바꿈 되지 않는 문제가 발생했는데.. \r\n\r\n어제 해결 했습니다.\r\n\r\n그럼 모두들 즐공 하삼~\r\n\r\n','2017-12-01 04:58:45', 72, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('저두 궁금했는데','회원7','안녕하세요\r\n\r\n저두 궁금한 점이 해결 되었습니다.\r\n\r\n감사합니다.\r\n\r\n그럼 모두 수고하삼~','2017-12-01 05:44:32', 33, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('궁금한게 해결 되었네요','회원8','안녕하세요\r\n\r\n님 덕분에 궁금한점이 해결 되었습니다.\r\n\r\n감사합니다.\r\n\r\n그럼 이만...^_^','2017-12-01 05:50:21', 37, '1234', null);
INSERT INTO springbbs (title,writer,content,reg_date, read_count, pass, file1) VALUES ('감사합니다.','회원1','제 덕분에 궁금한게 해결 되었다니...\r\n\r\n다행입니다.\r\n\r\n즐공하삼~','2017-12-01 05:44:32', 39, '1234', null);
COMMIT;

SELECT * FROM springbbs ORDER BY no DESC;


-- 댓글 테이블
DROP TABLE IF EXISTS reply;
CREATE TABLE IF NOT EXISTS reply(
	no INTEGER AUTO_INCREMENT PRIMARY KEY,
	bbs_no INTEGER NOT NULL,
	reply_content VARCHAR(500) NOT NULL,
	reply_writer VARCHAR(20) NOT NULL,
	reg_date TIMESTAMP NOT NULL,	
	CONSTRAINT reply_fk FOREIGN KEY(bbs_no) REFERENCES springbbs(no)
);


-- 댓글 데이터 추가하기 - 1번만 실행한다. --
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '항상 감사합니다.\r\n땡큐!~', 'midas', '2016-05-08 13:44:32');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '저두 감사드립니다.', 'servlet', '2018-01-09 05:24:57');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '저두요~ ^_^', 'midas', '2018-01-09 09:19:23');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '나두 관심이 많은뎅~', 'servlet', '2018-01-09 11:54:45');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '헤헷~ 감사 합니다.', 'admin', '2018-01-09 12:16:51');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '아~ 다들 이 사이트가 잘되길 바라는 군요', 'midas', '2018-01-09 13:34:11');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '관리자님께서 워낙 신경을 쓰시니...\r\n잘될 겁니다. ', 'servlet', '2018-01-09 14:03:37');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '저두 사이트 발전에 관심이 많습니다.\r\n감사합니다.' , 'midas', '2018-01-09 14:39:29');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '와~ 대단하네요 우리 사이트~\r\nㅋㅋㅋ~', 'servlet', '2018-01-09 14:41:18');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '우리 관리자님이 워낙 열심이시라~\r\n그리고 회원님들도 열성으로 사이트에 충성 접속하시니...\r\n않될 수가 있나요...^_^', 'admin', '2018-01-09 14:52:48');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '네 맞아요~', 'admin', '2018-01-10 15:42:12');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '그렇죠 그렇고 말구요', 'servlet', '2018-01-11 15:44:57');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '항상 감사합니다.', 'midas', '2018-01-15 16:19:23');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '땡큐!~', 'servlet', '2018-01-16 17:31:45');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '저두요~ 땡큐!~', 'servlet', '2018-01-19 18:16:51');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '모두들 열성이네요~\r\n헤헤~\r\n땡큐!~', 'admin', '2018-01-20 10:34:11');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '땡큐~ 여기 붙어라~ ^_^', 'midas', '2018-01-20 11:33:27');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(99, '땡큐~ 붙었어요~', 'admin', '2018-01-20 13:19:59');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '당연 회원이면 관심 가져야죠~', 'servlet', '2018-02-10 13:41:17');
INSERT INTO reply(bbs_no, reply_content, reply_writer, reg_date)VALUES(100, '동감 합니다.\r\n땡큐!~', 'midas', '2018-02-12 14:52:48');

commit;
SELECT * FROM reply;