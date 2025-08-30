package com.cheems.cheemsaicode.model.dto.user;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

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
public class UserUpdateRequest implements Serializable {

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


}
