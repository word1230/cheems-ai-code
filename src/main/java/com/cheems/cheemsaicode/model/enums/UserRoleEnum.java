package com.cheems.cheemsaicode.model.enums;

import cn.hutool.core.util.ObjUtil;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 拿到更高权限的角色
     * @param mustRoleEnum
     * @return
     */
    public boolean hasPermission(UserRoleEnum mustRoleEnum) {
        ThrowUtils.throwIf(mustRoleEnum == null, ErrorCode.PARAMS_ERROR);
        return this.ordinal() >= mustRoleEnum.ordinal();
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
