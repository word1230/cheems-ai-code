-- 用户表
CREATE TABLE "user" (
                        id BIGSERIAL NOT NULL PRIMARY KEY,
                        user_account VARCHAR(255) NOT NULL,
                        user_password VARCHAR(255) NOT NULL,
                        user_name VARCHAR(255) DEFAULT NULL,
                        user_avatar VARCHAR(1024) DEFAULT NULL,
                        user_profile VARCHAR(512) DEFAULT NULL,
                        user_role VARCHAR(255) DEFAULT 'user',
                        edit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        is_delete SMALLINT NOT NULL DEFAULT 0
);

-- 应用表
CREATE TABLE app (
                     id BIGSERIAL PRIMARY KEY,
                     app_name VARCHAR(256) NULL,
                     cover VARCHAR(512) NULL,
                     init_prompt TEXT NULL,
                     code_gen_type VARCHAR(64) NULL,
                     deploy_key VARCHAR(64) NULL,
                     deployed_time TIMESTAMP NULL,
                     priority INTEGER NOT NULL DEFAULT 0,
                     user_id BIGINT NOT NULL,
                     edit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     is_delete SMALLINT NOT NULL DEFAULT 0
);

-- 对话历史表
CREATE TABLE chat_history (
                              id BIGSERIAL PRIMARY KEY,
                              message TEXT NOT NULL,
                              message_type VARCHAR(32) NOT NULL,
                              app_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              is_delete SMALLINT NOT NULL DEFAULT 0
);

-- 创建约束
ALTER TABLE "user" ADD CONSTRAINT uk_user_account UNIQUE (user_account);

ALTER TABLE app ADD CONSTRAINT uk_deploy_key UNIQUE (deploy_key);

-- 创建索引
CREATE INDEX idx_user_name ON "user" (user_name);

CREATE INDEX idx_app_name ON app (app_name);
CREATE INDEX idx_user_id ON app (user_id);

CREATE INDEX idx_app_id ON chat_history (app_id);
CREATE INDEX idx_create_time ON chat_history (create_time);
CREATE INDEX idx_app_id_create_time ON chat_history (app_id, create_time);

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为 user 表创建更新时间触发器
CREATE TRIGGER update_user_updated_at
    BEFORE UPDATE ON "user"
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 为 app 表创建更新时间触发器
CREATE TRIGGER update_app_updated_at
    BEFORE UPDATE ON app
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 为 chat_history 表创建更新时间触发器
CREATE TRIGGER update_chat_history_updated_at
    BEFORE UPDATE ON chat_history
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
