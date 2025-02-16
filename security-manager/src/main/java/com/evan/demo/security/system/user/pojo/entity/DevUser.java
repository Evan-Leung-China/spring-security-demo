package com.evan.demo.security.system.user.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "dev_user")
@Entity
@Data
public class DevUser {
    @Id
    @GeneratedValue
    private Integer id;
    @Column
    private String userCode;
    @Column
    private String userName;
    @Column
    private String password;
    @Column(name = "is_lock")
    private Boolean lock;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
