#!/bin/sh

# Replace nginx default port (80) with 3000
sed -i 's/listen  *80;/listen 3000;/g' /etc/nginx/conf.d/default.conf

# Replace API URL if provided at runtime
if [ -n "$VITE_API_URL" ]; then
  find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|RUNTIME_API_URL_PLACEHOLDER|$VITE_API_URL|g" {} \;
  echo "API URL set to $VITE_API_URL"
fi

# Execute the main container command
exec "$@"
