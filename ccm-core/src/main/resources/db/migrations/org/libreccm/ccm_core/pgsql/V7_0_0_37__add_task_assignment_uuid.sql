alter table CCM_CORE.WORKFLOW_TASK_ASSIGNMENTS
    add column UUID varchar(255) not null;

alter table CCM_CORE.WORKFLOW_TASK_ASSIGNMENTS 
    add constraint UK_gv93k167pe9qy3go9vjau1q2t unique (UUID);






