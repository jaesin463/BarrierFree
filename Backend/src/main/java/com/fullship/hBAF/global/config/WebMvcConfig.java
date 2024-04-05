package com.fullship.hBAF.global.config;

import com.fullship.hBAF.global.auth.jwt.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterCeptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(authInterCeptor).excludePathPatterns(
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs"
                );
    }
}
