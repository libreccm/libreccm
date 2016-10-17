-- Adds a foreign key column which references the workflow template used
-- to create a workflow

alter table CCM_CORE.WORKFLOWS
    add column TEMPLATE_ID int8;

alter table CCM_CORE.WORKFLOWS 
        add constraint FKol71r1t83h0qe65gglq43far2 
        foreign key (TEMPLATE_ID) 
        references CCM_CORE.WORKFLOW_TEMPLATES;