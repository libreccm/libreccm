alter table CCM_CMS.CONTENT_ITEMS
    rename column launchDate to LAUNCH_DATE;

alter table CCM_CMS.CONTENT_ITEMS
    rename column version to VERSION;

alter table CCM_CMS.CONTENT_ITEMS
    rename column contentType_OBJECT_ID to CONTENT_TYPE_ID;

alter table CCM_CMS.CONTENT_ITEMS_AUD
    rename column launchDate to LAUNCH_DATE;

alter table CCM_CMS.CONTENT_ITEMS
    rename column version to VERSION;

alter table CCM_CMS.CONTENT_ITEMS
    rename column contentType_OBJECT_ID to CONTENT_TYPE_ID;

alter table CCM_CMS.CONTENT_TYPES
    rename column mode to TYPE_MODE;

