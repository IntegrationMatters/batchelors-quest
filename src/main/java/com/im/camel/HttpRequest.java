package com.im.camel;

public class HttpRequest {
    private String httpMethod;
    private String url;
    private String payload;

    // Constructor
    public HttpRequest(String httpMethod, String url, String payload) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.payload = payload;
    }

    // Default constructor
    public HttpRequest() {
    }

    // Getters and setters
    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "HttpRequestDetails{" +
               "httpMethod='" + httpMethod + '\'' +
               ", url='" + url + '\'' +
               ", payload='" + payload + '\'' +
               '}';
    }
}
