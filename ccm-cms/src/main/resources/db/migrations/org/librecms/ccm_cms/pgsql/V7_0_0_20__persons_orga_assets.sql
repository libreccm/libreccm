create table CCM_CMS.CONTACT_ENTRIES (
   CONTACT_ENTRY_ID int8 not null,
    ENTRY_KEY varchar(255) not null,
    ENTRY_ORDER int8,
    ENTRY_VALUE varchar(4096),
    CONTACTABLE_ID int8,
    primary key (CONTACT_ENTRY_ID)
);

create table CCM_CMS.CONTACTABLE_ENTITIES (
   OBJECT_ID int8 not null,
    POSTAL_ADDRESS_ID int8,
    primary key (OBJECT_ID)
);

create table CCM_CMS.ORGANIZATIONS (
   NAME varchar(1024),
    OBJECT_ID int8 not null,
    primary key (OBJECT_ID)
);

create table CCM_CMS.PERSONS (
   BIRTHDATA date,
    GIVEN_NAME varchar(255),
    NAME_PREFIX varchar(255),
    SUFFIX varchar(255),
    SURNAME varchar(255),
    OBJECT_ID int8 not null,
    primary key (OBJECT_ID)
);

create table CCM_CMS.POSTAL_ADDRESSES (
   ADDRESS varchar(2048),
    CITY varchar(512),
    ISO_COUNTRY_CODE varchar(10),
    POSTAL_CODE varchar(255),
    ADDRESS_STATE varchar(255),
    OBJECT_ID int8 not null,
    primary key (OBJECT_ID)
);

alter table CCM_CMS.CONTACT_ENTRIES 
   add constraint FKfm16ni25r5iscfcyqhlyo4y24 
   foreign key (CONTACTABLE_ID) 
   references CONTACTABLE_ENTITY;

alter table CCM_CMS.CONTACTABLE_ENTITY 
   add constraint FKn7nb0chctw8ih05kguf2s4jh0 
   foreign key (POSTAL_ADDRESS_ID) 
   references POSTAL_ADDRESSES;

alter table CCM_CMS.CONTACTABLE_ENTITY 
   add constraint FK37gvl3x07envs4u4lwustuyge 
   foreign key (OBJECT_ID) 
   references CCM_CMS.ASSETS;

alter table CCM_CMS.ORGANIZATIONS 
   add constraint FKjjcnjs0eecrla6eqq8fes8o86 
   foreign key (OBJECT_ID) 
   references CONTACTABLE_ENTITY;

alter table CCM_CMS.PERSONS 
   add constraint FK3i2r1w7qc1ofdn4jlbak7vkpu 
   foreign key (OBJECT_ID) 
   references CONTACTABLE_ENTITY;

alter table CCM_CMS.POSTAL_ADDRESSES 
   add constraint FK4vajjjjo8ro0wns58t8f3i782 
   foreign key (OBJECT_ID) 
   references CCM_CMS.ASSETS;