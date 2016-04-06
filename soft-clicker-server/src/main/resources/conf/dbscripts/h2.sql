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

INSERT INTO `USER` (`FIRST_NAME`, `LAST_NAME`, `USER_NAME`, `PASSWORD`, `ROLES`) VALUES (
  'admin',
  'admin',
  'admin',
  'admin',
  'ADMIN'
);