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