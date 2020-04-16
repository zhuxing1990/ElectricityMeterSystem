package com.vunke.electricity.server;


import com.vunke.electricity.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Administrator on 2018-03-23.
 */
public class WebRequest {
    private static final String TAG = "WebRequest";
    private String pageName;
    private HashMap<String, String> params = new HashMap<String, String>();

    public static WebRequest parse(InputStream in) throws IOException {
        return new WebRequest(in);
    }

    private WebRequest(InputStream in) throws IOException {
        parseUrlRequest(new BufferedReader(new InputStreamReader(in)));
    }

    /**
     * 仅解析请求页面和 GET请求方式的参数
     * @param reader
     * @throws IOException
     */
    private void parseUrlRequest(BufferedReader reader) throws IOException {
        String request = reader.readLine();

        if (request != null && request.contains("GET") && request.contains("HTTP/1.1") ) {
            request = request.replace("GET ", "")
                    .replace(" HTTP/1.1", "");

            String[]splits = request.split("\\?");
            if (splits.length > 0) {
                LogUtil.e("WebConfig", request+"---"+ splits.length);
                //parse request page name
                if (splits.length >=1){
                        pageName = URLDecoder.decode(splits[0]);
                        if (pageName.charAt(pageName.length() - 1) == '/') {
                            pageName = pageName.substring(0, pageName.length() - 1);
                        }
                }
                if (splits.length>=2){
                    splits = splits[1].split("&");
                    // parse parameters of url
                    for (int i = 0; i < splits.length; i++) {
                        String[]kv = splits[i].split("=");
                        if (kv.length >= 2) {
                            params.put(URLDecoder.decode(kv[0]), URLDecoder.decode(kv[1]));
                        }
                    }
                }
            }// end if
        }// end if
    }

    public String getPageName() {
        return pageName;
    }

    public String queryParameter(String name) {
        return params.get(name);
    }
}
