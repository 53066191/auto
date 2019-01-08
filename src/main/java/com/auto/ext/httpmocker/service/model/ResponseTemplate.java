 package com.auto.ext.httpmocker.service.model;

 import java.io.Serializable;

 public class ResponseTemplate
   implements Serializable
 {
   private static final long serialVersionUID = 1L;
   private String id;
   private String comment;
   private String service;
   private String method;
   private String paramExp;
   private String response;
   private ResponseType responseType;

   public static enum ResponseType
   {
     JSON(1),  SCRIPT(2),  TEXT(3);

     private final int value;

     private ResponseType(int value)
     {
       this.value = value;
     }

     public int getValue()
     {
       return this.value;
     }
   }

   public String getId()
   {
     return this.id;
   }

   public void setId(String value)
   {
     this.id = value;
   }

   public String getComment()
   {
     return this.comment;
   }

   public void setComment(String value)
   {
     this.comment = value;
   }

   public String getService()
   {
     return this.service;
   }

   public void setService(String value)
   {
     this.service = value;
   }

   public String getMethod()
   {
     return this.method;
   }

   public void setMethod(String value)
   {
     this.method = value;
   }

   public String getParamExp()
   {
     return this.paramExp;
   }

   public void setParamExp(String value)
   {
     this.paramExp = value;
   }

   public String getResponse()
   {
     return this.response;
   }

   public void setResponse(String value)
   {
     this.response = value;
   }

   public ResponseType getResponseType()
   {
     return this.responseType;
   }

   public void setResponseType(ResponseType value)
   {
     this.responseType = value;
   }

   public String toString()
   {
     return "id:" + this.id + "," + "comment:" + this.comment + "," + "service:" + this.service + "," + "method:" + this.method + "," + "paramExp:" + this.paramExp + "," + "response:" + this.response + "," + "responseType:" + this.responseType;
   }
 }


