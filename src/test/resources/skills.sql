insert into skills(skillName, categoryId, validated, version)
values('test1', (select categoryId from categories where categoryName = 'test1'), false, 0);
insert into skills(skillName, categoryId, validated, version)
values('test1v', (select categoryId from categories where categoryName = 'test2'), false, 0);
insert into skills(skillName, categoryId, validated, version)
values('test2', (select categoryId from categories where categoryName = 'test2'), false, 0);