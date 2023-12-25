package com.evan.demo.security.system.dao;

import com.evan.demo.security.system.pojo.entity.DevMenuInterface;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevMenuInterfaceRepositories extends CrudRepository<DevMenuInterface, Integer> {
}
