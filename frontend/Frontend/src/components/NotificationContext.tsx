import React, { createContext, useContext, useEffect, useState } from 'react';
import axios from 'axios';

interface Notification {
  notificationId: number;
  notificationsType: string;
  content: string;
  createdAt: string;
  relatedEntityId: number | null;
  postId: number | null;
  isRead: boolean;
}

interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  refreshNotifications: () => void;
  markAsRead: (id: number) => void;
  setNotifications: React.Dispatch<React.SetStateAction<Notification[]>>;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) throw new Error("useNotification must be used within NotificationProvider");
  return context;
};

export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const userId = localStorage.getItem('userId');

  const fetchNotifications = async () => {
    if (!userId) return;

    try {
      const res = await axios.get<Notification[]>(`http://localhost:8080/notifications/getAll`, {
        params: { uId: userId },
      });
      const sorted = res.data.sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
      setNotifications(sorted);
    } catch (err) {
      console.error('Failed to fetch notifications');
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, [userId]);

  const markAsRead = async (id: number) => {
    try {
      await axios.put(
        `http://localhost:8080/notifications/mark-as-read/${id}`,
        null,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken')}`,
            'Content-Type': 'application/json',
          },
        }
      );
      setNotifications((prev) =>
        prev.map((n) => (n.notificationId === id ? { ...n, isRead: true } : n))
      );
    } catch (e) {
      console.error('Error marking notification as read:', e);
    }
  };

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return (
    <NotificationContext.Provider
      value={{ notifications, unreadCount, refreshNotifications: fetchNotifications, markAsRead, setNotifications }}
    >
      {children}
    </NotificationContext.Provider>
  );
};
