package com.zzx.dao;

import com.zzx.model.pojo.Log;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: Tyson
 * @time: 2020-07-24 22:46
 */
@Repository
@Mapper
public interface LogDao {
    void saveLog(Log log);
}