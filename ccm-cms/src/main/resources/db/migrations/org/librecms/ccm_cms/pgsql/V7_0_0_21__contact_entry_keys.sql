alter table CCM_CMS.CONTACT_ENTRIES add column CONTACT_ENTRY_KEY_ID int8;

create table CCM_CMS.CONTACT_ENTRY_KEY_LABELS (
       KEY_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (KEY_ID, LOCALE)
    );

create table CCM_CMS.CONTACT_ENTRY_KEY_LABELS_AUD (
   REV int4 not null,
    KEY_ID int8 not null,
    LOCALIZED_VALUE text not null,
    LOCALE varchar(255) not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, KEY_ID, LOCALIZED_VALUE, LOCALE)
);

create table CCM_CMS.CONTACT_ENTRY_KEYS (
    KEY_ID int8 not null,
    ENTRY_KEY varchar(255),
    primary key (KEY_ID)
);

create table CCM_CMS.CONTACT_ENTRY_KEYS_AUD (
    KEY_ID int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    ENTRY_KEY varchar(255),
    primary key (KEY_ID, REV)
);

alter table CCM_CMS.CONTACT_ENTRIES 
    add constraint FKirtfj8sm4y5myworl5hvs1l78 
    foreign key (CONTACT_ENTRY_KEY_ID) 
    references CCM_CMS.CONTACT_ENTRY_KEYS;

alter table CCM_CMS.CONTACT_ENTRY_KEY_LABELS 
    add constraint FK243nk3buqm0pskkr5ifjqfxn5 
    foreign key (KEY_ID) 
    references CCM_CMS.CONTACT_ENTRY_KEYS;

alter table CCM_CMS.CONTACT_ENTRY_KEY_LABELS_AUD 
    add constraint FK6n995k5gao6v63gfcga3yaxcw 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.CONTACT_ENTRY_KEY_LABELS_AUD 
    add constraint FKdr8ujdpn1ej8l6omlxq8bsxbd 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.CONTACT_ENTRY_KEYS_AUD 
    add constraint FKcvn2b1h1d4uvvmtbf4qf81l0y 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.CONTACT_ENTRY_KEYS_AUD 
    add constraint FKkyy4v3tax8w5htnpkmmt8aec1 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;