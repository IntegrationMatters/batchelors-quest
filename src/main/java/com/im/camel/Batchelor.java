package com.im.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Batchelor extends RouteBuilder{

    @ConfigProperty(name = "batch.size", defaultValue = "50")
    private int batchSize;
    
    @ConfigProperty(name = "batch.timeoutms", defaultValue = "10000")
    private int batchTimeout;
    
    @Override
    public void configure() throws Exception {
        
        from("direct:addToBatch")
          .id("addToBatch")
            // store the request details
            .process(exchange -> {
                String httpMethod = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);
                String uri = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
                String body = exchange.getIn().getBody(String.class);

                HttpRequest details = new HttpRequest();
                details.setHttpMethod(httpMethod);
                details.setUrl(uri.substring(1));
                details.setPayload(body);

                exchange.getIn().setBody(details); 
            })
            // batch the requests
            .aggregate(simple("true"), new GroupedBodyAggregationStrategy())
            .completionSize(batchSize)
            .completionInterval(batchTimeout)
            .to("seda:processBatch")
        ;
        
    }
}
