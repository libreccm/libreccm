create table CCM_CORE.CCM_OBJECTS_AUD (
    OBJECT_ID bigint not null,
    REV integer not null,
    REVTYPE tinyint,
    REVEND integer,
    DISPLAY_NAME varchar(255),
    primary key (OBJECT_ID, REV)
);

alter table CCM_CORE.CCM_OBJECTS_AUD 
    add constraint FKr00eauutiyvocno8ckx6h9nw6 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CORE.CCM_OBJECTS_AUD 
    add constraint FKo5s37ctcdny7tmewjwv7705h5 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;
