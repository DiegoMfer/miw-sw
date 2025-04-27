# Gateway Service
  gateway:
    build: ./gateway
    container_name: gateway
    depends_on:
      - user-service
      - search-service
      - history-service
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVICE_USER_URL=http://user-service:8086
      - SERVICE_SEARCH_URL=http://search-service:8084
      - SERVICE_HISTORY_URL=http://history-service:8085
    ports:
      - "8080:8080"
    networks:
      - searchmiw-network
