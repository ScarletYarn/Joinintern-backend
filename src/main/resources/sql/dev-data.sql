INSERT INTO my_user(user_id, gender, level, major, card_photo_path, validation, user_identity, nickname, avatar,
                    student_id, enterprise_type_id)
VALUES ('openid_1', 'male', 2017, 1,
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633463754&di=dddfcef89c5d12cb63d25b80beea3c61&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farticle%2F39a4baa194eb9da3bf1cee2f258ce4f118d7a1ba.png',
        'validate', 'stu', '力大猪飞',
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633463754&di=dddfcef89c5d12cb63d25b80beea3c61&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farticle%2F39a4baa194eb9da3bf1cee2f258ce4f118d7a1ba.png',
        '100000', 1),
       ('openid_2', 'female', 2017, 1,
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633495177&di=b9a2658a0f315f210a561176f2d5d736&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fmw690%2Faae48400gw1eyc6u27hefj20ed09kgmz.jpg',
        'validate', 'stu', '两千万',
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633495177&di=b9a2658a0f315f210a561176f2d5d736&imgtype=0&src=http%3A%2F%2Fww2.sinaimg.cn%2Fmw690%2Faae48400gw1eyc6u27hefj20ed09kgmz.jpg',
        '100001', 1),
       ('openid_3', 'male', 2017, 2,
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633528449&di=1b6813795e8da758206b3d85eb5014e0&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201901%2F26%2F20190126011522_rpbot.thumb.700_0.jpg',
        'validate', 'stu', '七七',
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633528449&di=1b6813795e8da758206b3d85eb5014e0&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201901%2F26%2F20190126011522_rpbot.thumb.700_0.jpg',
        '100002', 2),
       ('openid_4', 'male', 2019, 3,
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633565433&di=b2f2996ab499ca223d1f8cb26f4a67a6&imgtype=0&src=http%3A%2F%2Fn1.itc.cn%2Fimg8%2Fwb%2Fsmccloud%2F2015%2F06%2F17%2F143452673836349385.JPEG',
        'validate', 'admin', 'Akihi',
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633565433&di=b2f2996ab499ca223d1f8cb26f4a67a6&imgtype=0&src=http%3A%2F%2Fn1.itc.cn%2Fimg8%2Fwb%2Fsmccloud%2F2015%2F06%2F17%2F143452673836349385.JPEG',
        '100003', 3),
       ('ultra master', 'male', 2020, 1,
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633604375&di=5d0ee76abdf8b08fbb170339566fcb5d&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201804%2F02%2F20180402003053_vKaQw.thumb.700_0.jpeg',
        'validate', 'admin', '超级管理员',
        'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1580633604375&di=5d0ee76abdf8b08fbb170339566fcb5d&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201804%2F02%2F20180402003053_vKaQw.thumb.700_0.jpeg',
        '100004', 1);

INSERT INTO message(has_read, message_time, message_content, sender_id, receiver_id)
VALUES (false, '2019-12-31 20:00', 'Something important', 'openid_1', 'openid_2'),
       (true, '2019-12-30 20:00', 'Something important', 'openid_2', 'openid_3'),
       (false, '2020-1-1 21:00', 'Something important', 'openid_3', 'openid_4'),
       (false, '2020-1-20 22:00', 'Something important', 'openid_4', 'openid_1');

INSERT INTO post(post_title, duration, location, distanceZB, distanceMH, post_content, completed, expiration, author_id,
                 start_time, end_time, post_date)
VALUES ('程序员1', 30, '金沙江路10086号', 0.8, 1.6, '招聘程序员一名', false, '2020-2-1', 'openid_1', '2020-1-1', '2020-1-10', '2020-01-01'),
       ('程序员2', 60, '金沙江路10010号', 1.0, 1.8, '招聘程序员一名', false, '2020-1-1', 'openid_2', '2020-1-1', '2020-1-10', '2020-01-01'),
       ('程序员3', 80, '金沙江路996号', 2, 2.0, '招聘程序员一名', false, '2020-2-2', 'openid_3', '2020-1-1', '2020-2-2', '2020-01-01'),
       ('程序员4', 20, '金沙江路251号', 3.1, 3.3, '招聘程序员一名', true, '2020-3-1', 'openid_4', '2020-3-1', '2020-3-3', '2020-01-01');

INSERT INTO video(video_title, video_description, video_path, validation, poster_id, validator_id, post_date,
                  validate_date)
VALUES ('金沙江路招聘挖路员', '金沙江路招聘挖路员视频', 'http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4', 'validate', 'openid_1',
        'openid_4', '2020-1-1', '2020-2-1'),
       ('金沙江路招聘管道工', '金沙江路招聘管道工视频', 'http://vjs.zencdn.net/v/oceans.mp4', 'validate', 'openid_2', 'openid_4',
        '2020-1-10', '2020-1-2'),
       ('金沙江路招聘勘探员', '金沙江路招聘勘探员视频', 'https://media.w3.org/2010/05/sintel/trailer.mp4', 'unvalidated', 'openid_3', null,
        '2020-1-1', '2020-1-1'),
       ('金沙江路招聘监工', '金沙江路招聘监工视频', 'http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4', 'unvalidated',
        'openid_1', 'openid_4', '2020-1-20', '2020-1-21');

INSERT INTO video_hit(video_hit_time, hitter_id, video_id)
VALUES ('2020-1-1', 'openid_1', 1),
       ('2020-1-1', 'openid_2', 3),
       ('2020-1-1', 'openid_3', 2);

INSERT INTO post_hit(post_hit_time, hitter_id, post_id)
VALUES ('2020-1-10', 'openid_1', 2),
       ('2020-1-11', 'openid_3', 3),
       ('2020-1-15', 'openid_2', 1),
       ('2020-1-17', 'openid_1', 3),
       ('2020-1-20', 'openid_2', 2),
       ('2020-1-10', 'openid_1', 1);

INSERT INTO post_label(post_id, label_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2),
       (2, 4),
       (3, 1),
       (3, 2),
       (4, 3),
       (4, 4);

INSERT INTO post_major(post_id, major_id)
VALUES (1, 1),
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