-- phpMyAdmin SQL Dump
-- version 3.3.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 04. Oktober 2011 um 13:59
-- Server Version: 5.1.54
-- PHP-Version: 5.3.5-1ubuntu7.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Datenbank: `hesperid`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `agent_bundle`
--

CREATE TABLE IF NOT EXISTS `agent_bundle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `published` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `agent_bundle`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `asset`
--

CREATE TABLE IF NOT EXISTS `asset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `asset_identifier` varchar(255) DEFAULT NULL,
  `asset_name` varchar(255) DEFAULT NULL,
  `care_pack` varchar(255) DEFAULT NULL,
  `cost_per_year` decimal(19,2) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `last_tick_received` datetime DEFAULT NULL,
  `last_updated_observer` datetime DEFAULT NULL,
  `managed` bit(1) NOT NULL,
  `purchased` date DEFAULT NULL,
  `room_number` varchar(255) DEFAULT NULL,
  `escalation_scheme` bigint(20) DEFAULT NULL,
  `location` bigint(20) DEFAULT NULL,
  `system` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `asset_identifier` (`asset_identifier`),
  KEY `FK58CEAF0386B28C0` (`escalation_scheme`),
  KEY `FK58CEAF09858C001` (`location`),
  KEY `FK58CEAF0F0B79EB5` (`system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `asset`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `asset_contact`
--

CREATE TABLE IF NOT EXISTS `asset_contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `asset` bigint(20) DEFAULT NULL,
  `business_role` bigint(20) DEFAULT NULL,
  `contact` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC71C5D1F53EF4A9` (`asset`),
  KEY `FKC71C5D166C6D8C9` (`contact`),
  KEY `FKC71C5D142BC8302` (`business_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `asset_contact`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `asset_software_license`
--

CREATE TABLE IF NOT EXISTS `asset_software_license` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expiration_date` date DEFAULT NULL,
  `license_key` longtext,
  `remark` longtext,
  `version` varchar(255) DEFAULT NULL,
  `asset` bigint(20) DEFAULT NULL,
  `software_license` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKD48AB18F53EF4A9` (`asset`),
  KEY `FKD48AB183449CDCC` (`software_license`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `asset_software_license`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `business_role`
--

CREATE TABLE IF NOT EXISTS `business_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `business_role`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `client_hierarchy`
--

CREATE TABLE IF NOT EXISTS `client_hierarchy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_relation_type` int(11) DEFAULT NULL,
  `first_asset` bigint(20) DEFAULT NULL,
  `second_asset` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKCCEC7EC1C58D665A` (`first_asset`),
  KEY `FKCCEC7EC1D926751E` (`second_asset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `client_hierarchy`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `contact`
--

CREATE TABLE IF NOT EXISTS `contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `pager` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `location` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK38B724209858C001` (`location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `contact`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `escalation_level`
--

CREATE TABLE IF NOT EXISTS `escalation_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(11) NOT NULL,
  `timeout` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `escalation_scheme` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKAD47E55A386B28C0` (`escalation_scheme`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `escalation_level`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `escalation_scheme`
--

CREATE TABLE IF NOT EXISTS `escalation_scheme` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `escalation_scheme`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `failure`
--

CREATE TABLE IF NOT EXISTS `failure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `acknowledged` datetime DEFAULT NULL,
  `detected` datetime DEFAULT NULL,
  `escalated` datetime DEFAULT NULL,
  `failure_status` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `resolved` datetime DEFAULT NULL,
  `asset` bigint(20) DEFAULT NULL,
  `escalation_level` bigint(20) DEFAULT NULL,
  `observer` bigint(20) DEFAULT NULL,
  `observer_parameter` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKBF3C318A5928783C` (`observer_parameter`),
  KEY `FKBF3C318AF53EF4A9` (`asset`),
  KEY `FKBF3C318A8D71F2D2` (`escalation_level`),
  KEY `FKBF3C318ADF482643` (`observer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `failure`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `failure_escalation`
--

CREATE TABLE IF NOT EXISTS `failure_escalation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `escalated` datetime DEFAULT NULL,
  `escalation_level` bigint(20) DEFAULT NULL,
  `failure` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKFB6D946A73D0F39D` (`failure`),
  KEY `FKFB6D946A8D71F2D2` (`escalation_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `failure_escalation`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `location`
--

CREATE TABLE IF NOT EXISTS `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `location_code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `location`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `mail_server`
--

CREATE TABLE IF NOT EXISTS `mail_server` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `default_sender` varchar(255) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `mail_server_secure_connection_type` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `port` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `mail_server`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `observer`
--

CREATE TABLE IF NOT EXISTS `observer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_interval` bigint(20) DEFAULT NULL,
  `expected_value` varchar(255) DEFAULT NULL,
  `expected_value_max` float DEFAULT NULL,
  `expected_value_min` float DEFAULT NULL,
  `last_check` datetime DEFAULT NULL,
  `monitor` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parameters` longtext,
  `show_on_asset_overview` bit(1) NOT NULL,
  `asset` bigint(20) DEFAULT NULL,
  `observer_strategy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK14C752D6C83970BC` (`observer_strategy`),
  KEY `FK14C752D6F53EF4A9` (`asset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `observer`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `observer_parameter`
--

CREATE TABLE IF NOT EXISTS `observer_parameter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `value` longtext,
  `observer` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKDC35ED80DF482643` (`observer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `observer_parameter`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `observer_strategy`
--

CREATE TABLE IF NOT EXISTS `observer_strategy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `groovy_script` longtext,
  `name` varchar(255) DEFAULT NULL,
  `observation_scope` varchar(255) DEFAULT NULL,
  `possible_parameters` varchar(255) DEFAULT NULL,
  `result_parameter_name` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `observer_strategy`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `report_type`
--

CREATE TABLE IF NOT EXISTS `report_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hql_query` longtext,
  `jasper_xml_code` longtext,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `report_type`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `role`
--

CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;



-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `schema_version`
--

CREATE TABLE IF NOT EXISTS `schema_version` (
  `version` varchar(32) NOT NULL,
  `applied_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `duration` int(11) NOT NULL,
  UNIQUE KEY `version` (`version`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `software_license`
--

CREATE TABLE IF NOT EXISTS `software_license` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `software_license`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `system`
--

CREATE TABLE IF NOT EXISTS `system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `system`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `system_health`
--

CREATE TABLE IF NOT EXISTS `system_health` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `log` longtext,
  `log_date` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `system_health`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Daten für Tabelle `user`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_roles`
--

CREATE TABLE IF NOT EXISTS `user_roles` (
  `user` bigint(20) NOT NULL,
  `roles` bigint(20) NOT NULL,
  PRIMARY KEY (`user`,`roles`),
  KEY `FK7342994975F89A90` (`roles`),
  KEY `FK734299496FB662F3` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `asset`
--
ALTER TABLE `asset`
  ADD CONSTRAINT `FK58CEAF0F0B79EB5` FOREIGN KEY (`system`) REFERENCES `system` (`id`),
  ADD CONSTRAINT `FK58CEAF0386B28C0` FOREIGN KEY (`escalation_scheme`) REFERENCES `escalation_scheme` (`id`),
  ADD CONSTRAINT `FK58CEAF09858C001` FOREIGN KEY (`location`) REFERENCES `location` (`id`);

--
-- Constraints der Tabelle `asset_contact`
--
ALTER TABLE `asset_contact`
  ADD CONSTRAINT `FKC71C5D142BC8302` FOREIGN KEY (`business_role`) REFERENCES `business_role` (`id`),
  ADD CONSTRAINT `FKC71C5D166C6D8C9` FOREIGN KEY (`contact`) REFERENCES `contact` (`id`),
  ADD CONSTRAINT `FKC71C5D1F53EF4A9` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`);

--
-- Constraints der Tabelle `asset_software_license`
--
ALTER TABLE `asset_software_license`
  ADD CONSTRAINT `FKD48AB183449CDCC` FOREIGN KEY (`software_license`) REFERENCES `software_license` (`id`),
  ADD CONSTRAINT `FKD48AB18F53EF4A9` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`);

--
-- Constraints der Tabelle `client_hierarchy`
--
ALTER TABLE `client_hierarchy`
  ADD CONSTRAINT `FKCCEC7EC1D926751E` FOREIGN KEY (`second_asset`) REFERENCES `asset` (`id`),
  ADD CONSTRAINT `FKCCEC7EC1C58D665A` FOREIGN KEY (`first_asset`) REFERENCES `asset` (`id`);

--
-- Constraints der Tabelle `contact`
--
ALTER TABLE `contact`
  ADD CONSTRAINT `FK38B724209858C001` FOREIGN KEY (`location`) REFERENCES `location` (`id`);

--
-- Constraints der Tabelle `escalation_level`
--
ALTER TABLE `escalation_level`
  ADD CONSTRAINT `FKAD47E55A386B28C0` FOREIGN KEY (`escalation_scheme`) REFERENCES `escalation_scheme` (`id`);

--
-- Constraints der Tabelle `failure`
--
ALTER TABLE `failure`
  ADD CONSTRAINT `FKBF3C318ADF482643` FOREIGN KEY (`observer`) REFERENCES `observer` (`id`),
  ADD CONSTRAINT `FKBF3C318A5928783C` FOREIGN KEY (`observer_parameter`) REFERENCES `observer_parameter` (`id`),
  ADD CONSTRAINT `FKBF3C318A8D71F2D2` FOREIGN KEY (`escalation_level`) REFERENCES `escalation_level` (`id`),
  ADD CONSTRAINT `FKBF3C318AF53EF4A9` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`);

--
-- Constraints der Tabelle `failure_escalation`
--
ALTER TABLE `failure_escalation`
  ADD CONSTRAINT `FKFB6D946A8D71F2D2` FOREIGN KEY (`escalation_level`) REFERENCES `escalation_level` (`id`),
  ADD CONSTRAINT `FKFB6D946A73D0F39D` FOREIGN KEY (`failure`) REFERENCES `failure` (`id`);

--
-- Constraints der Tabelle `observer`
--
ALTER TABLE `observer`
  ADD CONSTRAINT `FK14C752D6F53EF4A9` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  ADD CONSTRAINT `FK14C752D6C83970BC` FOREIGN KEY (`observer_strategy`) REFERENCES `observer_strategy` (`id`);

--
-- Constraints der Tabelle `observer_parameter`
--
ALTER TABLE `observer_parameter`
  ADD CONSTRAINT `FKDC35ED80DF482643` FOREIGN KEY (`observer`) REFERENCES `observer` (`id`);

--
-- Constraints der Tabelle `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `FK734299496FB662F3` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FK7342994975F89A90` FOREIGN KEY (`roles`) REFERENCES `role` (`id`);
