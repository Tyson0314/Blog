package com.zzx.service;

import com.zzx.dao.LogDao;
import com.zzx.model.pojo.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Tyson
 * @time: 2020-07-24 22:30
 */
@Service
public class LogService {
    @Autowired
    LogDao logDao;

    public void saveLog(Log log) {
        logDao.saveLog(log);
    }
}