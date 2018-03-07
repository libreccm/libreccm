    create table CCM_CORE.THEME_DATA_FILES (
        CREATION_DATE timestamp,
        FILE_DATA blob,
        LAST_MODIFIED timestamp,
        FILE_SIZE bigint,
        TYPE varchar(255),
        FILE_ID bigint not null,
        primary key (FILE_ID)
    );

    create table CCM_CORE.theme_directories (
        FILE_ID bigint not null,
        primary key (FILE_ID)
    );

    create table CCM_CORE.THEME_FILES (
        FILE_ID bigint not null,
        NAME varchar(255) not null,
        FILE_PATH varchar(8192) not null,
        UUID varchar(255) not null,
        VERSION varchar(255),
        PARENT_DIRECTORY_ID bigint,
        primary key (FILE_ID)
    );

    create table CCM_CORE.THEMES (
        THEME_ID bigint not null,
        NAME varchar(255),
        UUID varchar(255),
        VERSION varchar(255),
        ROOT_DIRECTORY_ID bigint,
        primary key (THEME_ID)
    );
    

    alter table CCM_CORE.THEME_DATA_FILES 
        add constraint FK630m2y2p7pp487ofowbefrm89 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.theme_directories 
        add constraint FKrmgyslvw22j87n4cxau5jvsou 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.THEME_FILES 
        add constraint FKfsycb4bt8d0wye7r3n06ekfeu 
        foreign key (PARENT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEMES 
        add constraint FKlat55c5l3fxbykkibrmv7qi4x 
        foreign key (ROOT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEME_DATA_FILES 
        add constraint FK630m2y2p7pp487ofowbefrm89 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.theme_directories 
        add constraint FKrmgyslvw22j87n4cxau5jvsou 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.THEME_FILES 
        add constraint FKfsycb4bt8d0wye7r3n06ekfeu 
        foreign key (PARENT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEMES 
        add constraint FKlat55c5l3fxbykkibrmv7qi4x 
        foreign key (ROOT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;