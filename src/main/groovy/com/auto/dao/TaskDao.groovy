package com.auto.dao

import com.auto.util.GroovyJDBC

class TaskDao {

    static String taskHeaderTable = "task"
    static String taskItemTable = "task_item"

    def static  queryTaskByBizNo(GroovyJDBC jdbc, String bizNo){
        String sql = "select * from ${taskHeaderTable} where biz_no = ?"
        return jdbc.selectOne(sql, [bizNo])
    }

    def static queryTaskItemsByBizNo(GroovyJDBC jdbc, String bizNo){
        def taskId = queryTaskByBizNo(jdbc, bizNo).id
        String sql = "select * from ${taskItemTable} where task_id = ?"
        return jdbc.select(sql, [taskId])
    }

    // type=1 入库，2出库
    def static queryNoDoneTask(GroovyJDBC jdbc, int status, int type){
        String sql = "select * from ${taskHeaderTable} where status = ? and type = ?"
        return jdbc.select(sql, [status, type])
    }

}
