-- Remove obsolete tables

alter table CCM_CORE.FLEX_LAYOUT_BOXES 
    drop constraint FKeiqh69t1lr7u09hjuxfyxsbs;

alter table CCM_CORE.FLEX_LAYOUT_BOXES
    drop constraint FKmrobhhqidcf1657ugcgatrd0y;

alter table CCM_CORE.FLEX_LAYOUT_COMPONENTS 
    drop constraint FK8qxnqt75ikxtedx0xreoeiygg;

drop table CCM_CORE.FLEX_LAYOUT_COMPONENTS;

drop table CCM_CORE.FLEX_LAYOUT_BOXES;

-- Create new tables and columns

alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS 
    add column CONTAINER_ID int8;

create table CCM_CORE.PAGE_MODEL_CONTAINER_MODELS (
    CONTAINER_ID int8 not null,
    CONTAINER_UUID varchar(255) not null,
    CONTAINER_KEY varchar(255),
    UUID varchar(255) not null,
    PAGE_MODEL_ID int8,
    STYLE_ID int8,
    primary key (CONTAINER_ID)
);

create table CCM_CORE.STYLE_MEDIA_QUERIES (
    MEDIA_QUERY_ID int8 not null,
    MAX_WIDTH_UNIT varchar(255),
    MAX_WIDTH_VALUE float4,
    MEDIA_TYPE varchar(255),
    MIN_WIDTH_UNIT varchar(255),
    MIN_WIDTH_VALUE float4,
    primary key (MEDIA_QUERY_ID)
);

create table CCM_CORE.STYLE_MEDIA_RULES (
    MEDIA_RULE_ID int8 not null,
    MEDIA_QUERY_ID int8,
    STYLE_ID int8,
    primary key (MEDIA_RULE_ID)
);

create table CCM_CORE.STYLE_PROPERTIES (
    PROPERTY_ID int8 not null,
    NAME varchar(256),
    PROPERTY_VALUE varchar(4096),
    RULE_ID int8,
    primary key (PROPERTY_ID)
);

create table CCM_CORE.STYLE_RULES (
    RULE_ID int8 not null,
    SELECTOR varchar(2048),
    STYLE_ID int8,
    primary key (RULE_ID)
);

create table CCM_CORE.STYLES (
    STYLE_ID int8 not null,
    STYLENAME varchar(255),
    primary key (STYLE_ID)
);

alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS 
    add constraint FK1uvkayybawff8sqkmerqt60bk 
    foreign key (CONTAINER_ID) 
    references CCM_CORE.PAGE_MODEL_CONTAINER_MODELS;

alter table CCM_CORE.PAGE_MODEL_CONTAINER_MODELS 
    add constraint FK1c6drneacxveol92vpum79fxb 
    foreign key (PAGE_MODEL_ID) 
    references CCM_CORE.PAGE_MODELS;

alter table CCM_CORE.PAGE_MODEL_CONTAINER_MODELS 
    add constraint FKoi5wphv3vtwryc19akku28p24 
    foreign key (STYLE_ID) 
    references CCM_CORE.STYLES;

alter table CCM_CORE.STYLE_MEDIA_RULES 
    add constraint FKdq24a4atxp4c1sbqs8g6lpkx0 
    foreign key (MEDIA_QUERY_ID) 
    references CCM_CORE.STYLE_MEDIA_QUERIES;

alter table CCM_CORE.STYLE_MEDIA_RULES 
    add constraint FKf67h8q9kkjft9go2xo2572n17 
    foreign key (STYLE_ID) 
    references CCM_CORE.STYLES;

alter table CCM_CORE.STYLE_PROPERTIES 
    add constraint FKg2g0n7jmce3vjmula0898yp94 
    foreign key (RULE_ID) 
    references CCM_CORE.STYLE_RULES;

alter table CCM_CORE.STYLE_RULES 
    add constraint FKcbr0k93g001jix7i4kncsce1w 
    foreign key (STYLE_ID) 
    references CCM_CORE.STYLES;
