package com.searchmiw.dataaggregator.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ServiceException) {
            ServiceException serviceEx = (ServiceException) ex;
            return GraphqlErrorBuilder.newError()
                    .message(serviceEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .errorType(serviceEx.getErrorType())
                    .build();
        } else if (ex instanceof WebClientResponseException) {
            WebClientResponseException wcEx = (WebClientResponseException) ex;
            ErrorType errorType = wcEx.getStatusCode().is4xxClientError() ?
                    ErrorType.BAD_REQUEST : ErrorType.INTERNAL_ERROR;
            
            return GraphqlErrorBuilder.newError()
                    .message("Service communication error: " + wcEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .errorType(errorType)
                    .build();
        }
        
        return GraphqlErrorBuilder.newError()
                .message("Unexpected error: " + ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}
