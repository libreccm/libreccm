alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES
    drop constraint if exists FK1htp420ki24jaswtcum56iawe;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES
    drop constraint if exists FK8rbggnp4yjpab8quvvx800ymy;

create table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES_ (
    TASK_DEPENDENCY_ID bigint,
    BLOCKED_TASK_ID bigint,
    BLOCKING_TASK_ID bigint
);

insert into CCM_CORE.WORKFLOW_TASK_DEPENDENCIES_ (BLOCKED_TASK_ID, BLOCKING_TASK_ID) 
    (select DEPENDENT_TASK_ID, DEPENDS_ON_TASK_ID 
    from CCM_CORE.WORKFLOW_TASK_DEPENDENCIES);

update CCM_CORE.WORKFLOW_TASK_DEPENDENCIES_ 
    set TASK_DEPENDENCY_ID = nextval('hibernate_sequence');

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES_ 
    alter column TASK_DEPENDENCY_ID set not null;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES_ 
    add primary key(TASK_DEPENDENCY_ID);

drop table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES_ 
    rename to CCM_CORE.WORKFLOW_TASK_DEPENDENCIES;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES
    add constraint FKci4hwj0evq82m4nyvux1bwc73 
    foreign key (BLOCKED_TASK_ID) 
    references CCM_CORE.WORKFLOW_TASKS;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
    add constraint FKkmrbcm2fbbrb43l5j7supp7eg 
    foreign key (BLOCKING_TASK_ID) 
    references CCM_CORE.WORKFLOW_TASKS;
