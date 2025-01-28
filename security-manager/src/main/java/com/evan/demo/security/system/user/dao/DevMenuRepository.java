package com.evan.demo.security.system.user.dao;

import com.evan.demo.security.system.user.pojo.entity.DevMenu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevMenuRepository extends CrudRepository<DevMenu, Integer> {
}
