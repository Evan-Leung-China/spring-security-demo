package com.evan.demo.security.system.user.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "dev_menu")
@Data
public class DevMenu {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String menuCode;
    @Column
    private String menuName;
    @Column
    private Integer parent;
    /**
     * 菜单uri
     */
    @Column
    private String menuUri;
    /**
     * 菜单图标 icon_directory:目录 2 菜单 3 按钮'
     */
    @Column
    private String menuIcon;
    /**
     * 菜单类型 1 目录 2 菜单 3 按钮'
     */
    @Column
    private String menuType;
    /**
     * 菜单权限
     */
    @Column
    private String permission;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
