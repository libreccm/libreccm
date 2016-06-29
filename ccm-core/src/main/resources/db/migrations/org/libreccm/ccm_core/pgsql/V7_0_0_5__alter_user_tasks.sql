alter table CCM_CORE.WORKFLOW_USER_TASKS drop column if exists ACTIVE;
alter table CCM_CORE.WORKFLOW_USER_TASKS drop column if exists TASK_STATE;

alter table CCM_CORE.WORKFLOW_USER_TASKS drop constraint FK_bg60xxg9kerqsxyphbfxulg8y;
alter table CCM_CORE.WORKFLOW_USER_TASKS drop column if exists WORKFLOW_ID;

