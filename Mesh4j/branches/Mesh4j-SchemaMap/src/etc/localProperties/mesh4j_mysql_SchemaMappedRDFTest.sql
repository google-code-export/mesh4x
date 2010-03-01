CREATE DATABASE IF NOT EXISTS `mesh4xdb` /*!40100 DEFAULT CHARACTER SET latin1 */;

DROP TABLE IF EXISTS `mesh4xdb`.`user2`;
CREATE TABLE  `mesh4xdb`.`user2` (
  `id` varchar(20) NOT NULL,
  `name` varchar(100) default NULL,
  `pass` varchar(100) default NULL,
  `phone` bigint(20) default NULL,
  `balance` double default NULL,
  PRIMARY KEY  USING BTREE (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `mesh4xdb`.`user2_sync`;
CREATE TABLE  `mesh4xdb`.`user2_sync` (
  `sync_id` varchar(255) NOT NULL,
  `entity_name` varchar(255) default NULL,
  `entity_id` varchar(255) default NULL,
  `entity_version` varchar(255) default NULL,
  `sync_data` text,
  PRIMARY KEY  (`sync_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;