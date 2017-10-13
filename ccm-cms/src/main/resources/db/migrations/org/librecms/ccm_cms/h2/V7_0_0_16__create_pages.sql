 create table CCM_CMS.PAGE_THEME_CONFIGURATIONS (
    PAGE_ID bigint not null,
    INDEX_PAGE_TEMPLATE varchar(255),
    ITEM_PAGE_TEMPLATE varchar(255),
    THEME varchar(255) not null,
    primary key (PAGE_ID, THEME)
);

create table CCM_CMS.PAGES (
    OBJECT_ID bigint not null,
    INDEX_PAGE_MODEL_ID bigint,
    ITEM_PAGE_MODEL_ID bigint,
    primary key (OBJECT_ID)
);

create table CCM_CMS.PAGES_APP (
    OBJECT_ID bigint not null,
    CATEGORY_DOMAIN_ID bigint,
    SITE_ID bigint,
    primary key (OBJECT_ID)
);

alter table CCM_CMS.PAGE_THEME_CONFIGURATIONS 
    add constraint FK6l6xp6ex6sh2uuxfmeekf6ckn 
    foreign key (PAGE_ID) 
    references CCM_CMS.PAGES;

alter table CCM_CMS.PAGES 
    add constraint FKqweb08d151ot4ij9io72w3yhx 
    foreign key (INDEX_PAGE_MODEL_ID) 
    references CCM_CORE.PAGE_MODELS;

alter table CCM_CMS.PAGES 
    add constraint FKg2p2ahbayc2coei72pk1lnenf 
    foreign key (ITEM_PAGE_MODEL_ID) 
    references CCM_CORE.PAGE_MODELS;

alter table CCM_CMS.PAGES 
    add constraint FKmgmth087tmxwieujn2vs5opbo 
    foreign key (OBJECT_ID) 
    references CCM_CORE.CCM_OBJECTS;

alter table CCM_CMS.PAGES_APP 
    add constraint FK5swx0e8pj0mm5t1es0lj4nwlx 
    foreign key (CATEGORY_DOMAIN_ID) 
    references CCM_CORE.CATEGORY_DOMAINS;

alter table CCM_CMS.PAGES_APP 
    add constraint FK3wkyn4oxa65f7svtj917m61jc 
    foreign key (SITE_ID) 
    references CCM_CORE.SITES;

alter table CCM_CMS.PAGES_APP 
    add constraint FKrrk4g7my3e4qkdoeiygkqxduy 
    foreign key (OBJECT_ID) 
    references CCM_CORE.APPLICATIONS;
