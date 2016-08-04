alter table CCM_CMS.CONTENT_TYPES
    drop constraint FKoqvcvktnvt4ncx5k6daqat4u8;

alter table CCM_CMS.CONTENT_TYPES
    drop constraint FKpgeccqsr50xwb268ypmfx0r66;

alter table CCM_CMS.CONTENT_TYPES 
    add constraint FK8s83we1tuh9r3j57dyos69wfa 
    foreign key (DEFAULT_LIFECYCLE_ID) 
    references CCM_CMS.LIFECYLE_DEFINITIONS;

alter table CCM_CMS.CONTENT_TYPES 
    add constraint FKhnu9oikw8rpf22lt5fmk41t7k 
    foreign key (DEFAULT_WORKFLOW) 
    references CCM_CORE.WORKFLOW_TEMPLATES;
