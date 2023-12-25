package com.evan.demo.security.system.pojo.entity;

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
    @Column
    private String menuUri;
    @Column
    private String menuIcon;
    @Column
    private String menuType;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
