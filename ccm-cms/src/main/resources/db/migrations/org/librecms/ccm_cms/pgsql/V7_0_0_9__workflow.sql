-- drop unused tables

drop table ccm_cms.article_leads;

drop table ccm_cms.task_event_url_generator;

alter table CCM_CMS.WORKFLOW_TASKS
    add column TASK_TYPE varchar(255);

alter table CCM_CMS.WORKFLOW_TASKS
    drop column task_type_id;

alter table CCM_CMS.WORKFLOW_TASKS
    add constraint fkoon3rwfmg0lhgbj4un4q3otya
    foreign key (TASK_ID)
    references CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS;

drop table CCM_CMS.WORKFLOW_TASK_TYPES;


