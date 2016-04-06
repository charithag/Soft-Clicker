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

INSERT INTO `USER` (`FIRST_NAME`, `LAST_NAME`, `USER_NAME`, `PASSWORD`, `ROLES`) VALUES (
  'admin',
  'admin',
  'admin',
  'admin',
  'ADMIN'
);