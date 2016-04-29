-- Remove registry domain (if existing) and modify settings to new structure

alter table CCM_CORE.SETTINGS 
    drop constraint FK_3k0t3in140j6wj6eq5olwjgu;

delete from CCM_CORE.CATEGORY_DOMAINS
    where DOMAIN_KEY = 'registry';

delete from CCM_CORE.CATEGORIES 
    where NAME = 'registry-root';

alter table CCM_CORE.SETTINGS_ENUM_VALUES
    drop constraint FK_sq653hqyeeklci0y7pvoxf5ha;

alter table CCM_CORE.SETTINGS_L10N_STR_VALUES
    drop constraint FK_t21obt5do2tjhskjxgxd5143r;

alter table CCM_CORE.SETTINGS_STRING_LIST
    drop constraint FK_obwiaa74lrjqjlpjidjltysoq;

alter table CCM_CORE.SETTINGS_ENUM_VALUES
    add constraint FK_sq653hqyeeklci0y7pvoxf5ha 
        foreign key (ENUM_ID) 
        references CCM_CORE.SETTINGS;

alter table CCM_CORE.SETTINGS_L10N_STR_VALUES
    add constraint FK_t21obt5do2tjhskjxgxd5143r 
        foreign key (ENTRY_ID) 
        references CCM_CORE.SETTINGS;

alter table CCM_CORE.SETTINGS_STRING_LIST
    add constraint FK_obwiaa74lrjqjlpjidjltysoq 
        foreign key (LIST_ID) 
        references CCM_CORE.SETTINGS;

alter table CCM_CORE.SETTINGS_STRING_LIST
    DROP COLUMN OBJECT_ID;

alter table CCM_CORE.SETTINGS 
    rename column OBJECT_ID to SETTING_ID;

alter table CCM_CORE.SETTINGS 
    add column DTYPE varchar(31) not null;

alter table CCM_CORE.SETTINGS 
    add column CONFIGURATION_CLASS varchar(512) not null;

alter table CCM_CORE.SETTINGS 
    add column SETTING_VALUE_DOUBLE float8;

alter table CCM_CORE.SETTINGS 
    add column SETTING_VALUE_BIG_DECIMAL numeric(19, 2);

alter table CCM_CORE.SETTINGS 
    add column SETTING_VALUE_STRING varchar(1024);

alter table CCM_CORE.SETTINGS 
    add column SETTING_VALUE_BOOLEAN boolean;

alter table CCM_CORE.SETTINGS 
    add column SETTING_VALUE_LONG int8;

alter table CCM_CORE.SETTINGS 
        add constraint UK_5whinfxdaepqs09e5ia9y71uk 
        unique (CONFIGURATION_CLASS, NAME);

drop table CCM_CORE.SETTINGS_BIG_DECIMAL;

drop table CCM_CORE.SETTINGS_BOOLEAN;

drop table CCM_CORE.SETTINGS_DOUBLE;

drop table CCM_CORE.SETTINGS_L10N_STRING;

drop table CCM_CORE.SETTINGS_LONG;

drop table CCM_CORE.SETTINGS_STRING;

drop table CCM_CORE.SETTINGS_ENUM;