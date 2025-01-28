package com.evan.demo.security.system.user.dao;

import com.evan.demo.security.system.user.pojo.entity.DevUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevUserRepository extends CrudRepository<DevUser, Integer> {
    DevUser findByUserName(String username);
    DevUser findByUserCode(String userCode);
}
