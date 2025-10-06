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


-- 应用表
create table app
(
    id           bigint auto_increment comment 'id' primary key,
    appName      varchar(256)                       null comment '应用名称',
    cover        varchar(512)                       null comment '应用封面',
    initPrompt   text                               null comment '应用初始化的 prompt',
    codeGenType  varchar(64)                        null comment '代码生成类型（枚举）',
    deployKey    varchar(64)                        null comment '部署标识',
    deployedTime datetime                           null comment '部署时间',
    priority     int      default 0                 not null comment '优先级',
    userId       bigint                             not null comment '创建用户id',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_deployKey (deployKey), -- 确保部署标识唯一
    INDEX idx_appName (appName),         -- 提升基于应用名称的查询性能
    INDEX idx_userId (userId)            -- 提升基于用户 ID 的查询性能
) comment '应用' collate = utf8mb4_unicode_ci;
