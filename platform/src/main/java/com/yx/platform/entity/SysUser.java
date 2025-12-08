package com.yx.platform.entity;

import java.time.LocalDateTime;

public class SysUser {

    /** 用户ID (对应数据库的 BIGINT) */
    private Long id;

    /** 用户名 (对应 VARCHAR) */
    private String username;

    /** 密码 (对应 VARCHAR) */
    private String password;

    /** 角色 (对应 VARCHAR) */
    private String role;

    /** 创建时间 (对应 DATETIME) */
    private LocalDateTime createTime;

    // ==========================================
    // 下面是 Getter 和 Setter 方法
    // 这些方法是用来让外部读取和设置这些私有属性的
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // toString 方法，方便以后打印日志看数据
    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
