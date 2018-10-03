alter table CCM_CORE.DOMAIN_OWNERSHIPS
    add column UUID varchar(255) not null;

alter table CCM_CORE.DOMAIN_OWNERSHIPS 
    add constraint UK_j86gai9740v9hshascbsboudb unique (UUID);



