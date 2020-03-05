package com.joininterngroup.joinintern.config;

import com.joininterngroup.joinintern.utils.JoinInternEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private JoinInternEnvironment joinInternEnvironment;

    public TomcatConfig(JoinInternEnvironment joinInternEnvironment) {
        this.joinInternEnvironment = joinInternEnvironment;
    }

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return new MyErrorPageRegistrar();
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        if (this.joinInternEnvironment.isProd()) factory.addAdditionalTomcatConnectors(createHttpConnect());
    }

    private Connector createHttpConnect() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");

        connector.setScheme("http");
        connector.setPort(80);
        connector.setRedirectPort(443);

        return connector;
    }

    private static class MyErrorPageRegistrar implements ErrorPageRegistrar {

        @Override
        public void registerErrorPages(ErrorPageRegistry registry) {
            registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/"));
        }
    }
}
