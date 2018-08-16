package team.benchem.communication;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName ServiceCenter
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-16 9:23
 * @Version 1.0
 **/
public interface ServiceCenter extends ServicePortal {

    void register(String clientTag);

    void unRegister(String clientTag);

    void cast(RequestBody requestBody);

    ResponseBody forward(String clientTag, RequestBody requestBody);

}
