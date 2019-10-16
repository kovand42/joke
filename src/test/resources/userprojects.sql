insert into userprojects(userSkillId, projectId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test1') and
    skillId = (select skillId from skills where skillName = 'test1')),
    (select projectId from projects where projectName = 'test1'),0);
insert into userprojects(userSkillId, projectId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test1') and
    skillId = (select skillId from skills where skillName = 'test1v')),
    (select projectId from projects where projectName = 'test1'),0);
insert into userprojects(userSkillId, projectId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test2') and
    skillId = (select skillId from skills where skillName = 'test1v')),
    (select projectId from projects where projectName = 'test1'),0);
insert into userprojects(userSkillId, projectId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test3') and
    skillId = (select skillId from skills where skillName = 'test2')),
    (select projectId from projects where projectName = 'test1'),0);