create table CCM_CMS.SITES (
    DEFAULT_SITE boolean,
    NAME varchar(255),
    OBJECT_ID bigint not null,
    CATEGORY_DOMAIN_ID bigint,
    primary key (OBJECT_ID)
);

alter table CCM_CMS.SITES 
    add constraint UK_fgjx0nuuxlgnuit724a96vw81 unique (NAME);

alter table CCM_CMS.SITES 
    add constraint FKmiysfmv1nkcso6bm18sjhvtm8 
    foreign key (CATEGORY_DOMAIN_ID) 
    references CCM_CORE.CATEGORY_DOMAINS;

alter table CCM_CMS.SITES 
    add constraint FK5kmn26x72uue9t3dfnjwes45 
    foreign key (OBJECT_ID) 
    references CCM_CORE.APPLICATIONS;
