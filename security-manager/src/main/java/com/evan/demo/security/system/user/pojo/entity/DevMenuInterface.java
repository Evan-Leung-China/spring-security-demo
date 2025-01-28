package com.evan.demo.security.system.user.pojo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dev_menu_interface")
public class DevMenuInterface {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer menuId;

    private String uri;

    private String description;
}
