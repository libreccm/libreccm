alter table CCM_CMS.CONTENT_ITEMS 
    add column CREATION_DATE timestamp;

alter table CCM_CMS.CONTENT_ITEMS 
    add column CREATION_USER_NAME varchar(255);

alter table CCM_CMS.CONTENT_ITEMS 
    add column LAST_MODIFIED timestamp;

alter table CCM_CMS.CONTENT_ITEMS 
    add column LAST_MODIFYING_USER_NAME varchar(255);

