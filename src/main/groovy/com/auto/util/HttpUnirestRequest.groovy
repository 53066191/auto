package com.auto.util

import com.alibaba.fastjson.JSON
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import groovy.json.JsonBuilder
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HttpUnirestRequest {
    public static Logger logger = LoggerFactory.getLogger("TEST");

    public static JSONObject sendPost(String url, Map body) {
        logger.info("${url}报文:");
        logger.info(new JsonBuilder(body).toPrettyString())
        HttpResponse<JsonNode> resp = Unirest.post(url).header("Content-Type", "application/json").fields(body).asJson();
        logger.info("+++++++收到返回:+++++++:\n{}", resp.getBody().jsonObject.toString())
        return resp.getBody().jsonObject;
    }

    public static JSONObject sendPost(String url) {
        logger.info("+++++++发送请求:{} +++++++:\n", url)
        HttpResponse<JsonNode> resp = Unirest.post(url).asJson();
        logger.info("+++++++收到返回:+++++++:\n{}", resp.getBody().jsonObject.toString())

        return resp.getBody().jsonObject;
    }

    public static JSONObject sendGet(String url) {
        logger.info("+++++++发送请求:{} +++++++:\n", url)
        HttpResponse<JsonNode> resp = Unirest.get(url).asJson();
        logger.info("+++++++收到返回:+++++++:\n{}", resp.getBody().jsonObject.toString())
        return resp.getBody().jsonObject;
    }

    public static JSONObject sendPut(String url) {
        logger.info("+++++++发送请求:{} +++++++:\n", url)
        HttpResponse<JsonNode> resp = Unirest.put(url).asJson()
        logger.info("+++++++收到返回:+++++++:\n{}", resp.getBody().jsonObject.toString())

        return resp.getBody().jsonObject;
    }

    public static JSONObject sendPut(String url, Map body) {

        String str = JSON.toJSONString(body, true)
        logger.info("+++++++发送请求:{} +++++++:\n{}", url, str)
        HttpResponse<JsonNode> resp = Unirest.put(url).header("Content-Type", "application/json").body(str).asJson()
        logger.info("+++++++收到返回:+++++++:\n{}", resp.getBody().jsonObject.toString())

        return resp.getBody().jsonObject;
    }

    public static JSONObject sendPutByString(String url, String body){
        logger.info("+++++++发送请求:{} +++++++:\n{}", url, body)
        HttpResponse<JsonNode> resp = Unirest.put(url).header("Content-Type", "application/json").body(body).asJson()
        logger.info("+++++++收到返回:+++++++:\n{}", resp.getBody().jsonObject.toString())

        return resp.getBody().jsonObject
    }

    public static JSONObject sendUpload2(String url, String fileName, String paramName) {
        logger.info(url)
        HttpResponse<JsonNode> resp = Unirest.post(url).field(paramName, new File(fileName)).asJson()
        logger.info(resp.getBody().jsonObject.toString())
        return resp.getBody().jsonObject;
    }

    public static JSONObject sendUpload(String url, String fileName) {
        logger.info(url)
        HttpResponse<JsonNode> resp = Unirest.post(url).field("fileName", new File(fileName)).asJson()
        logger.info(resp.getBody().jsonObject.toString())
        return resp.getBody().jsonObject;
    }


    public static JSONObject sendPostByString(String url, String body) {
        logger.info(url)
        HttpResponse<JsonNode> resp = Unirest.post(url).header("Content-Type", "application/json").body(body).asJson()
//        def resp =  Unirest.post(url).header("Content-Type", "application/json").body(body)

        logger.info(resp.getBody().jsonObject.toString())
        return resp.getBody().jsonObject
    }

    public static JSONObject sendPostByObject(String url, Object obj) {
        String body = new JsonBuilder(obj).toPrettyString()
        logger.info("+++++++发送请求:{} +++++++:\n{}", url, body)
        JSONObject resp = sendPostByString(url, body)
        logger.info("+++++++收到返回:+++++++:\n{}", resp.toString())
        return resp
    }




}




