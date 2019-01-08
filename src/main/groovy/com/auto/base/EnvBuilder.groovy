package com.auto.base

import com.auto.util.GroovyJDBC

class EnvBuilder extends TestCaseBase{

    public static GroovyJDBC forkliftJDBC = getForkliftJdbc();
    public static GroovyJDBC taskistributeJDBC = getTaskDistributeJdbc();

}
