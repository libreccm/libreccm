alter table CCM_CORE.WORKFLOWS 
    add column ACTIVE boolean;

alter table CCM_CORE.WORKFLOWS 
    add column WORKFLOW_STATE character varying(255);

alter table CCM_CORE.WORKFLoWS
    add column TASKS_STATE character varying(255);

alter table CCM_CORE.WORKFLOWS
    add column UUID character varying(255) not null;

alter table CCM_CORE.WORKFLOWS
    add column object_id bigint;

alter table CCM_CORE.WORKFLOWS 
    add constraint UK_o113id7d1cxql0edsrohlnn9x unique (UUID);

alter table CCM_CORE.WORKFLOWS
    add constraint fkrm2yfrs6veoxoy304upq2wc64 
    foreign key(object_id)
    references CCM_CORE.CCM_OBJECTS;

alter table CCM_CORE.WORKFLOW_TASKS
    add column UUID character varying(255) not null;

alter table CCM_CORE.WORKFLOW_TASKS 
    add constraint UK_2u6ruatxij8wfojl8a1eigqqd unique (UUID);

alter table CCM_CORE.WORKFLOW_TASK_LABELS 
    add constraint fkf715qud6g9xv2xeb8rrpnv4x
    foreign key (TASK_ID)
    references CCM_CORE.WORKFLOW_TASKS;

alter table CCM_CORE.WORKFLOW_TASKS_DESCRIPTIONS
    rename to WORKFLOW_TASK_DESCRIPTIONS;

alter table CCM_CORE.WORKFLOW_TASK_DESCRIPTIONS
    add constraint fkf715qud6g9xv2xeb8rrpnv4xs
    foreign key (TASK_ID)
    references CCM_CORE.WORKFLOW_TASKS;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES
    add constraint fk1htp420ki24jaswtcum56iawe 
    foreign key (DEPENDENT_TASK_ID)
    references CCM_CORE.WORKFLOW_TASKS;

alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES
    add constraint fk8rbggnp4yjpab8quvvx800ymy
    foreign key (DEPENDS_ON_TASK_ID)
    references CCM_CORE.WORKFLOW_TASKS;

drop table CCM_CORE.WORKFLOW_TASK_COMMENTS;

create table CCM_CORE.WORKFLOW_TASK_COMMENTS (
    COMMENT_ID int8 not null,
    COMMENT text,
    UUID varchar(255) not null,
    AUTHOR_ID int8,
    TASK_ID int8,
    primary key (COMMENT_ID)
);

alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
        add constraint UK_4nnedf08odyjxalfkg16fmjoi unique (UUID);

alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
    add constraint FKd2ymdg8nay9pmh2nn2whba0j8 
    foreign key (AUTHOR_ID) 
    references CCM_CORE.USERS;

alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
    add constraint FKkfqrf9jdvm7livu5if06w0r5t 
    foreign key (TASK_ID) 
    references CCM_CORE.WORKFLOW_TASKS;

alter table CCM_CORE.WORKFLOW_USER_TASKS
    rename to WORKFLOW_ASSIGNABLE_TASKS;

alter table CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS
    add constraint fkt9ha3no3bj8a50pnw8cnqh2cq
    foreign key(TASK_ID)
    references CCM_CORE.WORKFLOW_TASKS(TASK_ID);

alter table CCM_CORE.TASK_ASSIGNMENTS
    rename to WORKFLOW_TASK_ASSIGNMENTS;

