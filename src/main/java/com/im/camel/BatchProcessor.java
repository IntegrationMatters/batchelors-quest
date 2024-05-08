package com.im.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BatchProcessor extends RouteBuilder {

    @ConfigProperty(name = "forward.to", defaultValue = "http://httpbin.org/post")
    private String forwardTo;

    @ConfigProperty(name = "batch.mode", defaultValue = "batch")
    private String batchMode;

    @Override
    public void configure() throws Exception {
        
        from("seda:processBatch")
            .id("processBatch")
            .log("Processing batch of ${header.CamelAggregatedSize} items")
            // when batchmode is single process individually
            .choice()
                .when(constant("single").isEqualTo(batchMode))
                    .split(body())
                        .log("Processing item: ${body}")
                        // restore request details
                        .process(exchange -> {                    
                            exchange.getIn().removeHeaders("*");
                            HttpRequest details = exchange.getIn().getBody(HttpRequest.class);
                            exchange.getIn().setHeader(Exchange.HTTP_METHOD, details.getHttpMethod());
                            exchange.getIn().setHeader(Exchange.HTTP_URI, forwardTo + details.getUrl());
                            exchange.getIn().setBody(details.getPayload());
                        })
                        .log("Forwarding to: ${exchangeProperty.CamelHttpUri}")
                        .toD("${header.CamelHttpUri}")
                        .log("Response from the forwarded request: ${body}")
                    .end()
                .endChoice()
                // otherwise concatenate bodies and forward as a single request
                .otherwise()
                    // concatenate bodies
                    .process(exchange -> {
                        exchange.getIn().removeHeaders("*");
                        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                        exchange.getIn().setHeader(Exchange.HTTP_URI, forwardTo);
                        StringBuilder body = new StringBuilder();
                        for (Object item : exchange.getIn().getBody(Iterable.class)) {
                            HttpRequest details = (HttpRequest) item;
                            body.append(details.getPayload());
                        }
                        exchange.getIn().getBody(String.class);
                        exchange.getIn().setBody(body.toString());
                    })
                    .log("Forwarding to: ${exchangeProperty.CamelHttpUri}")
                    .toD("${header.CamelHttpUri}")
                    .log("Response from the forwarded request: ${body}");
        ;
        
    }

}
