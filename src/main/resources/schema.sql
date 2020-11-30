create table users(username varchar(30),password varchar(30));
create table userRoles(username varchar(30), role varchar(30));
create table projectRoles(projectName varchar(30),username varchar(30), role varchar(30));
create table permissions(endpoint varchar(100), role varchar(30));
create table endpointRoleType(endpoint varchar(100),roleType varchar(30));

insert into users(username,password) values('vamsi@tcs.com','Password@12345');
insert into users(username,password) values('kavitha@tcs.com','Password@12345');
insert into users(username,password) values('bansi@tcs.com','Password@12345');

insert into userRoles values('vamsi@tcs.com','ORG_ADMIN');
insert into userRoles values('bansi@tcs.com','ORG_ASSOCIATE');
insert into userRoles values('kavitha@tcs.com','ORG_ASSOCIATE');

insert into projectRoles values('java-project','vamsi@tcs.com','PROJECT_OWNER');
insert into projectRoles values('java-project','bansi@tcs.com','PROJECT_EDITOR');
insert into projectRoles values('java-project','kavitha@tcs.com','PROJECT_VIEWER');

insert into permissions values('/project/create','ORG_ADMIN');
insert into permissions values('/project/delete','PROJECT_OWNER');
insert into permissions values('/function/create','PROJECT_OWNER');
insert into permissions values('/function/create','PROJECT_EDITOR');

insert into endpointRoleType values('/project/create','ORG');
insert into endpointRoleType values('/project/delete','PROJECT');
insert into endpointRoleType values('/function/create','PROJECT');

