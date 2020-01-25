INSERT INTO my_user(user_id, gender, level, major, card_photo_path, checked, user_identity, nickname, avatar, enterprise_type_id) VALUES
('openid_1', 'male', 2017, 1, '/joinintern/media-dev/card/1.png', 'pass', 'stu', '力大猪飞', '#', 1),
('openid_2', 'female', 2017, 1, '/joinintern/media-dev/card/2.png', 'pass', 'stu', '两千万', '#', 1),
('openid_3', 'male', 2017, 2, '/joinintern/media-dev/card/3.png', 'pass', 'stu', '神秘用户', '#', 2),
('openid_4', 'male', 2019, 3, '/joinintern/media-dev/card/4.png', 'pass', 'admin', '神秘管理员', '#', 3);

INSERT INTO message(has_read, message_time, message_content, sender_id, receiver_id) VALUES
(false, '2019-12-31 20:00', 'Something important', 'openid_1', 'openid_2'),
(true, '2019-12-30 20:00', 'Something important', 'openid_2', 'openid_3'),
(false, '2020-1-1 21:00', 'Something important', 'openid_3', 'openid_4'),
(false, '2020-1-20 22:00', 'Something important', 'openid_4', 'openid_1');

INSERT INTO post(duration, location, distanceZB, distanceMH, post_content, completed, expiration, author_id, start_time, end_time) VALUES
(30, '金沙江路10086号', 0.8, 1.6, '招聘程序员一名', false, '2020-2-1', 'openid_1', '2020-1-1', '2020-1-10'),
(60, '金沙江路10010号', 1.0, 1.8, '招聘程序员一名', false, '2020-1-1', 'openid_2', '2020-1-1', '2020-1-10'),
(80, '金沙江路996号', 2, 2.0, '招聘程序员一名', false, '2020-2-2', 'openid_3', '2020-1-1', '2020-2-2'),
(20, '金沙江路251号', 3.1, 3.3, '招聘程序员一名', true, '2020-3-1', 'openid_4', '2020-3-1', '2020-3-3');

INSERT INTO video(video_description, video_path, checked, poster_id, checker_id, post_date, check_date) VALUES
('金沙江路招聘挖路员视频', '/joinintern/media-dev/video/1.mp4', true, 'openid_1', 'openid_4', '2020-1-1', '2020-2-1'),
('金沙江路招聘管道工视频', '/joinintern/media-dev/video/2.mp4', true, 'openid_2', 'openid_4', '2020-1-10', '2020-1-2'),
('金沙江路招聘勘探员视频', '/joinintern/media-dev/video/3.mp4', false, 'openid_3', null, '2020-1-1', '2020-1-1'),
('金沙江路招聘监工视频', '/joinintern/media-dev/video/4.mp4', true, 'openid_1', 'openid_4', '2020-1-20', '2020-1-21');

INSERT INTO video_click(video_click_time, clicker_id, video_id) VALUES
('2020-1-1', 'openid_1', 1),
('2020-1-1', 'openid_2', 3),
('2020-1-1', 'openid_3', 2);

INSERT INTO post_click(post_click_time, clicker_id, post_id) VALUES
('2020-1-10', 'openid_1', 2),
('2020-1-11', 'openid_3', 3),
('2020-1-15', 'openid_2', 1),
('2020-1-17', 'openid_1', 3),
('2020-1-20', 'openid_2', 2),
('2020-1-10', 'openid_1', 1);

INSERT INTO post_label(post_id, label_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 1),
(2, 2),
(2, 4),
(3, 1),
(3, 2),
(4, 3),
(4, 4);

INSERT INTO post_major(post_id, major_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(2, 2),
(2, 3),
(2, 4),
(3, 1),
(3, 2);