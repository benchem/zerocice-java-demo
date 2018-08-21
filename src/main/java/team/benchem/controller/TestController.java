package team.benchem.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.benchem.communication.JsonServiceCenter;
import team.benchem.communication.RequestBody;

/**
 * @ClassName TestController
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-16 18:12
 * @Version 1.0
 **/
@RequestMapping("/")
@RestController
public class TestController {

    @Autowired
    JsonServiceCenter serviceCenter;

    @GetMapping("/test")
    public void test(){
        JSONObject messageBody = new JSONObject();
        messageBody.put("aaa", "bbb");
        RequestBody body = new RequestBody("test", messageBody);

        serviceCenter.cast(JSON.toJSONString(body), null);
    }

}
