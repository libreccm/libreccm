ALTER TABLE ccm_cms.content_items
    ADD COLUMN lifecycle_id int8;

ALTER TABLE ccm_cms.content_items
    ADD COLUMN workflow_id int8;

ALTER TABLE ccm_cms.content_items_aud
    ADD COLUMN lifecycle_id int8;

ALTER TABLE ccm_cms.content_items_aud
    ADD COLUMN workflow_id int8;

alter table CCM_CMS.CONTENT_ITEMS 
        add constraint FKfh1nm46qpw6xcwkmgaqw2iu3h 
        foreign key (LIFECYCLE_ID) 
        references CCM_CMS.LIFECYCLES;

alter table CCM_CMS.CONTENT_ITEMS 
        add constraint FKl00ldjygr6as8gqbt3j14ke7j 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;