CREATE TABLE Board
(
    idx              NUMBER(4),
    name          VARCHAR2(20)  NOT NULL,
    email           VARCHAR2(100),
    title             VARCHAR2(200) NOT NULL,
    contents      VARCHAR2(2000) NOT NULL,
    writedate      DATE    DEFAULT SYSDATE   NOT NULL,
    readcount     NUMBER(4)   DEFAULT 0   NOT NULL,
    filename       VARCHAR2(500),
    CONSTRAINT board_idx_pk  PRIMARY KEY(idx)
);

CREATE  SEQUENCE board_idx_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE    9999
    NOCACHE
    NOCYCLE;

--게시판 목록 가져오기
CREATE OR REPLACE PROCEDURE sp_board_select_all
(
    board_record    OUT   SYS_REFCURSOR
)
AS
BEGIN
    OPEN board_record FOR
    SELECT idx, name, email, title, writedate, readcount, filename
    FROM Board  
    ORDER BY idx DESC;
END;

--새로운 글 입력하기(파일이름 들어옴)
CREATE OR REPLACE PROCEDURE SP_BOARD_INSERT
(
    V_NAME      IN  BOARD.NAME%TYPE,
    V_EMAIL         IN  BOARD.EMAIL%TYPE,
    V_TITLE         IN  BOARD.TITLE%TYPE,
    V_CONTENTS       IN  BOARD.CONTENTS%TYPE,
    V_FILENAME        IN  BOARD.FILENAME%TYPE
)
IS
BEGIN
    INSERT INTO BOARD(IDX, NAME,  EMAIL, TITLE, CONTENTS, FILENAME)
    VALUES(BOARD_IDX_SEQ.NEXTVAL, V_NAME, V_EMAIL, V_TITLE, V_CONTENTS, V_FILENAME);
END;

--글 번호로 한 개의 게시글 가져오기
CREATE OR REPLACE PROCEDURE sp_board_select_one
(
    V_idx          IN      BOARD.idx%TYPE,
    board_RECORD      OUT     SYS_REFCURSOR
)
AS
BEGIN
    OPEN board_RECORD FOR
    SELECT idx, name, email,  TITLE, CONTENTs, writedate, readcount, filename
    FROM BOARD
    WHERE idx = V_idx;
    
    --조회수 증가
    update board set readcount = readcount + 1
    where idx = v_idx;
END;