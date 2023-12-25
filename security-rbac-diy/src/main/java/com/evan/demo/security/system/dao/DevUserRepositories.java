package com.evan.demo.security.system.dao;

import com.evan.demo.security.system.pojo.entity.DevUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevUserRepositories extends CrudRepository<DevUser, Integer> {
    DevUser findByUserName(String username);
    DevUser findByUserCode(String userCode);
}
