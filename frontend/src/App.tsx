import React, { JSX } from "react";

import { Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./AuthContext";

import Navbar from "./components/Navbar";

import HomePage from "./components/Home";

import Login from "./components/Login";

import Register from "./components/Register";

import EventsPage from "./components/EventsPage";

import CreateEventPage from "./components/CreateEventPage";
import EventDetails from "./components/EventDetails";
import NewsPage from "./components/NewsPage";
import NewsDetailPage from "./components/NewsDetailPage";
import "bootstrap/dist/css/bootstrap.min.css";

// Protected route component
import AddNewsPage from "./components/AddNewsPage";
import EditNewsPage from "./components/EditNewsPage";
import SurveyList from "./components/SurveyList";
import SurveyCreator from "./components/SurveyCreator";
import SurveyParticipation from "./components/SurveyParticipation";
import { Key } from "lucide-react";
import { keys } from "@mui/system";
import EditEvents from "./components/EditEvents";
import ForumsPage from "./components/ForumsPage";
import ForumThreadsPage from "./components/ForumThreadsPage";
import ForumThreadDetails from "./components/ForumThreadDetails";
import AddForumThread from "./components/AddForumThread";
import EditForumThread from "./components/EditForumThread";
import AddForum from "./components/AddForum";
import UserProfile from "./components/UserProfile";
import ChangePassword from "./components/ChangePassword";
import CreateProfile from "./components/CreateProfile";
import SurveyQuestionBuilder from "./components/SurveyQuestionBuilder";
import Notifications from "./components/Notifications";
import { ToastContainer } from "react-toastify";
import SurveyResultDisplay from "./components/SurveyResults";
import SurveyResultsWrapper from "./components/SurveyResultsWrapper";

const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const token = localStorage.getItem("authToken");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

function App() {
  return (
    <AuthProvider>
      <Navbar />

      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/" element={<HomePage />} />

        <Route path="/events" element={<EventsPage />} />

        <Route path="/events/:id" element={<EventDetails />} />

        <Route path="/events/createEvent" element={<CreateEventPage />} />

        <Route path="/news" element={<NewsPage />} />

        <Route path="/forums" element={<ForumsPage />} />

        <Route path="/profile" element={<UserProfile />} />
        <Route
          path="/news/:id"
          element={<NewsDetailPage key={window.location.pathname} />}
        />

        <Route
          path="/forums/:id"
          element={<ForumThreadsPage key={window.location.pathname} />}
        />

        {/* <Route
          path="/notifications"
          element={<Notifications key={window.location.pathname} />}
        /> */}

        <Route
          path="/forums/:id/:threadId"
          element={<ForumThreadDetails key={window.location.pathname} />}
        />

        <Route
          path="/events/create"
          element={
            <ProtectedRoute>
              <CreateEventPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/forumThread/create/:forumId"
          element={
            <ProtectedRoute>
              <AddForumThread />
            </ProtectedRoute>
          }
        />

        <Route
          path="/forums/create"
          element={
            <ProtectedRoute>
              <AddForum />
            </ProtectedRoute>
          }
        />

        <Route
          path="/news/add"
          element={
            <ProtectedRoute>
              <AddNewsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/news/edit/:id"
          element={
            <ProtectedRoute>
              <EditNewsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/forumThread/edit/:id"
          element={
            <ProtectedRoute>
              <EditForumThread />
            </ProtectedRoute>
          }
        />

        <Route
          path="/events/edit/:id"
          element={
            <ProtectedRoute>
              <EditEvents />
            </ProtectedRoute>
          }
        />

        <Route
          path="user/changepassword"
          element={
            <ProtectedRoute>
              <ChangePassword />
            </ProtectedRoute>
          }
        />

        <Route
          path="/create-profile"
          element={
            <ProtectedRoute>
              <CreateProfile />
            </ProtectedRoute>
          }
        />

        <Route
          path="/survey/create"
          element={
            <ProtectedRoute>
              <SurveyCreator />
            </ProtectedRoute>
          }
        />

        <Route
          path="/survey/participation/:id"
          element={
            <ProtectedRoute>
              <SurveyParticipation />
            </ProtectedRoute>
          }
        />

        <Route
          path="survey/questions/:id"
          element={
              <SurveyQuestionBuilder />
          }
        />

        <Route path="*" element={<Navigate to="/" />} />
        <Route path="/survey" element={<SurveyList />} />
        <Route path="/survey/results/:surveyId" element={<SurveyResultsWrapper />} />

      </Routes>
    </AuthProvider>
  );
}

export default App;
