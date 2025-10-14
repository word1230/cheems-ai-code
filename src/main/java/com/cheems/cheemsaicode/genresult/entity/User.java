package com.cheems.cheemsaicode.genresult.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.io.Serializable;
import java.sql.Timestamp;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author cheems
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator,value = KeyGenerators.snowFlakeId)
    private Long id;

    private String userAccount;

    private String userPassword;

    private String userName;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private Timestamp editTime;

    private Timestamp createTime;

    private Timestamp updateTime;

    @Column(isLogicDelete = true)
    private Integer isDelete;

}
