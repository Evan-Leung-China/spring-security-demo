package com.evan.demo.security.system.user.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "dev_user_role")
@Entity
@Data
public class DevUserRole {
    @Id
    @GeneratedValue
    private Integer id;
    @Column
    private Integer userId;
    @Column
    private Integer roleId;
    
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
