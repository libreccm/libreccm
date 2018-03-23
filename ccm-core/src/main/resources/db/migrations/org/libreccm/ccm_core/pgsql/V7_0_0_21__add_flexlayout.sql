    create table CCM_CORE.FLEX_LAYOUT_BOXES (
        BOX_ID int8 not null,
        BOX_ORDER int4,
        BOX_SIZE int4,
        COMPONENT_ID int8,
        LAYOUT_ID int8,
        primary key (BOX_ID)
    );

    create table CCM_CORE.FLEX_LAYOUT_COMPONENTS (
        DIRECTION varchar(255),
        COMPONENT_MODEL_ID int8 not null,
        primary key (COMPONENT_MODEL_ID)
    );

    alter table CCM_CORE.FLEX_LAYOUT_BOXES 
        add constraint FKeiqh69t1lr7u09hjuxfyxsbs 
        foreign key (COMPONENT_ID) 
        references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

    alter table CCM_CORE.FLEX_LAYOUT_BOXES 
        add constraint FKmrobhhqidcf1657ugcgatrd0y 
        foreign key (LAYOUT_ID) 
        references CCM_CORE.FLEX_LAYOUT_COMPONENTS;

    alter table CCM_CORE.FLEX_LAYOUT_COMPONENTS 
        add constraint FK8qxnqt75ikxtedx0xreoeiygg 
        foreign key (COMPONENT_MODEL_ID) 
        references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;
