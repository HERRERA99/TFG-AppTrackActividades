package com.aitor.api_tfg.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "ddi3feiij",
                "api_key", "242975367628615",
                "api_secret", "ssbV1aAhDSOZ4q6Y21EmtxbHOyg");
        return new Cloudinary(config);
    }
}
