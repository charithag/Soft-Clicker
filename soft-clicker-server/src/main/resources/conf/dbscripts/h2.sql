-- -----------------------------------------------------
-- Table `USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `USER` (
  `USER_ID`    INTEGER AUTO_INCREMENT,
  `FIRST_NAME` VARCHAR(255) NOT NULL,
  `LAST_NAME`  VARCHAR(255) NOT NULL,
  `USER_NAME`  VARCHAR(100) NOT NULL UNIQUE,
  `PASSWORD`   CHAR(64)     NOT NULL,
  `ROLES`      VARCHAR(255) NOT NULL,
  PRIMARY KEY (`USER_ID`)
);

-- -----------------------------------------------------
-- Table `QUESTION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QUESTION` (
  `QUESTION_ID`    INTEGER AUTO_INCREMENT,
  `QUESTION`       VARCHAR(255) NOT NULL,
  `CORRECT_ANSWER` VARCHAR(255) NOT NULL,
  `OWNER_ID`       INTEGER      NOT NULL,
  `CREATED_TIME`   TIMESTAMP    NOT NULL,
  `EXPIRE_TIME`    TIMESTAMP    NOT NULL,
  PRIMARY KEY (`QUESTION_ID`)
);

-- -----------------------------------------------------
-- Table `ANSWER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ANSWER` (
  `ANSWER_ID`     INTEGER AUTO_INCREMENT,
  `ANSWER`        VARCHAR(255) NOT NULL,
  `QUESTION_ID`   INTEGER      NOT NULL,
  `OWNER_ID`      INTEGER      NOT NULL,
  `ANSWERED_TIME` TIMESTAMP    NOT NULL,
  PRIMARY KEY (`ANSWER_ID`)
);

INSERT INTO `USER` (`USER_ID`, `FIRST_NAME`, `LAST_NAME`, `USER_NAME`, `PASSWORD`, `ROLES`) VALUES
  (
    1,
    'SoftClicker',
    'Administrator',
    'admin',
    'admin',
    'ADMIN'
  ),
  (
    2,
    'Michael',
    'Jones',
    'jones',
    'jones123',
    'TEACHER'
  ),
  (
    3,
    'Craig',
    'Coleman',
    'craig',
    'craig123',
    'TEACHER'
  ), (
    4,
    'Eric',
    'Hill',
    'eric',
    'eric123',
    'STUDENT'
  ), (
    5,
    'Walter',
    'Phillips',
    'walter',
    'walter123',
    'STUDENT'
  );

INSERT INTO `QUESTION` (`QUESTION_ID`, `QUESTION`, `CORRECT_ANSWER`, `OWNER_ID`, `CREATED_TIME`, `EXPIRE_TIME`)
VALUES
  (
    1,
    'What is 20+20?',
    '40',
    2,
    '2016-04-06 18:00:10',
    '2016-04-06 18:10:00'
  ),
  (
    2,
    'What is 15/5',
    '3',
    2,
    '2016-04-06 18:15:10',
    '2016-04-06 18:20:00'
  );

INSERT INTO `ANSWER` (`ANSWER_ID`, `ANSWER`, `QUESTION_ID`, `OWNER_ID`, `ANSWERED_TIME`) VALUES
  (
    1,
    '40',
    1,
    4,
    '2016-04-06 18:05:24'
  ),
  (
    2,
    '42',
    1,
    5,
    '2016-04-06 18:06:44'
  );