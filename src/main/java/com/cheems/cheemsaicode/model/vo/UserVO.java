package com.cheems.cheemsaicode.model.vo;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类。
 *
 * @author cheems
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户账号
     */
    @Column("userAccount")
    private String userAccount;


    /**
     * 用户名
     */
    @Column("userName")
    private String userName;

    /**
     * 用户头像
     */
    @Column("userAvatar")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Column("userProfile")
    private String userProfile;

    /**
     * 用户角色
     */
    @Column("userRole")
    private String userRole;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;


}
