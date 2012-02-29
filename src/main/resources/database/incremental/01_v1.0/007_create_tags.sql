CREATE TABLE IF NOT EXISTS `TAGS` (
  `TAG` varchar(32) NOT NULL,
  `WEIGHT` bigint(10) NOT NULL default 0,
  PRIMARY KEY (`TAG`),
  INDEX TAG_INDEX (`TAG`)
);