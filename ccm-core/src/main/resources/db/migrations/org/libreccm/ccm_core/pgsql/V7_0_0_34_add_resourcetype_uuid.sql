alter table CCM_CORE.RESOURCE_TYPES
    add column UUID varchar(255) not null;

alter table CCM_CORE.RESOURCE_TYPES 
    add constraint UK_ioax2ix2xmq3nw7el5k6orggb unique (UUID);





