package com.example.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.example.properties.NextcloudProperties;

@Configuration
@EnableConfigurationProperties(NextcloudProperties.class)
public class NextcloudConfig {
}
