-- -----------------------------------------------------
-- Table `USER`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `USER` (
  `USER_ID` INTEGER NOT NULL,
  `FIRST_NAME` VARCHAR(255) NOT NULL,
  `LAST_NAME` VARCHAR(255) NOT NULL,
  `USER_NAME` VARCHAR(100) NOT NULL,
  `PASSWORD` CHAR(64) NOT NULL,
  `ROLES` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`USER_ID`) );
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `QUESTION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QUESTION` (
  `QUESTION_ID`  INTEGER AUTO_INCREMENT,
  `QUESTION`     VARCHAR(255) NOT NULL,
  `ANSWER`       VARCHAR(255) NOT NULL,
  `OWNER_ID`     INTEGER      NOT NULL,
  `CREATED_TIME` VARCHAR(100) NOT NULL,
  `EXPIRE_TIME`  CHAR(64)     NOT NULL,
  PRIMARY KEY (`QUESTION_ID`)
);
ENGINE = InnoDB;

INSERT INTO `USER` (`FIRST_NAME`, `LAST_NAME`, `USER_NAME`, `PASSWORD`, `ROLES`) VALUES (
  'admin',
  'admin',
  'admin',
  'admin',
  'ADMIN'
);