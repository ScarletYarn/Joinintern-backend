INSERT INTO major(major_name) VALUES
('工学'),
('管理学'),
('经济学'),
('理学'),
('教育学'),
('文学'),
('历史学'),
('哲学'),
('法学'),
('艺术学'),
('其它');

INSERT INTO enterprise_type(enterprise_type_name) VALUES
('国有企业'),
('中央企业'),
('私营企业'),
('外资企业'),
('合资企业');

INSERT INTO label(label_content) VALUES
('软件开发'),
('UI设计'),
('产品经理'),
('时长较短'),
('时长中等'),
('时长较长'),
('计算机行业'),
('信息行业'),
('食品行业'),
('教育行业'),
('卫生行业'),
('建筑行业'),
('传媒公司'),
('算法设计');

INSERT INTO my_user(user_id, gender, level, major, card_photo_path, validation, user_identity, nickname, avatar,
                    student_id, enterprise_type_id, description)
VALUES ('ultra master', 'male', 2020, 1,
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633604375&di=5d0ee76abdf8b08fbb170339566fcb5d&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201804%2F02%2F20180402003053_vKaQw.thumb.700_0.jpeg',
        'validate', 'admin', '超级管理员',
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633604375&di=5d0ee76abdf8b08fbb170339566fcb5d&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201804%2F02%2F20180402003053_vKaQw.thumb.700_0.jpeg',
        '100004', 1, '描述');