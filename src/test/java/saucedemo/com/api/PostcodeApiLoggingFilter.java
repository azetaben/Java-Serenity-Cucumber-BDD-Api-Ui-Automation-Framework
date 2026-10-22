package saucedemo.com.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostcodeApiLoggingFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostcodeApiLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpecification,
                           FilterContext context) {
        LOGGER.info("api REQUEST  {} {}", request.getMethod(), request.getURI());
        if (!request.getHeaders().asList().isEmpty()) {
            LOGGER.info("api REQUEST HEADERS\n{}", request.getHeaders());
        }
        if (request.getBody() != null) {
            LOGGER.info("api REQUEST BODY\n{}", (Object) request.getBody());
        }

        Response response = context.next(request, responseSpecification);
        LOGGER.info("api RESPONSE {} {} ({} ms)", response.getStatusCode(),
                request.getURI(), response.getTime());
        if (response.getHeaders() != null) {
            LOGGER.info("api RESPONSE HEADERS\n{}", response.getHeaders());
        }
        String responseBody = response.getBody() == null ? null : response.getBody().asString();
        if (responseBody != null && !responseBody.isBlank()) {
            LOGGER.info("api RESPONSE BODY\n{}", responseBody);
        }
        return response;
    }
}
