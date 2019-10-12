package net.kovand42.kova_design.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    CookieLocaleResolver localeResolver(){
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieMaxAge(604_800);
        return resolver;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LocaleChangeInterceptor());
    }
}
