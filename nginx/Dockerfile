# Nginx reverse proxy Dockerfile
FROM nginx:alpine

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/

# Copy SSL certificates (self-signed or real)
# Place your certs in the certs/ directory
COPY certs/ /etc/nginx/certs/

# Copy index.html for static serving
COPY index.html /usr/share/nginx/html/index.html

EXPOSE 443
CMD ["nginx", "-g", "daemon off;"]
