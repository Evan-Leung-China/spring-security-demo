package com.evan.demo.security.system.pojo.entity;

import lombok.Data;

import jakarta.persistence.*;
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
