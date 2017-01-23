alter table CCM_CORE.PERMISSIONS 
    add column INHERITED boolean;

alter table CCM_CORE.PERMISSIONS 
    add column INHERITED_FROM_ID bigint;

alter table CCM_CORE.PERMISSIONS 
    add constraint FKc1x3h1p3o20qiwmonpmva7t5i 
    foreign key (INHERITED_FROM_ID) 
    references CCM_CORE.CCM_OBJECTS;
