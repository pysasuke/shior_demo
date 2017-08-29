

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_permission`
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `permissionname` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('1', '1', 'user:create');
INSERT INTO `t_permission` VALUES ('2', '2', 'user:update');
INSERT INTO `t_permission` VALUES ('3', '1', 'user:update');

-- ----------------------------
-- Table structure for `t_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1', 'teacher');
INSERT INTO `t_role` VALUES ('2', 'student');

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(20) NOT NULL,
  `password` varchar(50) NOT NULL,
  `createTime` date DEFAULT NULL,
  `lastTime` datetime DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `sex` enum('male','female') DEFAULT 'male',
  `salt` varchar(50) DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', 'admin', '86c4604b628d4e91f5f2a2fed3f88430', '2017-08-28', null, '404158848@qq.com', 'male', '26753209835f4c837066d1cc7d9b46aa', '1');
INSERT INTO `t_user` VALUES ('2', 'test', 'a038892c7b638aad0357adb52cabfb29', '2017-08-28', null, '404158848@qq.com', 'male', '6ced07d939407fb0449d92d9f17cfcd1', '2');
INSERT INTO `t_user` VALUES ('3', 'test1', '4be958cccb89213221888f9ffca6969b', '2017-08-28', null, '404158848@qq.com', 'male', 'c95a278e52daf5166b1ffd6436cde7b7', '1');
