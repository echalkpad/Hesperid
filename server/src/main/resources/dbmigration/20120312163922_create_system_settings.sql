CREATE TABLE IF NOT EXISTS `system_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_deletion_barrier` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- set 15 days as data deletion barrier
insert into system_settings (id, data_deletion_barrier) values (1, 1296000);