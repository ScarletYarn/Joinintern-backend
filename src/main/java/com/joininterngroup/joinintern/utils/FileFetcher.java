package com.joininterngroup.joinintern.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Component
@Slf4j
public class FileFetcher {

    @Value("${joinintern.mediaPath}")
    private String basePath;

    @Value("${joinintern.maxChunkLength}")
    private Integer maxLength;

    public String getFile(String url, String dir, String filename) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            String path = new File(dir, filename).toString();

            try (CloseableHttpResponse response = client.execute(get)) {
                HttpEntity entity = response.getEntity();
                log.info(String.format("Getting resource %s with type %s and length %d",
                        url, entity.getContentType(),
                        entity.getContentLength()));
                File file = new File(this.basePath, path);
                if (file.exists()) log.warn(String.format("File %s already exists and will be overwritten.", file.toString()));
                File p = file.getParentFile();
                if (!p.exists()) p.mkdirs();
                InputStream inputStream = response.getEntity().getContent();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] bytes = new byte[this.maxLength];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
                inputStream.close();
                fileOutputStream.close();
                log.info(String.format("File %s saved.", file.toString()));
                return file.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error! ";
    }
}
