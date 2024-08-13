import React from 'react';
import ReactDOM from 'react-dom/client';

import '@/globals.css';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { ChatPage } from './chat';
import { IngestionPage } from './ingest';

const rootEl = document.getElementById('root');
if (rootEl) {
  const root = ReactDOM.createRoot(rootEl);
  root.render(
    <React.StrictMode>
      <BrowserRouter basename="/app">
        <Routes>
          <Route path="/chat" element={<ChatPage />} />
          <Route path="/ingest" element={<IngestionPage />} />
          <Route path="*" element={<Navigate to="/chat" />} />
        </Routes>
      </BrowserRouter>
    </React.StrictMode>,
  );
}
