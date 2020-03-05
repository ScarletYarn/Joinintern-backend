package com.joininterngroup.joinintern.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class WeixinController {

    public String getOpenid(String code) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(
                    String.format(
                            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                            "wxf2b6552c0ffdf036",
                            "733cfccdc53909002349f432fdefe4de", code));
            log.info(String.format("Executing request %s", httpGet.getRequestLine()));

            ResponseHandler<String> responseHandler = httpResponse -> {
                HttpEntity entity = httpResponse.getEntity();
                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String, String> map = objectMapper.readValue(EntityUtils.toString(entity),
                        new TypeReference<HashMap<String, String>>() {});
                return map.get("openid");
            };

            String resBody = httpClient.execute(httpGet, responseHandler);
            log.info(String.format("The response is %s", resBody));
            return resBody;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
