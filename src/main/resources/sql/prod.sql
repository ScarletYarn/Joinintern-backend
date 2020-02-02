CREATE TABLE major (
    major_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    major_name VARCHAR(100)
);

CREATE TABLE enterprise_type (
    enterprise_type_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    enterprise_type_name VARCHAR(100)
);

CREATE TABLE my_user (
    user_id VARCHAR (100) PRIMARY KEY ,
    -- male, female
    gender VARCHAR (10) ,
    level INTEGER ,
    major INTEGER ,
    card_photo_path VARCHAR (300) ,
    -- validate, unvalidated, invalidate
    checked VARCHAR (100) ,
    -- stu for plain user, admin for administrator
    user_identity VARCHAR (10) ,
    nickname VARCHAR (50) ,
    avatar VARCHAR (200) ,
    enterprise_type_id INTEGER ,
    FOREIGN KEY(enterprise_type_id) REFERENCES enterprise_type(enterprise_type_id) ,
    FOREIGN KEY(major) REFERENCES major(major_id)
);

CREATE TABLE message (
    message_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    has_read BOOLEAN ,
    message_time DATE ,
    message_content VARCHAR (1000) ,
    sender_id VARCHAR (100) ,
    receiver_id VARCHAR (100) ,
    FOREIGN KEY(sender_id) REFERENCES my_user(user_id) ,
    FOREIGN KEY(receiver_id) REFERENCES my_user(user_id)
);

CREATE TABLE post (
    post_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    -- in month
    duration INTEGER ,
    location VARCHAR (500) ,
    -- in km
    distanceZB FLOAT ,
    distanceMH FLOAT ,
    post_content VARCHAR (1000) ,
    completed BOOLEAN ,
    expiration DATE ,
    author_id VARCHAR (100) ,
    start_time DATE ,
    end_time DATE ,
    FOREIGN KEY(author_id) REFERENCES my_user(user_id)
);

CREATE TABLE label (
    label_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    label_content VARCHAR (500)
);

CREATE TABLE video (
    video_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    video_description VARCHAR (500) ,
    video_path VARCHAR (1000) ,
    checked BOOLEAN ,
    poster_id VARCHAR (100) ,
    checker_id VARCHAR (100) ,
    FOREIGN KEY(poster_id) REFERENCES my_user(user_id) ,
    FOREIGN KEY(checker_id) REFERENCES my_user(user_id)
);

CREATE TABLE video_click (
    video_click_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    video_click_time DATE ,
    clicker_id VARCHAR (100) ,
    video_id INTEGER ,
    FOREIGN KEY(clicker_id) REFERENCES my_user(user_id) ,
    FOREIGN KEY(video_id) REFERENCES video(video_id)
);

CREATE TABLE post_click (
    post_click_id INTEGER PRIMARY KEY AUTO_INCREMENT ,
    post_click_time DATE ,
    clicker_id VARCHAR (100) ,
    post_id INTEGER ,
    FOREIGN KEY(clicker_id) REFERENCES my_user(user_id) ,
    FOREIGN KEY(post_id) REFERENCES post(post_id)
);

CREATE TABLE post_label (
    post_id INTEGER ,
    label_id INTEGER ,
    FOREIGN KEY(post_id) REFERENCES post(post_id) ,
    FOREIGN KEY(label_id) REFERENCES label(label_id)
);

CREATE TABLE post_major (
    post_id INTEGER ,
    major_id INTEGER ,
    FOREIGN KEY(post_id) REFERENCES post(post_id) ,
    FOREIGN KEY(major_id) REFERENCES major(major_id)
);
