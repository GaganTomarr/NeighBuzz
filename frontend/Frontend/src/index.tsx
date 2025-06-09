import React from "react";

import ReactDOM from "react-dom/client";

import "./index.css";

import App from "./App";

import reportWebVitals from "./reportWebVitals";

import { BrowserRouter } from "react-router-dom";

import {Toaster} from 'react-hot-toast';
import { NotificationProvider } from "./components/NotificationContext";
const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

root.render(
  <React.StrictMode>
    <BrowserRouter>
      <NotificationProvider>
        <Toaster position="top-center" />
        <App />
      </NotificationProvider>
    </BrowserRouter>
  </React.StrictMode>
);
reportWebVitals();
