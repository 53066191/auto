package com.auto.wait

import com.auto.dao.TaskDao
import com.auto.util.GroovyJDBC
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RobotArrival {

    static Logger logger = LoggerFactory.getLogger(RobotArrival.class);

    static boolean waitTaskItemArrival(GroovyJDBC jdbc, String bizNo, int tryTimes, int period) {

        def items = TaskDao.queryTaskItemsByBizNo(jdbc, bizNo)

        WaitTools.waitForResult(tryTimes, period) {

            items = TaskDao.queryTaskItemsByBizNo(jdbc, bizNo)
            List arrival = items.status.unique()
            if (arrival == [1]){   // 1已到达
                logger.info("${bizNo}: 所有任务均已到达")
                return true
            }else{
                logger.info(".....等待机器人到达")
                return false
            }



        }
    }

}
