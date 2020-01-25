package com.joininterngroup.joinintern.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class JoinInternEnvironment {

    private final ApplicationContext applicationContext;

    public JoinInternEnvironment(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean isProd() {
        String profile = this.applicationContext.getEnvironment().getActiveProfiles()[0];
        return profile.equals("prod");
    }
}
