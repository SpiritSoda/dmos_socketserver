package com.dmos.dmos_socketserver.dmos_common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ConfigUtil {
    public static HashMap<String, String> load(String fileName) throws Exception{
        String path = System.getProperty("user.dir");
        File file = new File(path + "/" + fileName);
        log.info("Loading config from path: {}", file.getPath());
        if(!file.exists()){
            log.error("Config file {} not exists, creating template", file.getPath());
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write("{\n");
            writer.write("  \"register\": \"127.0.0.1\",\n");
            writer.write("  \"data\": \"127.0.0.1\",\n");
            writer.write("  \"parent\": \"127.0.0.1\",\n");
            writer.write("  \"localhost\": \"127.0.0.1\"\n");
            writer.write("}");
            writer.flush();

            return new HashMap<String, String>();
        }
        InputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuffer buffer = new StringBuffer();
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        Gson gson = new Gson();
        HashMap<String, String> items = gson.fromJson(buffer.toString(), new TypeToken<HashMap<String, String>>(){}.getType());
        return items;
    }
}
