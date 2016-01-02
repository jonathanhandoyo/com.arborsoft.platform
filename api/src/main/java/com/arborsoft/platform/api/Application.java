package com.arborsoft.platform.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    private static final Log LOG = LogFactory.getLog(Application.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplicationBuilder app = new Application().configure(new SpringApplicationBuilder(Application.class));
        Environment env = app.run(args).getEnvironment();
        LOG.info(
                String.format(
                        "\n" +
                        "Access URLs:\n" +
                        "----------------------------------------------------------\n" +
                        "\tExternal: \thttp://%s:%s\n" +
                        "\tInternal: \thttp://localhost:%s\n\n" +
                        "\tSwagger : \thttp://%s:%s/swagger-ui.html\n" +
                        "\tSwagger : \thttp://localhost:%s/swagger-ui.html\n" +
                        "----------------------------------------------------------",
                        InetAddress.getLocalHost().getHostAddress(),
                        env.getProperty("server.port"),
                        env.getProperty("server.port"),
                        InetAddress.getLocalHost().getHostAddress(),
                        env.getProperty("server.port"),
                        env.getProperty("server.port")
                )
        );
    }
}
