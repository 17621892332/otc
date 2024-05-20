package org.orient.otc.common.security.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求来源的IP地址
 */
public class AdrressIpUtils {

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * 获取Ip地址
     * @param request HttpServletRequest
     * @return ip
     */
    public static String getIpAdrress(HttpServletRequest request) {
        String rip = request.getRemoteAddr();
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                rip = ip;
                break;
            }
        }
        int pos = rip.indexOf(',');
        if (pos >= 0) {
            rip = rip.substring(0, pos);
        }
        return rip;
    }
}
