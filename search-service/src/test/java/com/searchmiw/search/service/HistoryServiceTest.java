package com.searchmiw.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    private HistoryService historyService;

    @Mock
    private WebClient webClientMock;
    
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    
    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;
    
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        // Create a new service with our mock WebClient
        historyService = new HistoryService("http://test-history-service:8085") {
            @Override
            protected WebClient createWebClient(String baseUrl) {
                return webClientMock;
            }
        };
        
        // We'll move the WebClient call chain setup to the specific test that needs it
    }

    @Test
    void doNotRecordSearchHistoryWhenUserIdIsNull() {
        // When
        historyService.recordSearchHistory(null, "test query");
        
        // Then
        verify(webClientMock, never()).post();
    }

    @Test
    void recordSearchHistoryWithValidUserId() {
        // Setup WebClient call chain for this test only
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.empty());
        
        // When
        historyService.recordSearchHistory(123L, "test query");
        
        // Then
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(any(java.util.function.Function.class));
        verify(requestBodySpecMock).retrieve();
        verify(responseSpecMock).toBodilessEntity();
    }
}
