package ext.mocker.common.service;

import com.kiktech.ext.mocker.common.httpserver.ResponseClosure;

import java.util.Map;

public abstract interface MockerPlusService<V>
{
  public abstract int mock(String paramString1, String paramString2, String paramString3, Map<String, Object> paramMap, String paramString4, ResponseClosure<V> paramResponseClosure)
    throws Exception;
  
  public abstract void close()
    throws Exception;
}



