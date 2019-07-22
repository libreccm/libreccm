create table CONTACT_ENTRIES (
    CONTACT_ENTRY_ID bigint not null,
    ENTRY_KEY varchar(255) not null,
    ENTRY_ORDER bigint,
    ENTRY_VALUE varchar(4096),
    CONTACTABLE_ID bigint,
    primary key (CONTACT_ENTRY_ID)
);

create table CCM_CMS.CONTACT_ENTRIES_AUD (
    CONTACT_ENTRY_ID bigint not null,
    REV integer not null,
    REVTYPE tinyint,
    REVEND integer,
    ENTRY_KEY varchar(255),
    ENTRY_ORDER bigint,
    ENTRY_VALUE varchar(4096),
    primary key (CONTACT_ENTRY_ID, REV)
);

create table CONTACTABLE_ENTITIES (
    OBJECT_ID bigint not null,
    POSTAL_ADDRESS_ID bigint,
    primary key (OBJECT_ID)
);

create table CCM_CMS.CONTACTABLE_ENTITIES_AUD (
    OBJECT_ID bigint not null,
    REV integer not null,
    POSTAL_ADDRESS_ID bigint,
    primary key (OBJECT_ID, REV)
);

create table CCM_CMS.ContactableEntity_ContactEntry_AUD (
    REV integer not null,
    CONTACTABLE_ID bigint not null,
    CONTACT_ENTRY_ID bigint not null,
    REVTYPE tinyint,
    REVEND integer,
    primary key (REV, CONTACTABLE_ID, CONTACT_ENTRY_ID)
);

create table ORGANIZATIONS (
   NAME varchar(1024),
    OBJECT_ID bigint not null,
    primary key (OBJECT_ID)
);

create table CCM_CMS.ORGANIZATIONS_AUD (
    OBJECT_ID bigint not null,
    REV integer not null,
    NAME varchar(1024),
    primary key (OBJECT_ID, REV)
);

create table PERSONS (
    BIRTHDATE date,
    OBJECT_ID bigint not null,
    primary key (OBJECT_ID)
);

create table CCM_CMS.PERSONS_AUD (
    OBJECT_ID bigint not null,
    REV integer not null,
    BIRTHDATE date,
    primary key (OBJECT_ID, REV)
);

create table CCM_CMS.PERSON_NAMES (
    PERSON_ID bigint not null,
    GIVEN_NAME varchar(255),
    NAME_PREFIX varchar(255),
    SUFFIX varchar(255),
    SURNAME varchar(255)
);

 create table CCM_CMS.PERSON_NAMES_AUD (
    REV integer not null,
    REVTYPE tinyint not null,
    PERSON_ID bigint not null,
    REVEND integer,
    SURNAME varchar(255),
    NAME_PREFIX varchar(255),
    GIVEN_NAME varchar(255),
    SUFFIX varchar(255),
    primary key (REV, REVTYPE, PERSON_ID)
);

create table POSTAL_ADDRESSES (
   ADDRESS varchar(2048),
    CITY varchar(512),
    ISO_COUNTRY_CODE varchar(10),
    POSTAL_CODE varchar(255),
    ADDRESS_STATE varchar(255),
    OBJECT_ID bigint not null,
    primary key (OBJECT_ID)
);

create table CCM_CMS.POSTAL_ADDRESSES_AUD (
    OBJECT_ID bigint not null,
    REV integer not null,
    ADDRESS varchar(2048),
    CITY varchar(512),
    ISO_COUNTRY_CODE varchar(10),
    POSTAL_CODE varchar(255),
    ADDRESS_STATE varchar(255),
    primary key (OBJECT_ID, REV)
);

alter table CCM_CMS.CONTACT_ENTRIES 
    add constraint FKljrrfco44damal9eaqrnfam0m 
    foreign key (CONTACTABLE_ID) 
    references CCM_CMS.CONTACTABLE_ENTITIES;

alter table CCM_CMS.CONTACT_ENTRIES_AUD 
    add constraint FKib8xp3ab8kdkc0six36f99e2g 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.CONTACT_ENTRIES_AUD 
    add constraint FKrse7ibjqsfnny5t1b2tqqs3pt 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.CONTACTABLE_ENTITIES 
    add constraint FKqefwowr9adclj3xvpfje9rddr 
    foreign key (POSTAL_ADDRESS_ID) 
    references CCM_CMS.POSTAL_ADDRESSES;

alter table CCM_CMS.CONTACTABLE_ENTITIES 
    add constraint FKhdwlhf3jp8wf5wxjkoynrcspj 
    foreign key (OBJECT_ID) 
    references CCM_CMS.ASSETS;

alter table CCM_CMS.CONTACTABLE_ENTITIES_AUD 
    add constraint FKjx8trfvt96fkdn6bafnh839id 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.ASSETS_AUD;

alter table CCM_CMS.ContactableEntity_ContactEntry_AUD 
    add constraint FKs5tfdp1auj9ocgvfa9ivec517 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.ContactableEntity_ContactEntry_AUD 
    add constraint FKskn2ovg24tnnnwd2o8y0biyje 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.ORGANIZATIONS 
    add constraint FK77ig0to48xrlfx8qsc0vlfsp6 
    foreign key (OBJECT_ID) 
    references CCM_CMS.CONTACTABLE_ENTITIES;

alter table CCM_CMS.ORGANIZATIONS_AUD 
    add constraint FKp0k3bf008pih96sguio80siql 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.CONTACTABLE_ENTITIES_AUD;

alter table CCM_CMS.PERSON_NAMES 
    add constraint FK2yluyhmpuhwxafcbna6u8txrt 
    foreign key (PERSON_ID) 
    references CCM_CMS.PERSONS;

alter table CCM_CMS.PERSON_NAMES_AUD 
    add constraint FKtqtlwx8pa9ydh009sudtpfxie 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.PERSON_NAMES_AUD 
    add constraint FKs6m8tgbp8agrd5q3klwbtcujg 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.PERSONS 
    add constraint FKiv4ydysjekfx64pkb5v4vd9yj 
    foreign key (OBJECT_ID) 
    references CCM_CMS.CONTACTABLE_ENTITIES;

alter table CCM_CMS.PERSONS_AUD 
    add constraint FKpup1q3295qkuovaptq8aj5lxp 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.CONTACTABLE_ENTITIES_AUD;