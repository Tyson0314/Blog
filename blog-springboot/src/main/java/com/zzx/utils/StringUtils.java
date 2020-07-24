package com.zzx.utils;

import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @description:
 * @author: Tyson
 * @time: 2020-07-25 00:07
 */
public class StringUtils {
    /**
     * 根据ip获取详细地址
     */
    public static String getCityInfo(String ip) {
        DbSearcher searcher = null;
        try {
            String path = "ip2region/ip2region.db";
            String name = "ip2region.db";
            DbConfig config = new DbConfig();
            File file = FileUtil.inputStreamToFile(new ClassPathResource(path).getInputStream(), name);
            searcher = new DbSearcher(config, file.getPath());
            Method method;
            method = searcher.getClass().getMethod("btreeSearch", String.class);
            DataBlock dataBlock;
            dataBlock = (DataBlock) method.invoke(searcher, ip);
            String address = dataBlock.getRegion().replace("0|","");
            char symbol = '|';
            if(address.charAt(address.length()-1) == symbol){
                address = address.substring(0,address.length() - 1);
            }
            return address.equals(BlogConstant.REGION)?"内网IP":address;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(searcher!=null){
                try {
                    searcher.close();
                } catch (IOException ignored) {
                }
            }

        }
        return "";
    }
}