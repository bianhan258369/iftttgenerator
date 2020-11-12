package com.example.bianhan.iftttgenerator.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileUtil {
    public static void download(String filePath, HttpServletResponse res) throws IOException {
        // 发送给客户端的数据
        OutputStream outputStream = res.getOutputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        // 读取filename
        bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
        int i;
        while ((i = bis.read(buff)) != -1) {
            outputStream.write(buff, 0, i);
            outputStream.flush();
        }
        outputStream.close();
        bis.close();
    }
}
