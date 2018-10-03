alter table CCM_CORE.PERMISSIONS
    add column UUID varchar(255) not null;

alter table CCM_CORE.PERMISSIONS 
    add constraint UK_p50se7rdexv7xnkiqsl6ijyti unique (UUID);

