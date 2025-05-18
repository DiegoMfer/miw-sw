import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs';
import path from 'path';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 3001, // default, can be omitted if not customized
    open: true, // Automatically open the app in the default browser
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'certs/data-panel-client.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'certs/data-panel-client.crt')),
    },
  },
})
