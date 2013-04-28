ALTER TABLE  `escalation_level` ADD  `channel` VARCHAR( 50 ) NULL ,
ADD  `project_code` VARCHAR( 10 ) NULL;

ALTER TABLE  `failure_escalation`
ADD  `issue_key` VARCHAR( 20 ) NULL;