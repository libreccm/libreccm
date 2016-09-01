create table CONTENT_SECTION_LIFECYCLE_DEFINITIONS (
    CONTENT_SECTION_ID int8 not null,
    LIFECYCLE_DEFINITION_ID int8 not null
);

create table CONTENT_SECTION_WORKFLOW_TEMPLATES (
    CONTENT_SECTION_ID int8 not null,
    WORKFLOW_TEMPLATE_ID int8 not null
);

alter table CONTENT_SECTION_LIFECYCLE_DEFINITIONS 
    add constraint UK_dhbp1f81iaw6sl7tg36xh439e unique (LIFECYCLE_DEFINITION_ID);

alter table CONTENT_SECTION_WORKFLOW_TEMPLATES 
    add constraint UK_goj42ghwu4tf1akfb2r6ensns unique (WORKFLOW_TEMPLATE_ID);

alter table CONTENT_SECTION_LIFECYCLE_DEFINITIONS 
    add constraint FKqnsnk1eju8vrbm7x0wr5od4ll 
    foreign key (LIFECYCLE_DEFINITION_ID) 
    references CCM_CMS.LIFECYLE_DEFINITIONS;

alter table CONTENT_SECTION_LIFECYCLE_DEFINITIONS 
    add constraint FK7daejlunqsnhgky4b92n019a9 
    foreign key (CONTENT_SECTION_ID) 
    references CCM_CMS.CONTENT_SECTIONS;

alter table CONTENT_SECTION_WORKFLOW_TEMPLATES 
    add constraint FKrx08cdjm9tutrp5lvfhgslw48 
    foreign key (WORKFLOW_TEMPLATE_ID) 
    references CCM_CORE.WORKFLOW_TEMPLATES;

alter table CONTENT_SECTION_WORKFLOW_TEMPLATES 
    add constraint FK6kuejkcl9hcbkr8q6bdlatt8q 
    foreign key (CONTENT_SECTION_ID) 
    references CCM_CMS.CONTENT_SECTIONS;