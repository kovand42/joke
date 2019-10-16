insert into projects(projectName, repositoryId, version)
values ('test1', (select repositories.repositoryId from repositories where repositoryName = 'test1'), 0);
insert into projects(projectName, repositoryId, version)
values ('test1v', (select repositories.repositoryId from repositories where repositoryName = 'test1'), 0);
insert into projects(projectName, repositoryId, version)
values ('test2', (select repositories.repositoryId from repositories where repositoryName = 'test2'), 0);