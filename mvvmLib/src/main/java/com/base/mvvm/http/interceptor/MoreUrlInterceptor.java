package com.base.mvvm.http.interceptor;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 多 URL 切换
 */
public class MoreUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取request
        Request request = chain.request();
        // 从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        // 获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        // 从request中获取headers，通过给定的键url_name
        List<String> headerValues = request.headers("baseUrl");
        if (headerValues != null && headerValues.size() > 0) {
            // 如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder.removeHeader("baseUrl");
            // 匹配获得新的BaseUrl
            String headerValue = headerValues.get(0);
            // 新 baseUrl
            HttpUrl newBaseUrl = null;
            if (headerValue != null && headerValue.length() > 0) {
                newBaseUrl = HttpUrl.parse(headerValue);
            } else {
                newBaseUrl = oldHttpUrl;
            }

            // 重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    // 更换网络协议
                    .scheme(newBaseUrl.scheme())
                    // 更换主机名
                    .host(newBaseUrl.host())
                    // 更换端口
                    .port(newBaseUrl.port())
                    .build();
            // 重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            return chain.proceed(builder.url(newFullUrl).build());
        }
        return chain.proceed(request);
    }
}