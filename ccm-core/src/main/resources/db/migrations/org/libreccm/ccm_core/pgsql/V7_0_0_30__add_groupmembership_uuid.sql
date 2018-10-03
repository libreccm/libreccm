alter table CCM_CORE.GROUP_MEMBERSHIPS
    add column UUID varchar(255) not null;

alter table CCM_CORE.GROUP_MEMBERSHIPS 
    add constraint UK_kkdoia60bmiwhhdru169p3n9g unique (UUID);


