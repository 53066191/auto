package com.auto.ext.httpmocker.service;

import com.auto.ext.httpmocker.service.exception.MockerServiceException;
import com.auto.ext.httpmocker.service.model.MockerRouteEntry;
import com.auto.ext.httpmocker.service.model.ResponseTemplate;
import com.auto.ext.httpmocker.service.model.ResponseTemplateEntry;

import java.util.List;
import java.util.Map;

public abstract interface HttpMockerService
{
  public abstract void setGlobalVariable(String paramString1, String paramString2)
    throws MockerServiceException;
  
  public abstract void setGlobalLong(String paramString, Long paramLong)
    throws MockerServiceException;
  
  public abstract void setGlobalDouble(String paramString, Double paramDouble)
    throws MockerServiceException;
  
  public abstract void removeGlobalVariable(String paramString)
    throws MockerServiceException;
  
  public abstract <T> T getGlobalVariable(String paramString, Class<T> paramClass)
    throws MockerServiceException;
  
  public abstract String getGlobalVariableAsString(String paramString)
    throws MockerServiceException;
  
  public abstract boolean addRouteEntry(MockerRouteEntry paramMockerRouteEntry)
    throws MockerServiceException;
  
  public abstract boolean removeRouteEntry(MockerRouteEntry paramMockerRouteEntry)
    throws MockerServiceException;
  
  public abstract ResponseTemplate addResponseTemplate(ResponseTemplate paramResponseTemplate, Integer paramInteger)
    throws MockerServiceException;
  
  public abstract ResponseTemplate editResponseTemplate(ResponseTemplate paramResponseTemplate)
    throws MockerServiceException;
  
  public abstract ResponseTemplate findResponseTemplate(String paramString)
    throws MockerServiceException;
  
  public abstract boolean removeResponseTemplate(String paramString)
    throws MockerServiceException;
  
  public abstract List<ResponseTemplate> saveResponseTemplateFile(String paramString1, String paramString2, boolean paramBoolean)
    throws MockerServiceException;
  
  public abstract List<ResponseTemplate> saveResponseTemplateFiles(List<ResponseTemplateEntry> paramList, boolean paramBoolean)
    throws MockerServiceException;
  
  public abstract List<ResponseTemplateEntry> syncResponseTemplateStatus(List<ResponseTemplateEntry> paramList, long paramLong, boolean paramBoolean)
    throws MockerServiceException;
  
  public abstract int registerRemoteMock(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
    throws MockerServiceException;
  
  public abstract int registerMock(String paramString1, String paramString2, String paramString3, String paramString4, Map<String, Object> paramMap, String paramString5)
    throws MockerServiceException;
  
  public abstract boolean unregisterRemoteMock(int paramInt)
    throws MockerServiceException;
}


