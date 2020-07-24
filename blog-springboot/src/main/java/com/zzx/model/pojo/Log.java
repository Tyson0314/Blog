package com.zzx.model.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @description:
 * @author: Tyson
 * @time: 2020-07-24 22:50
 */
@Data
@ToString
public class Log {
    private Long id;
    private String username;
    private String method;
    private String params;
    private String ip;
    private String address;
    private Date createTime;
    private Long time;
}