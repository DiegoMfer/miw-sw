FROM node:18-alpine as build

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

# Install axios explicitly if it's not in package.json
RUN npm install axios --save

COPY . .

# Define build argument with a default value
ARG VITE_API_URL=http://localhost:8080

# Pass build argument to the environment for the build process
ENV VITE_API_URL=$VITE_API_URL

# Build the application with production settings
RUN npm run build

# Use nginx for serving static files
FROM nginx:alpine

# Copy the built app to nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create an entrypoint script that can modify files at runtime
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 3000

# Use the entrypoint script
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
