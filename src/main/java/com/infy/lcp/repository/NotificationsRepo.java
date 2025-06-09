package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.infy.lcp.entity.Notifications;

public interface NotificationsRepo extends JpaRepository<Notifications, Integer>{
	@Query("Select n from Notifications n where n.user.userId=?1")
	List<Notifications> allNotificationsOfUser(Integer userId);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Notifications n SET n.isRead = TRUE WHERE n.notificationId = :notificationId")
	void markAsRead(@Param("notificationId") Integer notificationId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Notifications n SET n.isRead = TRUE WHERE n.user.userId = :userId")
	void markAllAsRead(@Param("userId") Integer userId);


}