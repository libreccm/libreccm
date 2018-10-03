alter table CCM_CORE.ROLES
    add column UUID varchar(255) not null;

alter table CCM_CORE.ROLES 
    add constraint UK_rfmsjqsq6kagolsod3ufkugll unique (UUID);
