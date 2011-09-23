INSERT INTO `role` (`id`, `name`) VALUES ('1', 'ROLE_ADMIN');
INSERT INTO `user` (`id`, `enabled`, `password`, `username`) VALUES ('1', b'1', '3f0149558bb3967a270a5014507f1911399b283f', 'admin');
INSERT INTO user_roles VALUES (1,1);