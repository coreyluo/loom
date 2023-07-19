package com.bazinga.test;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("000001");
        list.add("000002");

        System.out.println(JSONObject.toJSONString(list));
        list.remove("000001");
        System.out.println(JSONObject.toJSONString(list));
    }
}
