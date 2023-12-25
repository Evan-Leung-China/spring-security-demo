package com.evan.demo.security.system.dao;

import com.evan.demo.security.system.pojo.entity.DevMenu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevMenuRepositories extends CrudRepository<DevMenu, Integer> {
}
