create table CCM_CORE.SITES (
    DEFAULT_SITE boolean,
    DEFAULT_THEME varchar(255),
    DOMAIN_OF_SITE varchar(255),
    OBJECT_ID bigint not null,
    primary key (OBJECT_ID)
);

alter table CCM_CORE.SITES 
    add constraint UK_kou1h4y4st2m173he44yy8grx unique (DOMAIN_OF_SITE);

alter table CCM_CORE.SITES 
    add constraint FKrca95c6p023men53b8ayu26kp 
    foreign key (OBJECT_ID) 
    references CCM_CORE.CCM_OBJECTS;

