package com.evan.demo.security.system.pojo.entity;

import lombok.Data;

import jakarta.persistence.*;

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
