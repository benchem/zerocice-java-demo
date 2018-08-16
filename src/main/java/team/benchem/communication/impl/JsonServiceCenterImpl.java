package team.benchem.communication.impl;

import com.alibaba.fastjson.JSON;
import com.zeroc.Ice.Current;
import team.benchem.communication.JsonServiceCenter;
import team.benchem.communication.JsonServicePortalPrx;
import team.benchem.communication.ResponseBody;
import team.benchem.lang.SystemStateCode;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JsonServiceCenterImpl
 * @Deseription TODO
 * @Author chenjiabin
 * @Date 2018-08-16 9:43
 * @Version 1.0
 **/
public class JsonServiceCenterImpl implements JsonServiceCenter {

    final static int TIMEOUT_SECOND = 30;
    final static Map<String, Date> clientExpiresTime = new HashMap<>();
    final static Map<String, JsonServicePortalPrx> clientProxies = new HashMap<>();

    @Override
    public void register(String clientTag, JsonServicePortalPrx callBack, Current current) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, TIMEOUT_SECOND);
        Date expiresTime = calendar.getTime();
        clientExpiresTime.put(clientTag, expiresTime);
        clientProxies.put(clientTag, callBack);
    }

    @Override
    public void unRegister(String clientTag, Current current) {
        if(clientProxies.containsKey(clientTag)){
            clientProxies.remove(clientTag);
        }
        if(clientExpiresTime.containsKey(clientTag)){
            clientExpiresTime.remove(clientTag);
        }
    }

    @Override
    public void cast(String requestBody, Current current) {
        Calendar calendar = Calendar.getInstance();
        Date timeNow = calendar.getTime();
        for(Map.Entry<String, Date> item : clientExpiresTime.entrySet()){
            if(item.getValue().before(timeNow)){
                continue;
            }

            if(clientProxies.containsKey(item.getKey())){
                clientProxies.get(item.getKey()).invoke(requestBody);
            }
        }
    }

    @Override
    public String forward(String clientTag, String requestBody, Current current) {
        if(!clientExpiresTime.containsKey(clientTag) || !clientProxies.containsKey(clientTag)){
            ResponseBody responseBody = new ResponseBody(SystemStateCode.CLIENT_IS_OFFLINE);
            return JSON.toJSONString(responseBody);
        }

        Calendar calendar = Calendar.getInstance();
        Date timeNow = calendar.getTime();
        if(clientExpiresTime.get(clientTag).before(timeNow)){
            ResponseBody responseBody = new ResponseBody(SystemStateCode.CLIENT_IS_OFFLINE);
            return JSON.toJSONString(responseBody);
        }

        return clientProxies.get(clientTag).invoke(requestBody);
    }

    @Override
    public void ice_ping(Current current) {
        if(!current.ctx.containsKey("clientTag")){
            return;
        }

        String clientTag = current.ctx.get("clientTag");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, TIMEOUT_SECOND);
        Date expiresTime = calendar.getTime();
        clientExpiresTime.put(clientTag, expiresTime);
    }
}
