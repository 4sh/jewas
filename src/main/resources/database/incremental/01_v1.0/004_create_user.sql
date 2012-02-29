CREATE TABLE IF NOT EXISTS `USER` (
  `ID` bigint(20) NOT NULL auto_increment,
  `LOGIN` varchar(32) NOT NULL,
  `PASSWORD` varchar(10) NOT NULL,
  `NAME` varchar(64) NOT NULL,
  `SURNAME` varchar(64) NOT NULL,
  `EMAIL` varchar(64) NOT NULL,
  `ROLE_REF` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
);