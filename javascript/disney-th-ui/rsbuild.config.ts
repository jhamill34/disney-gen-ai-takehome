import { defineConfig } from '@rsbuild/core';
import { pluginReact } from '@rsbuild/plugin-react';

export default defineConfig({
  plugins: [pluginReact()],
  source: {
    entry: {
      app: './src/entries/app/entry.tsx',
    },
  },
  server: {
    historyApiFallback: {
      rewrites: [{ from: /^\/app/, to: '/app.html' }],
    },
  },
  output: {
    manifest: true,
  },
});
