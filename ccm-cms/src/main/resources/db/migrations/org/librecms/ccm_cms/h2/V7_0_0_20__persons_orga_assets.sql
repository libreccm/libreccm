create table CONTACT_ENTRIES (
    CONTACT_ENTRY_ID bigint not null,
     ENTRY_KEY varchar(255) not null,
     ENTRY_ORDER bigint,
     ENTRY_VALUE varchar(4096),
     CONTACTABLE_ID bigint,
     primary key (CONTACT_ENTRY_ID)
);

create table CONTACTABLE_ENTITIES (
   OBJECT_ID bigint not null,
    POSTAL_ADDRESS_ID bigint,
    primary key (OBJECT_ID)
);

create table ORGANIZATIONS (
   NAME varchar(1024),
    OBJECT_ID bigint not null,
    primary key (OBJECT_ID)
);

create table PERSONS (
   BIRTHDATA date,
    GIVEN_NAME varchar(255),
    NAME_PREFIX varchar(255),
    SUFFIX varchar(255),
    SURNAME varchar(255),
    OBJECT_ID bigint not null,
    primary key (OBJECT_ID)
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

alter table CONTACT_ENTRIES 
   add constraint FKfm16ni25r5iscfcyqhlyo4y24 
   foreign key (CONTACTABLE_ID) 
   references CONTACTABLE_ENTITY;

alter table CONTACTABLE_ENTITY 
   add constraint FKn7nb0chctw8ih05kguf2s4jh0 
   foreign key (POSTAL_ADDRESS_ID) 
   references POSTAL_ADDRESSES;

alter table CONTACTABLE_ENTITY 
   add constraint FK37gvl3x07envs4u4lwustuyge 
   foreign key (OBJECT_ID) 
   references CCM_CMS.ASSETS;

alter table ORGANIZATIONS 
   add constraint FKjjcnjs0eecrla6eqq8fes8o86 
   foreign key (OBJECT_ID) 
   references CONTACTABLE_ENTITY;

alter table PERSONS 
   add constraint FK3i2r1w7qc1ofdn4jlbak7vkpu 
   foreign key (OBJECT_ID) 
   references CONTACTABLE_ENTITY;

alter table POSTAL_ADDRESSES 
   add constraint FK4vajjjjo8ro0wns58t8f3i782 
   foreign key (OBJECT_ID) 
   references CCM_CMS.ASSETS;