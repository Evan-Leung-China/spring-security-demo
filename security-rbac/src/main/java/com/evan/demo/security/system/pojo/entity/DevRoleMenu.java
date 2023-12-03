package com.evan.demo.security.system.pojo.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "dev_role_menu")
@Entity
@Data
public class DevRoleMenu {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private Integer roleId;
    @Column
    private Integer menuId;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
