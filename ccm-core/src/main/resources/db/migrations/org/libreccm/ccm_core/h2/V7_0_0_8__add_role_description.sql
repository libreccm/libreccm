create table CCM_CORE.ROLE_DESCRIPTIONS (
        ROLE_ID bigint not null,
        LOCALIZED_VALUE longvarchar,
        LOCALE varchar(255) not null,
        primary key (ROLE_ID, LOCALE)
    );

alter table CCM_CORE.ROLE_DESCRIPTIONS 
        add constraint FKo09bh4j3k3k0ph3awvjwx31ft 
        foreign key (ROLE_ID) 
        references CCM_CORE.CCM_ROLES;
