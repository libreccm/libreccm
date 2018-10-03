alter table CCM_CORE.ROLE_MEMBERSHIPS
    add column UUID varchar(255) not null;

alter table CCM_CORE.ROLE_MEMBERSHIPS 
    add constraint UK_82wdq214bfs99eii71fp50s97 unique (UUID);


