create table SITE_AWARE_APPLICATIONS (
    OBJECT_ID bigint not null,
    SITE_ID bigint,
    primary key (OBJECT_ID)
);

alter table SITE_AWARE_APPLICATIONS 
    add constraint FKopo91c29jaunpcusjwlphhxkd 
    foreign key (SITE_ID) 
    references CCM_CORE.SITES;

alter table SITE_AWARE_APPLICATIONS 
    add constraint FKslbu2qagg23dmdu01lun7oh7x 
    foreign key (OBJECT_ID) 
    references CCM_CORE.APPLICATIONS;