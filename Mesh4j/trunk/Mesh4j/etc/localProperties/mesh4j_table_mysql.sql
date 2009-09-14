DROP TABLE IF EXISTS mesh_multi_key;

CREATE TABLE mesh_multi_key (
  id1 varchar(100) not null,
  id2 varchar(100) not null,
  name varchar(100) not null,
  PRIMARY KEY(id1, id2));
  
insert into mesh_multi_key (id1, id2, name) values ('1', '2', 'jmt');
insert into mesh_multi_key (id1, id2, name) values ('1', '1', 'bia');


DROP TABLE IF EXISTS mesh_example;
DROP TABLE IF EXISTS mesh_example_sync;

CREATE TABLE mesh_example (
  uid varchar(100) PRIMARY KEY not null,
  name varchar(100) not null,
  pass varchar(100) not null);

insert into mesh_example (uid, name, pass) values (1, "jmt", "123");

DROP TABLE IF EXISTS mesh_example_1;
DROP TABLE IF EXISTS mesh_example_1_sync;

CREATE TABLE mesh_example_1 (
  uid varchar(100) PRIMARY KEY not null,
  vString varchar(100) not null,
  vdecimal decimal(5,3) not null  ,
  vdouble double(5,3) not null,
  vfloat float(5,3) not null  
);

insert into mesh_example_1 (uid, vString, vdecimal, vdouble, vfloat) values('2', 'jmt', 3.257, 3.257, 3.257);
insert into mesh_example_1 (uid, vString, vdecimal, vdouble, vfloat) values('3', 'jmt2', 3, 3, 3);

DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_sync;

CREATE TABLE user (
  uid varchar(100) PRIMARY KEY not null,
  name varchar(100) not null,
  pass varchar(100) not null);

insert into user (uid, name, pass) values (1, "jmt", "123");

DROP TABLE IF EXISTS user2;
DROP TABLE IF EXISTS user2_sync;

CREATE TABLE user2 (
  id INTEGER PRIMARY KEY not null,
  uname varchar(100) not null,
  passw varchar(100) not null);

insert into user2 (id, uname, passw) values (1, "msu", "gsl");

DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS person_sync;

CREATE TABLE person (
  uid varchar(100) PRIMARY KEY not null,
  firstName varchar(100) not null,
  lastName varchar(100) not null);

insert into person (uid, firstName, lastName) values (1, "Saiful", "Islam");

DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS country_sync;

CREATE TABLE country (
  uid varchar(100) PRIMARY KEY not null,
  countryName varchar(100) not null);

insert into country (uid, countryName) values (1, "Cambodia");