alter table CCM_CMS.CONTENT_ITEMS
    alter column launchDate rename to LAUNCH_DATE;

alter table CCM_CMS.CONTENT_ITEMS
    alter column version rename to VERSION;

alter table CCM_CMS.CONTENT_ITEMS
    alter column contentType_OBJECT_ID rename to CONTENT_TYPE_ID;

alter table CCM_CMS.CONTENT_ITEMS_AUD
    alter column launchDate rename to LAUNCH_DATE;

alter table CCM_CMS.CONTENT_ITEMS
    alter column version rename to VERSION;

alter table CCM_CMS.CONTENT_ITEMS_AUD
    alter column contentType_OBJECT_ID rename to CONTENT_TYPE_ID;

alter table CCM_CMS.CONTENT_TYPES
    alter column mode rename to TYPE_MODE;