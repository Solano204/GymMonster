/// <reference types="vitest/config" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'node:path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    globals: true,
    // Default 5000ms is too tight for tests that type into many fields sequentially
    // (e.g. the 10-field registration form) once the full suite's jsdom setup overhead
    // stacks up across all files.
    testTimeout: 15000,
  },
  server: {
    port: 3004,
    proxy: {
      // Proxy API calls to the gateway during development. Gateway's real port
      // is 8081 (server.port=${SERVER_PORT:8081} in gateway/application.properties,
      // same default in configMaps/gateway-map.yaml) - not 8080.
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/GymMonster': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
})
