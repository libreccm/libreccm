alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES
    add column UUID varchar(255) not null;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
    add constraint UK_787va2ep8ucoul29qgsoaxnub unique (UUID);







