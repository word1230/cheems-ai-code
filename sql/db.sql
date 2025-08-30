CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
                        `userAccount` varchar(255) CHARACTER SET utf8mb3 NOT NULL COMMENT '用户账号',
                        `userPassword` varchar(255) CHARACTER SET utf8mb3 NOT NULL COMMENT '用户密码',
                        `userName` varchar(255) CHARACTER SET utf8mb3 DEFAULT NULL COMMENT '用户名',
                        `userAvatar` varchar(1024) CHARACTER SET utf8mb3 DEFAULT NULL COMMENT '用户头像',
                        `userProfile` varchar(512) CHARACTER SET utf8mb3 DEFAULT NULL COMMENT '用户简介',
                        `userRole` varchar(255) CHARACTER SET utf8mb3 DEFAULT 'user' COMMENT '用户角色',
                        `editTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
                        `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                        `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除  1删除  ',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_userAccount` (`userAccount`),
                        KEY `idx_userName` (`userName`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

