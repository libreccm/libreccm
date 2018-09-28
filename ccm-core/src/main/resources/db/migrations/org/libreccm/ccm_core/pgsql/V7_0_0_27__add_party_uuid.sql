alter table CCM_CORE.PARTIES
    add column UUID varchar(255) not null;

alter table CCM_CORE.PARTIES 
    add constraint UK_1hv061qace2mn4loroe3fwdel unique (UUID);