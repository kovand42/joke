insert into userskills(userId, skillId, version)
values((select id from users where username = 'test1'),
    (select skillId from skills where skillName = 'test1'),0);
insert into userskills(userId, skillId, version)
values((select id from users where username = 'test1'),
    (select skillId from skills where skillName = 'test1v'),0);
insert into userskills(userId, skillId, version)
values((select id from users where username = 'test1'),
       (select skillId from skills where skillName = 'test2'),0);
insert into userskills(userId, skillId, version)
values((select id from users where username = 'test2'),
       (select skillId from skills where skillName = 'test1'),0);
insert into userskills(userId, skillId, version)
values((select id from users where username = 'test2'),
       (select skillId from skills where skillName = 'test1v'),0);
insert into userskills(userId, skillId, version)
values((select id from users where username = 'test3'),
       (select skillId from skills where skillName = 'test2'),0);