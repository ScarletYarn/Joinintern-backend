package com.joininterngroup.joinintern.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

@Component
@Slf4j
public class FileService {

    @Value("${joinintern.basePath}")
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
                InputStream inputStream = response.getEntity().getContent();
                storeFile(inputStream, file);
                return path;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error! ";
    }

    public String saveFile(String dir, MultipartFile file) {

        try {
            String originName = file.getOriginalFilename();
            String filename = DigestUtils.md5DigestAsHex(Long.toString(new Date().getTime()).getBytes());
            if (originName != null) {
                String[] parts = originName.split("\\.");
                filename += "." + parts[parts.length - 1];
            } else {
                if (file.getContentType() != null) {
                    String[] parts = file.getContentType().split("/");
                    filename += parts[parts.length - 1];
                }
            }
            InputStream inputStream = file.getInputStream();
            String path = new File(dir, filename).toString();
            File store = new File(this.basePath, path);
            storeFile(inputStream, store);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void deleteFile(String path) {
        File file = new File(this.basePath, path);
        if (file.isFile()) {
            if(file.delete()) {
                log.info(String.format("File %s successfully deleted", path));
            }
        }
    }

    private void testExist(File file) {
        if (file.exists()) log.warn(String.format("File %s already exists and will be overwritten.", file.toString()));
        File p = file.getParentFile();
        if (!p.exists()) p.mkdirs();
    }

    private void storeFile(InputStream inputStream, File file) throws IOException {
        testExist(file);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        byte[] bytes = new byte[this.maxLength];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
        inputStream.close();
        fileOutputStream.close();
        log.info(String.format("File %s saved.", file.toString()));
    }
}
