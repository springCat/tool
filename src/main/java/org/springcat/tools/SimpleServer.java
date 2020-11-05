package org.springcat.tools;

import cn.hutool.http.HttpUtil;
import com.jcraft.jsch.JSchException;

/**
 * @Description SimpleServer
 * @Author springCat
 * @Date 2020/11/3 15:10
 */
public class SimpleServer {
    public static void main(String[] args) throws JSchException {
        HttpUtil.createServer(8888)
                .addAction("/", (req, res)->{
                    res.write("Hello Hutool Server");
                })
                .start();
    }
}
