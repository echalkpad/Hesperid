-- phpMyAdmin SQL Dump
-- version 3.3.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 21. September 2011 um 11:58
-- Server Version: 5.1.54
-- PHP-Version: 5.3.5-1ubuntu7.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Datenbank: `molo`
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
  `description` varchar(255) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `last_tick_received` datetime DEFAULT NULL,
  `last_updated_observer` datetime DEFAULT NULL,
  `managed` bit(1) NOT NULL,
  `purchasing` date DEFAULT NULL,
  `room_number` varchar(255) DEFAULT NULL,
  `escalation_scheme` bigint(20) DEFAULT NULL,
  `location` bigint(20) DEFAULT NULL,
  `system` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `asset_identifier` (`asset_identifier`),
  KEY `FK58CEAF0B18421F8` (`escalation_scheme`),
  KEY `FK58CEAF042BA7139` (`location`),
  KEY `FK58CEAF02BC43DED` (`system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `asset`
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
  KEY `FKCCEC7EC143540022` (`first_asset`),
  KEY `FKCCEC7EC156ED0EE6` (`second_asset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `client_hierarchy`
--


-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `connection_credential`
--

CREATE TABLE IF NOT EXISTS `connection_credential` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection_credential_protocol` int(11) DEFAULT NULL,
  `last_successful_connection` datetime DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  `asset` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5F50685873058E71` (`asset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `connection_credential`
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
  KEY `FK38B7242042BA7139` (`location`)
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
  `contact` bigint(20) DEFAULT NULL,
  `escalation_scheme` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKAD47E55AB18421F8` (`escalation_scheme`),
  KEY `FKAD47E55A8D4E2091` (`contact`)
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
  KEY `FKBF3C318A32EA604` (`observer_parameter`),
  KEY `FKBF3C318A73058E71` (`asset`),
  KEY `FKBF3C318AD38F29A` (`escalation_level`),
  KEY `FKBF3C318A89A9D77B` (`observer`)
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
  KEY `FKFB6D946A9A583B65` (`failure`),
  KEY `FKFB6D946AD38F29A` (`escalation_level`)
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
-- Tabellenstruktur für Tabelle `mes_role`
--

CREATE TABLE IF NOT EXISTS `mes_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Daten für Tabelle `mes_role`
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
  KEY `FK14C752D6415269F4` (`observer_strategy`),
  KEY `FK14C752D673058E71` (`asset`)
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
  KEY `FKDC35ED8089A9D77B` (`observer`)
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
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_roles`
--

CREATE TABLE IF NOT EXISTS `user_roles` (
  `user` bigint(20) NOT NULL,
  `roles` bigint(20) NOT NULL,
  PRIMARY KEY (`user`,`roles`),
  KEY `FK73429949AB93A7C8` (`roles`),
  KEY `FK73429949A551702B` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `asset`
--
ALTER TABLE `asset`
  ADD CONSTRAINT `FK58CEAF02BC43DED` FOREIGN KEY (`system`) REFERENCES `system` (`id`),
  ADD CONSTRAINT `FK58CEAF042BA7139` FOREIGN KEY (`location`) REFERENCES `location` (`id`),
  ADD CONSTRAINT `FK58CEAF0B18421F8` FOREIGN KEY (`escalation_scheme`) REFERENCES `escalation_scheme` (`id`);

--
-- Constraints der Tabelle `client_hierarchy`
--
ALTER TABLE `client_hierarchy`
  ADD CONSTRAINT `FKCCEC7EC156ED0EE6` FOREIGN KEY (`second_asset`) REFERENCES `asset` (`id`),
  ADD CONSTRAINT `FKCCEC7EC143540022` FOREIGN KEY (`first_asset`) REFERENCES `asset` (`id`);

--
-- Constraints der Tabelle `connection_credential`
--
ALTER TABLE `connection_credential`
  ADD CONSTRAINT `FK5F50685873058E71` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`);

--
-- Constraints der Tabelle `contact`
--
ALTER TABLE `contact`
  ADD CONSTRAINT `FK38B7242042BA7139` FOREIGN KEY (`location`) REFERENCES `location` (`id`);

--
-- Constraints der Tabelle `escalation_level`
--
ALTER TABLE `escalation_level`
  ADD CONSTRAINT `FKAD47E55A8D4E2091` FOREIGN KEY (`contact`) REFERENCES `contact` (`id`),
  ADD CONSTRAINT `FKAD47E55AB18421F8` FOREIGN KEY (`escalation_scheme`) REFERENCES `escalation_scheme` (`id`);

--
-- Constraints der Tabelle `failure`
--
ALTER TABLE `failure`
  ADD CONSTRAINT `FKBF3C318A89A9D77B` FOREIGN KEY (`observer`) REFERENCES `observer` (`id`),
  ADD CONSTRAINT `FKBF3C318A32EA604` FOREIGN KEY (`observer_parameter`) REFERENCES `observer_parameter` (`id`),
  ADD CONSTRAINT `FKBF3C318A73058E71` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  ADD CONSTRAINT `FKBF3C318AD38F29A` FOREIGN KEY (`escalation_level`) REFERENCES `escalation_level` (`id`);

--
-- Constraints der Tabelle `failure_escalation`
--
ALTER TABLE `failure_escalation`
  ADD CONSTRAINT `FKFB6D946AD38F29A` FOREIGN KEY (`escalation_level`) REFERENCES `escalation_level` (`id`),
  ADD CONSTRAINT `FKFB6D946A9A583B65` FOREIGN KEY (`failure`) REFERENCES `failure` (`id`);

--
-- Constraints der Tabelle `observer`
--
ALTER TABLE `observer`
  ADD CONSTRAINT `FK14C752D673058E71` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`),
  ADD CONSTRAINT `FK14C752D6415269F4` FOREIGN KEY (`observer_strategy`) REFERENCES `observer_strategy` (`id`);

--
-- Constraints der Tabelle `observer_parameter`
--
ALTER TABLE `observer_parameter`
  ADD CONSTRAINT `FKDC35ED8089A9D77B` FOREIGN KEY (`observer`) REFERENCES `observer` (`id`);

--
-- Constraints der Tabelle `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `FK73429949A551702B` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FK73429949AB93A7C8` FOREIGN KEY (`roles`) REFERENCES `role` (`id`);
