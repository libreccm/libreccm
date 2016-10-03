    create table CCM_CMS.FOLDER_CONTENT_SECTION_MAP (
        CONTENT_SECTION_ID bigint,
        FOLDER_ID bigint not null,
        primary key (FOLDER_ID)
    );

    create table CCM_CMS.FOLDERS (
        TYPE varchar(255) not null,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

alter table CCM_CMS.CONTENT_SECTIONS
    drop constraint if exists FKajweudfxaf7g2ydr2hcgqwcib;

alter table CCM_CMS.CONTENT_SECTIONS
    drop constraint if exists FK6g7kw4b6diqa0nks45ilp0vhs;

alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FKavcn4aakxsb7kt7hmqlx0ecu6 
        foreign key (ROOT_ASSETS_FOLDER_ID) 
        references CCM_CMS.FOLDERS;

    alter table CCM_CMS.FOLDER_CONTENT_SECTION_MAP 
        add constraint FKmmb7728dp707dljq282ch47k3 
        foreign key (FOLDER_ID) 
        references CCM_CMS.FOLDERS;

    alter table CCM_CMS.FOLDERS 
        add constraint FK2ag06r5ywtuji2pkt68etlg48 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORIES;
