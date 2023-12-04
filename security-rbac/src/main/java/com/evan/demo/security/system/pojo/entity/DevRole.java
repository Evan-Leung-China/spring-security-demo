package com.evan.demo.security.system.pojo.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dev_role")
@Data
public class DevRole {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String roleCode;
    @Column
    private String roleName;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
