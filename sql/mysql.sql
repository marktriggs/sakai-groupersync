create table grouper_status (setting varchar(255) PRIMARY KEY, value varchar(255));

create table grouper_groups (group_id varchar(255) PRIMARY KEY, grouper_group_id varchar(255) NOT NULL, sakai_group_id varchar(255) NOT NULL, description varchar(512) NOT NULL, deleted int default 0);
create index grouper_groups_sakai_id on grouper_groups (sakai_group_id);

create table grouper_group_users (group_id varchar(255), netid varchar(255), role varchar(30),
       PRIMARY KEY (group_id, netid),
       FOREIGN KEY (group_id) references grouper_groups (group_id));

create index grouper_groups_role on grouper_group_users (role);

create view grouper_memberships as select group_id, netid from grouper_group_users where role = 'viewer';
create view grouper_managers as select group_id, netid from grouper_group_users where role = 'manager';
