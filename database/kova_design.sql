set names utf8mb4;
set charset utf8mb4;
drop database if exists kova_design;
create database kova_design charset utf8mb4;
use kova_design;

CREATE TABLE categories (
                        categoryId int unsigned NOT NULL AUTO_INCREMENT primary key,
                        categoryName varchar(50) NOT NULL unique,
                        version int unsigned NOT NULL DEFAULT 0
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
                          version int unsigned NOT NULL DEFAULT 0,
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
                       enabled tinyint NOT NULL default 1,
                       version int unsigned NOT NULL DEFAULT 0
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
                            version int unsigned NOT NULL DEFAULT 0,
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
                            url varchar(255) NOT NULL unique
);

insert into repositories (repositoryName, url) values ('application1', 'www.application1.be'),('application1', 'www.application2.be');

CREATE TABLE applications (
                            applicationId int unsigned NOT NULL AUTO_INCREMENT primary key,
                            applicationName varchar(50) NOT NULL,
                            repositoryId int unsigned NOT NULL,
                            version int unsigned NOT NULL DEFAULT 0,
                            CONSTRAINT applications_repositoryId FOREIGN KEY (repositoryId) REFERENCES repositories(repositoryId)
);

insert into applications (applicationName, repositoryId) values ('application1_1',(select repositoryId from repositories where url = 'www.application1.be')),
                                                                ('application1_2',(select repositoryId from repositories where url = 'www.application1.be')),
                                                                ('application1_3',(select repositoryId from repositories where url = 'www.application1.be')),
                                                                ('application2_1',(select repositoryId from repositories where url = 'www.application2.be')),
                                                                ('application2_2',(select repositoryId from repositories where url = 'www.application2.be'));

CREATE TABLE userapplications (
                            userSkillId int unsigned NOT NULL,
                            applicationId int unsigned NOT NULL,
                            primary key (userSkillId,applicationId),
                            version int unsigned NOT NULL DEFAULT 0,
                            CONSTRAINT userapplications_userSkillId FOREIGN KEY (userSkillId) REFERENCES userskills(userSkillId),
                            CONSTRAINT userapplications_applicationId FOREIGN KEY (applicationId) REFERENCES applications(applicationId)
);

insert into userapplications(userSkillId, applicationId) values (1,1),(8,3),(2,1),(11,4),(12,4),(11,3),(12,2),(12,5);

CREATE TABLE roles (
                        roleId int unsigned NOT NULL AUTO_INCREMENT primary key,
                        roleName varchar(50) NOT NULL unique,
                        version int unsigned NOT NULL DEFAULT 0
);

insert into roles(roleName) values ('admin'),('user');


CREATE TABLE userroles (
                                  userId int unsigned NOT NULL unique,
                                  roleId int unsigned NOT NULL,
                                  PRIMARY KEY (userId,roleid),
                                  version int unsigned NOT NULL DEFAULT 0,
                                  CONSTRAINT userroles_roleId FOREIGN KEY (roleId) REFERENCES roles(roleId),
                                  CONSTRAINT userroles_userId FOREIGN KEY (userId) REFERENCES users(id)
);

INSERT INTO userroles (userId, roleId) VALUES (1,1),(2,2);

create user if not exists cursist identified by 'Cursist11.';
grant select,insert,update,delete on categories to cursist;
grant select,insert,update,delete on skills to cursist;
grant select on users to cursist;
grant select on roles to cursist;
grant select on userroles to cursist;
