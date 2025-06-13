import React, { useEffect } from 'react';
import './Notifications.css';
import { useNavigate } from 'react-router-dom';
import { useNotification } from './NotificationContext';
import { LucideTrash2 } from 'lucide-react';
import toast from 'react-hot-toast';

interface NotificationsProps {
    isOpen: boolean;
    onClose: () => void;
}

const Notifications: React.FC<NotificationsProps> = ({ isOpen, onClose }) => {
    const {
        notifications,
        markAsRead,
        refreshNotifications
    } = useNotification();

    const navigate = useNavigate();

    const handleNotificationClick = async (notification: any) => {
        if (!notification.isRead) {
            await markAsRead(notification.notificationId);
        }

        if (
            notification.notificationsType === 'COMMENT_REPLY' &&
            notification.relatedEntityId &&
            notification.postId
        ) {
            navigate(`/news/${notification.postId}#comment-${notification.relatedEntityId}`);
        } else if (
            notification.notificationsType === 'FORUM_THREAD_DISCUSSION' &&
            notification.relatedEntityId
        ) {
            try {
                const res = await fetch(`http://localhost:8080/threads/getByThreadId/${notification.postId}`);
                const thread = await res.json();
                if (thread && thread.forum && thread.forum.forumId) {
                    navigate(`/forums/${thread.forum.forumId}/${notification.postId}`);
                } else {
                    navigate('/');
                }
            } catch (error) {
                console.error('Error fetching forum thread:', error);
                navigate('/');
            }
        } else if (
            notification.notificationsType === 'EVENT_UPDATE' &&
            notification.relatedEntityId
        ) {
            navigate(`/events/${notification.relatedEntityId}`);
        } else {
            navigate('/');
        }
    };

    const markAllAsRead = async () => {
        try {
            const userId = localStorage.getItem('userId');
            if (!userId) return;

            await fetch(`http://localhost:8080/notifications/mark-all-as-read?uId=${userId}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('authToken')}`
                }
            });

            refreshNotifications();
        } catch (error) {
            console.error("Failed to mark all as read", error);
        }
    };

    const handleDeleteNotification = async (notification: any) => {
        try {
            const userId = localStorage.getItem('userId');
            if (!userId) {
                console.error("User ID missing");
                return;
            }

            const payload = {
                notificationId: notification.notificationId,
                userDTO: { userId: Number(userId) },
                notificationsType: notification.notificationsType,
                content: notification.content,
                relatedEntityType: notification.relatedEntityType,
                relatedEntityId: notification.relatedEntityId,
                isRead: notification.isRead,
                createdAt: notification.createdAt,
                postId: notification.postId
            };

            const res = await fetch('http://localhost:8080/notifications/delete', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('authToken')}`
                },
                body: JSON.stringify(payload),
            });

            if (res.ok) {
                toast.success("Notification deleted successfully!");
            }

            if (!res.ok) {
                const errorMsg = await res.text();
                throw new Error(errorMsg || 'Failed to delete notification');
            }

            refreshNotifications();
        } catch (error) {
            console.error('Error deleting notification:', error);
        }
    };

    useEffect(() => {
        if (isOpen) {
            refreshNotifications();
        }
    }, [isOpen, refreshNotifications]);

    if (!notifications) {
        return (
            <div className={`notifications-container ${isOpen ? 'open' : ''}`}>
                Loading...
            </div>
        );
    }

    return (
        <div
            className={`notifications-container ${isOpen ? 'open' : ''}`}
            role="dialog"
            aria-modal="true"
            aria-labelledby="notifications-heading"
        >
            <div
                className="close-btn"
                onClick={onClose}
                aria-label="Close notifications panel"
            >
                &times;
            </div>

            <div
                className="notifications-header"
                style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                }}
            >
                <h2 id="notifications-heading">🔔 Notifications</h2>
                {notifications.length > 0 && (
                    <div onClick={markAllAsRead} className="mark-all-button">
                        Mark all as read
                    </div>
                )}
            </div>

            {notifications.length === 0 ? (
                <p>No notifications yet.</p>
            ) : (
                <ul className="notifications-list">
                    {notifications.map((notification) => (
                        <li
                            key={notification.notificationId}
                            className={`notification-item ${notification.isRead ? 'read' : 'unread'}`}
                            style={{ position: 'relative', cursor: 'pointer' }}
                            onClick={() => handleNotificationClick(notification)}
                        >
                            {!notification.isRead && (
                                <>
                                    <span className="unread-dot" aria-label="Unread notification" />
                                    <button
                                        className="mark-read-button"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            markAsRead(notification.notificationId);
                                        }}
                                    >
                                        Mark as Read
                                    </button>
                                </>
                            )}

                            <span className="type-tag">{notification.notificationsType}</span>
                            <p className="notification-content">{notification.content}</p>
                            <small className="timestamp">
                                {new Date(notification.createdAt).toLocaleString()}
                            </small>

                            <div
                                className="delete-notification-button"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleDeleteNotification(notification);
                                }}
                                title="Delete Notification"
                            >
                                <LucideTrash2 size={16} />
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default Notifications;
