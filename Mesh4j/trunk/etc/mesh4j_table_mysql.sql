DROP TABLE mesh_sync_info;

CREATE TABLE mesh_sync_info (
  sync_id varchar(100) PRIMARY KEY not null,
  entity_name varchar(100) not null,
  entity_id varchar(100) not null,
  last_update varchar(30) not null,
  sync_data LONGTEXT);
  
  
DROP TABLE mesh_sync_example;

CREATE TABLE mesh_sync_example (
  uid varchar(100) PRIMARY KEY not null,
  name varchar(100) not null,
  pass varchar(100) not null);
