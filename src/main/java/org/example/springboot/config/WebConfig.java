package org.example.springboot.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
//    @Configuration        声明这是 Spring 配置类
//    addCorsMappings()        配置跨域规则
//    addMapping("/**")        所有后端接口都应用跨域规则
//    allowedOrigins()        指定允许访问的前端地址
//    allowedMethods()        指定允许的请求方式
//    allowedHeaders("*")        允许前端携带各种请求头
//    allowCredentials(false)        不允许携带 Cookie 等凭证
//    maxAge(3600)        浏览器缓存预检结果 3600 秒
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许访问后端的前端地址
                .allowedOrigins("http://localhost:5173")
                // 允许的请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 暂时不允许 Cookie 等凭证
                .allowCredentials(false)
                // 预检请求缓存时间：3600 秒
                .maxAge(3600);
    }
}