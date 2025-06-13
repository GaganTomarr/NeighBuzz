	DROP DATABASE IF EXISTS LCP_DB;
	
	CREATE DATABASE LCP_DB;
	USE LCP_DB;
	
	CREATE TABLE users (
	    user_id INT PRIMARY KEY AUTO_INCREMENT,
	    email VARCHAR(255) NOT NULL UNIQUE,
	    username VARCHAR(50) NOT NULL UNIQUE,
	    password_hash VARCHAR(255) NOT NULL,
	    registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    last_login DATETIME,
	    account_status ENUM('ACTIVE', 'SUSPENDED', 'INACTIVE', 'DELETED') DEFAULT 'ACTIVE',
	    is_admin BOOLEAN NOT NULL DEFAULT FALSE
	);
	
	CREATE TABLE user_profiles (
	    profile_id INT PRIMARY KEY AUTO_INCREMENT,
	    user_id INT NOT NULL UNIQUE,
	    display_name VARCHAR(100),
	    profile_picture VARCHAR(255),
	    bio TEXT,
	    location VARCHAR(100),
	    profile_visibility ENUM('PUBLIC', 'PRIVATE') NOT NULL DEFAULT 'PUBLIC',
	    contact_type ENUM('EMAIL', 'SMS', 'MOBILENO', 'NONE') NOT NULL DEFAULT 'NONE',
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	
	CREATE TABLE login_history (
	    login_id INT PRIMARY KEY AUTO_INCREMENT,
	    user_id INT NOT NULL,
	    login_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    login_status ENUM('SUCCESS', 'FAILED', 'LOCKOUT') NOT NULL,
	    ip_address VARCHAR(45),
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	
	
	CREATE TABLE news_posts (
	    post_id INT PRIMARY KEY AUTO_INCREMENT,
	    author_id INT NOT NULL,
	    title VARCHAR(255) NOT NULL,
	    content TEXT NOT NULL,
	    featured_image VARCHAR(255),
	    excerpt VARCHAR(255),
	    location ENUM ('GENERAL', 'MUMBAI', 'DELHI', 'BENGALURU', 'HYDERABAD', 'AHMEDABAD', 'CHENNAI', 'KOLKATA', 'SURAT', 'PUNE', 'JAIPUR') NOT NULL DEFAULT 'GENERAL',
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	    published_at DATETIME,
	    is_approved BOOLEAN DEFAULT FALSE,
	    approval_user_id INT,
	    news_category ENUM('EDUCATIONAL', 'CRIME', 'GEOPOLITICS', 'NATIONAL', 'SPORTS', 'HEALTH', 'LIFESTYLE', 'EDITORIALS', 'ENTERTAINMENT', 'TECHNOLOGY', 'BUSINESS', 'SPIRITUAL', 'CULTURE'),
	    FOREIGN KEY (author_id) REFERENCES users(user_id),
	    FOREIGN KEY (approval_user_id) REFERENCES users(user_id)
	);
	
	CREATE TABLE news_post_comments (
	    comment_id INT PRIMARY KEY AUTO_INCREMENT,
	    content_id INT NOT NULL,
	    user_id INT NOT NULL,
	    comment_text TEXT NOT NULL,
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	    parent_comment_id INT,
	    is_approved BOOLEAN DEFAULT TRUE,
	    FOREIGN KEY (content_id) REFERENCES news_posts(post_id) ON DELETE CASCADE,
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	    FOREIGN KEY (parent_comment_id) REFERENCES news_post_comments(comment_id) ON DELETE SET NULL
	);
	
	CREATE TABLE events (
	    event_id INT PRIMARY KEY AUTO_INCREMENT,
	    organizer_id INT NOT NULL,
	    title VARCHAR(255) NOT NULL,
	    description TEXT NOT NULL,
	    location ENUM ('GENERAL', 'MUMBAI', 'DELHI', 'BENGALURU', 'HYDERABAD', 'AHMEDABAD', 'CHENNAI', 'KOLKATA', 'SURAT', 'PUNE', 'JAIPUR') NOT NULL DEFAULT 'GENERAL',
	    event_date DATE NOT NULL,
	    start_time TIME NOT NULL,
	    end_time TIME,
	    capacity INT,
	    registration_deadline DATETIME,
	    is_published BOOLEAN DEFAULT FALSE,
	    featured_image VARCHAR(255),
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	    events_category ENUM ('SEMINAR', 'BOOKMEET', 'HEALTHCAMP', 'NETWORKING', 'FESTIVAL', 'ENTERTAINMENT', 'WORKSHOPS', 'EDUCATIONAL', 'RECREATIONAL'),
	    FOREIGN KEY (organizer_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	
	CREATE TABLE event_registrations (
	    registration_id INT PRIMARY KEY AUTO_INCREMENT,
	    event_id INT NOT NULL,
	    user_id INT NOT NULL,
	    registration_status ENUM('REGISTERED', 'WAITLISTED', 'CANCELLED') DEFAULT 'REGISTERED',
	    registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    cancellation_date DATETIME,
	    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	    UNIQUE (event_id, user_id)
	);
	
	CREATE TABLE forum (
	    forum_id INT PRIMARY KEY AUTO_INCREMENT,
	    user_id INT NOT NULL,
	    forum_description TEXT NOT NULL,
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	    forum_category ENUM ('GENERAL', 'EDUCATIONAL', 'TECHNOLOGY', 'ENTERTAINMENT', 'LIFESTYLE', 'NEWS', 'HELP_AND_SUPPORT', 'FAITH', 'ENVIRONMENT'),
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	
	CREATE TABLE forum_threads (
	    thread_id INT PRIMARY KEY AUTO_INCREMENT,
	    forum_id INT ,
	    title VARCHAR(255) NOT NULL,
	    creator_id INT NOT NULL,
		forum_category ENUM('GENERAL'),
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	    is_pinned BOOLEAN DEFAULT FALSE,
	    is_locked BOOLEAN DEFAULT FALSE,
	    view_count INT DEFAULT 0,
	    featured_image VARCHAR(255),
	    description TEXT NOT NULL,
	    FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE,
	    FOREIGN KEY (forum_id) REFERENCES forum(forum_id) ON DELETE CASCADE
	);
	
	CREATE TABLE forum_thread_comments (
	    ft_comment_id INT PRIMARY KEY AUTO_INCREMENT,
	    thread_id INT NOT NULL,
	    user_id INT NOT NULL,
	    comment_text TEXT NOT NULL,
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	    parent_comment_id INT,
	    FOREIGN KEY (thread_id) REFERENCES forum_threads(thread_id) ON DELETE CASCADE,
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	    FOREIGN KEY (parent_comment_id) REFERENCES forum_thread_comments(ft_comment_id) ON DELETE SET NULL
	);
	
	CREATE TABLE surveys (
	    survey_id INT PRIMARY KEY AUTO_INCREMENT,
	    creator_id INT NOT NULL,
	    title VARCHAR(255) NOT NULL,
	    description TEXT,
	    start_date DATETIME NOT NULL,
	    end_date DATETIME NOT NULL,
	    is_anonymous BOOLEAN DEFAULT FALSE,
	    is_published BOOLEAN DEFAULT TRUE,
	    survey_type ENUM('NEWS', 'DISCUSSION', 'FEEDBACK', 'EVENTS', 'CONTENT') DEFAULT 'FEEDBACK',
	    FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	
	CREATE TABLE survey_questions (
		question_id INT PRIMARY KEY AUTO_INCREMENT,
		survey_id INT NOT NULL,
		question_text TEXT NOT NULL,
		is_required BOOLEAN ,
	    options TEXT NOT NULL, 
	    type ENUM('MULTIPLE_CHOICE','DROPDOWN'),
	    FOREIGN KEY (survey_id) REFERENCES surveys(survey_id) ON DELETE CASCADE
	);
	
	CREATE TABLE survey_responses (
		response_id INT PRIMARY KEY AUTO_INCREMENT,
		survey_id INT NOT NULL,
		question_id INT NOT NULL,
		user_id INT NOT NULL,
		submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
		answer TEXT NOT NULL,
		CONSTRAINT fk_survey FOREIGN KEY (survey_id) REFERENCES surveys(survey_id) ON DELETE CASCADE,
		CONSTRAINT fk_question FOREIGN KEY (question_id) REFERENCES survey_questions(question_id) ON DELETE CASCADE,
		CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
		CONSTRAINT uq_response UNIQUE (survey_id, question_id, user_id) -- Optional: enforce one answer per question per user
	);
	
	CREATE TABLE survey_result(
		survey_result_id INT PRIMARY KEY AUTO_INCREMENT,
		survey_id INT NOT NULL,
		result TEXT,
		FOREIGN KEY (survey_id) REFERENCES surveys(survey_id)
	);
	
	CREATE TABLE moderation_log (
	    log_id INT PRIMARY KEY AUTO_INCREMENT,
	    admin_id INT NOT NULL,
	    content_type ENUM('POST', 'COMMENT', 'THREAD', 'EVENT', 'SURVEY', 'USER') NOT NULL,
	    content_id INT NOT NULL,
	    action_type ENUM('APPROVE', 'REJECT', 'DELETE', 'SUSPEND', 'RESTORE') NOT NULL,
	    reason TEXT,
	    action_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    FOREIGN KEY (admin_id) REFERENCES users(user_id)
	);
	
	CREATE TABLE user_activity (
	    activity_id INT PRIMARY KEY AUTO_INCREMENT,
	    user_id INT NOT NULL,
	    activity_type ENUM('LOGIN', 'POST_CREATED', 'COMMENT', 'RSVP', 'FORUM_POST', 'SURVEY_SUBMIT', 'EVENT_CREATED') NOT NULL,
	    entity_type ENUM('POST', 'EVENT', 'THREAD', 'SURVEY', 'COMMENT', 'SYSTEM') NOT NULL,
	    entity_id INT NOT NULL,
	    occurred_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	
	CREATE TABLE notifications (
	    notification_id INT PRIMARY KEY AUTO_INCREMENT,
	    user_id INT NOT NULL,
	    notification_type ENUM('EVENT_UPDATE', 'SYSTEM', 'EVENTS_ADDED', 'COMMENT_REPLY', 'FORUM_THREAD_DISCUSSION') NOT NULL,
	    content TEXT NOT NULL,
	    related_entity_type ENUM('EVENT', 'FORUM_THREAD_DISCUSSION', 'SYSTEM', 'NEWS_POST_COMMENT_REPLY'),
	    related_entity_id INT,
	    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	    post_id INT NOT NULL,
	    is_read BOOLEAN NOT NULL DEFAULT FALSE,
	    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
	);
	