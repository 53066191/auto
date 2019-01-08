package com.auto.wait

class WaitTools {

    static Object waitForResult(int tryTimes, long period, Closure closure) {
        Object result =  closure.call()

        while (tryTimes-- > 0 && (result == null || Boolean.FALSE.equals(result))) {
            sleep(period)
            result = closure.call()

        }
        println("trytimes:${tryTimes}")
        return result
    }




}
