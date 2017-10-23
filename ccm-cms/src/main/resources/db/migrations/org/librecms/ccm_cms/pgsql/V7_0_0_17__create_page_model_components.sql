create table CCM_CMS.CATEGORIZED_ITEM_COMPONENTS (
    COMPONENT_MODEL_ID int8 not null,
    primary key (COMPONENT_MODEL_ID)
);

create table CCM_CMS.CATEGORY_TREE_COMPONENTS (
    SHOW_FULL_TREE boolean,
    COMPONENT_MODEL_ID int8 not null,
    primary key (COMPONENT_MODEL_ID)
);

create table CCM_CMS.CONTENT_ITEM_COMPONENTS (
    MODE varchar(255),
    COMPONENT_MODEL_ID int8 not null,
    primary key (COMPONENT_MODEL_ID)
    );

create table CCM_CMS.FIXED_CONTENT_ITEM_COMPONENTS (
    COMPONENT_MODEL_ID int8 not null,
    CONTENT_ITEM_ID int8,
    primary key (COMPONENT_MODEL_ID)
);

create table CCM_CMS.GREETING_ITEM_COMPONENTS (
    COMPONENT_MODEL_ID int8 not null,
    primary key (COMPONENT_MODEL_ID)
);

create table CCM_CMS.ITEM_LIST_COMPONENTS (
    DESCINDING boolean,
    LIMIT_TO_TYPE varchar(255),
    PAGE_SIZE int4,
    COMPONENT_MODEL_ID int8 not null,
    primary key (COMPONENT_MODEL_ID)
);

alter table CCM_CMS.CATEGORIZED_ITEM_COMPONENTS 
    add constraint FKlraxqtl9cnntdo0qovq340y7b 
    foreign key (COMPONENT_MODEL_ID) 
    references CCM_CMS.CONTENT_ITEM_COMPONENTS;

alter table CCM_CMS.CATEGORY_TREE_COMPONENTS 
    add constraint FKfhc51tkdf705o0sy8sndqpkqa 
    foreign key (COMPONENT_MODEL_ID) 
    references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

alter table CCM_CMS.CONTENT_ITEM_COMPONENTS 
    add constraint FKp83o82kxo2ipa0xo03wxp4dcr 
    foreign key (COMPONENT_MODEL_ID) 
    references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

alter table CCM_CMS.FIXED_CONTENT_ITEM_COMPONENTS 
    add constraint FKlfv2clu7ubk18unio8fyvlbnf 
    foreign key (CONTENT_ITEM_ID) 
    references CCM_CMS.CONTENT_ITEMS;

alter table CCM_CMS.FIXED_CONTENT_ITEM_COMPONENTS 
    add constraint FKkpiuth8e994phxy1x1drh2wf5 
    foreign key (COMPONENT_MODEL_ID) 
    references CCM_CMS.CONTENT_ITEM_COMPONENTS;

alter table CCM_CMS.GREETING_ITEM_COMPONENTS 
    add constraint FK3fble8pmmolb7lmsca8akmb94 
    foreign key (COMPONENT_MODEL_ID) 
    references CCM_CMS.CONTENT_ITEM_COMPONENTS;

alter table CCM_CMS.ITEM_LIST_COMPONENTS 
    add constraint FKje8r8nvkqv8fj7i0eo1pew2yq 
    foreign key (COMPONENT_MODEL_ID) 
    references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

alter table CCM_CMS.ITEM_LIST_ORDER 
    add constraint FKisnil2ibh98y2ws8or6guij21 
    foreign key (ITEM_LIST_ID) 
    references CCM_CMS.ITEM_LIST_COMPONENTS;
