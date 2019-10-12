insert into userapplications(userSkillId, applicationId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test1') and
    skillId = (select skillId from skills where skillName = 'test1')),
    (select applicationId from applications where applicationName = 'test1'),0);
insert into userapplications(userSkillId, applicationId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test1') and
    skillId = (select skillId from skills where skillName = 'test1v')),
    (select applicationId from applications where applicationName = 'test1'),0);
insert into userapplications(userSkillId, applicationId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test2') and
    skillId = (select skillId from skills where skillName = 'test1v')),
    (select applicationId from applications where applicationName = 'test1'),0);
insert into userapplications(userSkillId, applicationId, version)
values ((select userSkillId from userskills where
    userId = (select id from users where username = 'test3') and
    skillId = (select skillId from skills where skillName = 'test2')),
    (select applicationId from applications where applicationName = 'test1'),0);