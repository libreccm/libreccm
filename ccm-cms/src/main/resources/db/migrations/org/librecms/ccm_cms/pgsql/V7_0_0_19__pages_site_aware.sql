alter table CCM_CMS.PAGES_APP
    drop constraint FK3wkyn4oxa65f7svtj917m61jc;

alter table CCM_CMS.PAGES_APP
    drop contraint FKrrk4g7my3e4qkdoeiygkqxduy;

alter table CCM_CMS.PAGES_APP
    drop column if exists SITE_ID;

alter table CCM_CMS.PAGES_APP 
    add constraint FKk4jb5fylibg2pbbaypyt6f8lb 
    foreign key (OBJECT_ID) 
    references SITE_AWARE_APPLICATIONS;
