alter table user_reference add column type text;
update user_reference set type = 'FREE_USER';