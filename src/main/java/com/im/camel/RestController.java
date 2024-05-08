package com.im.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class RestController extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        restConfiguration()
            .bindingMode(RestBindingMode.json)
        ;

        rest("/")
            .post()
            .to("direct:addToBatch")
        ;

    }

}
