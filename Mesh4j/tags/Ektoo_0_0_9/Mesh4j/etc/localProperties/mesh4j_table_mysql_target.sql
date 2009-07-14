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

DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS person_sync;

CREATE TABLE person (
  uid varchar(100) PRIMARY KEY not null,
  firstName varchar(100) not null,
  lastName varchar(100) not null);

insert into person (uid, firstName, lastName) values (2, "Sharif", "Uddin");

DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS country_sync;

CREATE TABLE country (
  uid varchar(100) PRIMARY KEY not null,
  countryName varchar(100) not null);

insert into country (uid, countryName) values (2, "Bangldesh");
insert into country (uid, countryName) values (3, "Argentina");
