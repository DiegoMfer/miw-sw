// ...existing code...

@Configuration
public class GatewayConfig {

    @Value("${AUTH_SERVICE_URL:http://auth-service:8081}")
    private String authServiceUrl;

    @Value("${DATA_AGGREGATOR_URL:http://data-aggregator-service:8083}")
    private String dataAggregatorUrl;
    
    @Value("${USER_SERVICE_URL:http://user-service:8084}")
    private String userServiceUrl;

    // ...existing code...
}
