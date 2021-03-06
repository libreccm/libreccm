-- Adds a foreign key column which references the workflow template used
-- to create a workflow

alter table CCM_CORE.WORKFLOWS
    add column TEMPLATE_ID bigint;

alter table CCM_CORE.WORKFLOWS 
        add constraint FKol71r1t83h0qe65gglq43far2 
        foreign key (template_id) 
        references CCM_CORE.WORKFLOW_TEMPLATES;