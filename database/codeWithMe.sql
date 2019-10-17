set names utf8mb4;
set charset utf8mb4;
drop database if exists codeWithMe;
create database codeWithMe charset utf8mb4;
use codeWithMe;

CREATE TABLE categories (
                            categoryId int unsigned NOT NULL AUTO_INCREMENT primary key,
                            categoryName varchar(50) NOT NULL unique,
                            version int unsigned DEFAULT 0
);

INSERT INTO categories(categoryName) VALUES
('web'),
('DB'),
('java'),
('UML'),
('REST'),
('git'),
('IDE');

CREATE TABLE skills(
                       skillId int unsigned NOT NULL AUTO_INCREMENT primary key,
                       skillName varchar(50) NOT NULL unique,
                       categoryId int unsigned NOT NULL,
                       validated tinyint DEFAULT 0,
                       version int unsigned DEFAULT 0,
                       CONSTRAINT skills_categoryId FOREIGN KEY (categoryId) REFERENCES categories(categoryId)
);

INSERT INTO skills(skillName,categoryId) VALUES
('html5',1),
('css3',1),
('JavaScript',1),
('jquery',1),
('tomcat',1),
('MySQL',2),
('DataGrip',2),
('MongoDB',2),
('Maven',3),
('JUnit',3),
('spring',3),
('thymeleaf',3),
('jpa',3),
('hibernate',3),
('springSecurity',3),
('Umbrello',4),
('StarUML',4),
('XML',5),
('Json',5),
('GitHub',6),
('jenkins',6),
('IntelliJ',7),
('eclipse',7);

CREATE TABLE users (
                       id int unsigned NOT NULL AUTO_INCREMENT primary key,
                       username varchar(50) NOT NULL unique,
                       email varchar(50) NOT NULL unique,
                       password varchar(127) NOT NULL,
                       enabled tinyint default 1,
                       version int unsigned DEFAULT 0
);

INSERT INTO users(username,email,password) VALUES
('master', 'm@m', '{bcrypt}$2a$10$3DPuiwzO.I2UYggelBe8NuCHdd7Jblz2cu8K0ZkkguQZYnCIA4u5O'),
('kovand42','kovand42@gmail.com','{bcrypt}$2a$10$3DPuiwzO.I2UYggelBe8NuCHdd7Jblz2cu8K0ZkkguQZYnCIA4u5O'),
('joe','andras.kovacs@vdabcampus.be','{bcrypt}$2a$10$3DPuiwzO.I2UYggelBe8NuCHdd7Jblz2cu8K0ZkkguQZYnCIA4u5O');

CREATE TABLE userskills (
                            userSkillId int unsigned NOT NULL AUTO_INCREMENT primary key,
                            userId int unsigned NOT NULL,
                            skillId int unsigned NOT NULL,
                            validated tinyint DEFAULT 0,
                            version int unsigned DEFAULT 0,
                            CONSTRAINT userskills_userId FOREIGN KEY (userId) REFERENCES users(id),
                            CONSTRAINT userskills_skillId FOREIGN KEY (skillId) REFERENCES skills(skillId)
);

insert into userskills(userId, skillId) values
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23);
insert into userskills(userId, skillId) values
(2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(3,1);

CREATE TABLE repositories (
                              repositoryId int unsigned NOT NULL AUTO_INCREMENT primary key,
                              repositoryName varchar(50) NOT NULL,
                              version int unsigned NOT NULL DEFAULT 0,
                              url varchar(255) unique
);

insert into repositories (repositoryName, url) values ('application1', 'www.application1.be'),('application1', 'www.application2.be');

CREATE TABLE projects (
                              projectId int unsigned NOT NULL AUTO_INCREMENT primary key,
                              projectName varchar(50) NOT NULL,
                              repositoryId int unsigned NOT NULL,
                              version int unsigned DEFAULT 0,
                              CONSTRAINT applications_repositoryId FOREIGN KEY (repositoryId) REFERENCES repositories(repositoryId)
);

insert into projects (projectName, repositoryId) values ('project1_1',(select repositoryId from repositories where url = 'www.application1.be')),
                                                                ('project1_2',(select repositoryId from repositories where url = 'www.application1.be')),
                                                                ('project1_3',(select repositoryId from repositories where url = 'www.application1.be')),
                                                                ('project2_1',(select repositoryId from repositories where url = 'www.application2.be')),
                                                                ('project2_2',(select repositoryId from repositories where url = 'www.application2.be'));

CREATE TABLE projectMessages (
                                projectMessageId int unsigned NOT NULL AUTO_INCREMENT primary key,
                                projectId int unsigned NOT NULL,
                                userId int unsigned NOT NULL,
                                messageDateTime datetime NOT NULL,
                                message varchar(255) NOT NULL,
                                version int unsigned DEFAULT 0,
                                CONSTRAINT projectMessages_projectId FOREIGN KEY (projectId) REFERENCES projects(projectId),
                                CONSTRAINT projectMessages_userId FOREIGN KEY (userId) REFERENCES users(id)
);
insert into projectMessages (projectId, userId, messageDateTime, message)
values(1,2,'2019-10-16 13:32:03','ez a projekt elindult'),(1,2,'2019-10-16 13:57:24','megy is gyorsan'),(1,3,'2019-10-16 13:59:14','csuhajja de jo is ez'),(1,3,'2019-10-16 14:40:01','ilyen lett'); 
CREATE TABLE userprojects (
                                  userSkillId int unsigned NOT NULL,
                                  projectId int unsigned NOT NULL,
                                  primary key (userSkillId,projectId),
                                  version int unsigned DEFAULT 0,
                                  CONSTRAINT userprojects_userSkillId FOREIGN KEY (userSkillId) REFERENCES userskills(userSkillId),
                                  CONSTRAINT userprojects_projectId FOREIGN KEY (projectId) REFERENCES projects(projectId)
);

insert into userprojects(userSkillId, projectId) values (24,1),(31,3),(25,1),(34,4),(35,4),(34,3),(35,2),(35,5);

CREATE TABLE projectauthorities (
                                projectAuthorityId int unsigned NOT NULL AUTO_INCREMENT primary key,
                                projectAuthority varchar(50) NOT NULL,
                                version int unsigned DEFAULT 0
);

CREATE TABLE projectuserauthorities (
                                projectId int unsigned NOT NULL,
                                userId int unsigned NOT NULL,
                                PRIMARY KEY (projectId, userId),
                                projectAuthorityId int unsigned NOT NULL,
                                version int unsigned DEFAULT 0,
                                CONSTRAINT projectuserauthorities_projectId FOREIGN KEY (projectId) REFERENCES projects(projectId),
                                CONSTRAINT projectuserauthorities_userId FOREIGN KEY (userId) REFERENCES users(id),
                                CONSTRAINT projectuserauthorities_projectAuthorityId FOREIGN KEY (projectAuthorityId) REFERENCES projectauthorities(projectAuthorityId)
);
CREATE TABLE requests (
                                projectId int unsigned NOT NULL,
                                userId int unsigned NOT NULL,
                                PRIMARY KEY (projectId,userId),
                                invitation tinyint DEFAULT 0,
                                version int unsigned DEFAULT 0,
                                CONSTRAINT requests_projectId FOREIGN KEY (projectId) REFERENCES projects(projectId),
                                CONSTRAINT requests_userId FOREIGN KEY (userId) REFERENCES users(id)
);
CREATE TABLE roles (
                       roleId int unsigned NOT NULL AUTO_INCREMENT primary key,
                       roleName varchar(50) NOT NULL unique,
                       version int unsigned DEFAULT 0
);

insert into roles(roleName) values ('admin'),('user');


CREATE TABLE userroles (
                           userId int unsigned NOT NULL unique,
                           roleId int unsigned NOT NULL,
                           PRIMARY KEY (userId,roleid),
                           version int unsigned DEFAULT 0,
                           CONSTRAINT userroles_roleId FOREIGN KEY (roleId) REFERENCES roles(roleId),
                           CONSTRAINT userroles_userId FOREIGN KEY (userId) REFERENCES users(id)
);

INSERT INTO userroles (userId, roleId) VALUES (1,1),(2,2);

create user if not exists cursist identified by 'Cursist11.';
grant select,insert,update,delete on categories to cursist;
grant select,insert,update,delete on skills to cursist;
grant select,insert,update,delete on projects to cursist;
grant select,insert,update,delete on projectauthorities to cursist;
grant select,insert,update,delete on projectuserauthorities to cursist;
grant select,insert,update,delete on requests to cursist;
grant select,insert,update,delete on users to cursist;
grant select,insert,update,delete on roles to cursist;
grant select,insert,update,delete on userroles to cursist;
