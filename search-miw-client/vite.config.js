import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import fs from 'fs';
import path from 'path';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 3000,
    open: true, // Automatically open the app in the default browser
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'certs/search-client.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'certs/search-client.crt')),
    },
  },
})
