    create table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS (
        COMPONENT_MODEL_ID bigint not null,
        CLASS_ATTRIBUTE varchar(512),
        ID_ATTRIBUTE varchar(255),
        COMPONENT_KEY varchar(255),
        STYLE_ATTRIBUTE varchar(1024),
        UUID varchar(255) not null,
        PAGE_MODEL_ID bigint,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CORE.PAGE_MODEL_DESCRIPTIONS (
        PAGE_MODEL_ID bigint not null,
        LOCALIZED_VALUE longvarchar,
        LOCALE varchar(255) not null,
        primary key (PAGE_MODEL_ID, LOCALE)
    );

    create table CCM_CORE.PAGE_MODEL_TITLES (
        PAGE_MODEL_ID bigint not null,
        LOCALIZED_VALUE longvarchar,
        LOCALE varchar(255) not null,
        primary key (PAGE_MODEL_ID, LOCALE)
    );

    create table CCM_CORE.PAGE_MODELS (
        PAGE_MODEL_ID bigint not null,
        NAME varchar(255),
        TYPE varchar(255) not null,
        UUID varchar(255) not null,
        VERSION varchar(255) not null,
        APPLICATION_ID bigint,
        primary key (PAGE_MODEL_ID)
    );

    alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS 
        add constraint FKo696ch035fe7rrueol1po13od 
        foreign key (PAGE_MODEL_ID) 
        references CCM_CORE.PAGE_MODELS;

    alter table CCM_CORE.PAGE_MODEL_DESCRIPTIONS 
        add constraint FKcc5d6eqxu1369k8ycyyt6vn3e 
        foreign key (PAGE_MODEL_ID) 
        references CCM_CORE.PAGE_MODELS;

    alter table CCM_CORE.PAGE_MODEL_TITLES 
        add constraint FKj14q9911yhd4js9p6rs21rwjf 
        foreign key (PAGE_MODEL_ID) 
        references CCM_CORE.PAGE_MODELS;

    alter table CCM_CORE.PAGE_MODELS 
        add constraint FKk2lihllrxj89mn3tqv43amafe 
        foreign key (APPLICATION_ID) 
        references CCM_CORE.APPLICATIONS;
