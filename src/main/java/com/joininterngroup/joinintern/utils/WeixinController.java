package com.joininterngroup.joinintern.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeixinController {

    public String getOpenid(String code) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(
                    String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%S&secret=%s&js_code=%s&grant_type=authorization_code", "APPID", "SECRET", code));
            log.info(String.format("Executing request %s", httpGet.getRequestLine()));

            ResponseHandler<String> responseHandler = httpResponse -> {
                HttpEntity entity = httpResponse.getEntity();
                return EntityUtils.toString(entity);
            };

            String resBody = httpClient.execute(httpGet, responseHandler);
            log.info(String.format("The response is %s", resBody.substring(0, 20)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
