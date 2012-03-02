CREATE TABLE IF NOT EXISTS `CONTENT` (
  `ID` bigint(20) NOT NULL auto_increment,
  `CONTENT_ANCESTOR_REF` bigint(20),
  `VERSION` bigint(10) NOT NULL,
  `TITLE` varchar(64) NOT NULL,
  `STATUS` INT(2) NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CONTENT_TYPE` INT(2) NOT NULL,
  `AUTHOR_REF` bigint(20) NOT NULL,
  `FILE_URI` varchar(255),
  `TAGS` varchar(255),
  `POPULARITY` bigint(20) NOT NULL default 0,
  `CREATION_DATE` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `LAST_MODIFICATION_DATE` timestamp NULL default NULL,
  `LAST_CONSULTATION_DATE` timestamp NULL default NULL,
  `PUBLICATION_START_DATE` timestamp NULL default NULL,
  `PUBLICATION_END_DATE` timestamp NULL default NULL,
  PRIMARY KEY (`ID`)
);