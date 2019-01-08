package com.auto.util

import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GroovyJDBC {

    public static  Logger logger = LoggerFactory.getLogger(GroovyJDBC.class);
    Sql sql;

    GroovyJDBC(String url,String username, String password){
        String driverClassName= "com.mysql.jdbc.Driver";
        this.sql = Sql.newInstance(url, username,password, driverClassName)
    }

    def  excute(String sqlStr, List params=[]){
        logger.info(sqlStr)
        if (params){
            return sql.execute(sqlStr, params)
        }
        return sql.execute(sqlStr)
    }

    def  selectOne(String sqlStr, List params=[]){
        logger.info(sqlStr)
        if(params){
            return sql.firstRow(sqlStr, params)
        }
        return sql.firstRow(sqlStr)
    }


    def  select(String sqlStr, List params=[]){

        logger.info(sqlStr)
        if(params){
            return sql.rows(sqlStr, params)
        }
        return sql.rows(sqlStr)

    }

}
