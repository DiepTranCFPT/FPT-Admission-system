package com.sba.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dl9diyx0e",
                "api_key", "556613968443269",
                "api_secret", "YLQ2TlImPLE_UHhYLkH07c8pQyI",
                "secure", true
        ));
    }
}
