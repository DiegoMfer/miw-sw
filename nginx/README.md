# Nginx Static Test Page

This folder contains a simple `index.html` file used to verify that the Nginx reverse proxy is serving static content correctly over HTTPS.

## How to Test

1. Make sure the Nginx container is running and port 443 is open.
2. Visit: [https://156.35.95.52/](https://156.35.95.52/) in your browser.
3. You should see the welcome page from `nginx/index.html`.

If you see this page, your Nginx HTTPS setup is working!

## Service URLs via Nginx

- Data Panel Client: [https://156.35.95.52/data-panel/](https://156.35.95.52/data-panel/)
- SearchMIW Client: [https://156.35.95.52/search/](https://156.35.95.52/search/)
