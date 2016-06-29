create table CCM_CORE.WORKFLOW_TEMPLATES (
    WORKFLOW_ID int8 not null,
    primary key (WORKFLOW_ID)
);

alter table CCM_CORE.WORKFLOW_TEMPLATES 
    add constraint FK8692vdme4yxnkj1m0k1dw74pk 
    foreign key (WORKFLOW_ID) 
    references CCM_CORE.WORKFLOWS;