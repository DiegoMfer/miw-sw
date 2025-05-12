# Stage 1: Build with Node
FROM node:18-alpine as build

WORKDIR /app

# Copy package.json and package-lock.json (if available)
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy the built app from the build stage to Nginx's web root
COPY --from=build /app/dist /usr/share/nginx/html

# Copy a simple Nginx configuration
# You might need a custom nginx.conf if you have specific routing needs
# For now, we'll use a basic one that serves index.html for SPA routing.
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
