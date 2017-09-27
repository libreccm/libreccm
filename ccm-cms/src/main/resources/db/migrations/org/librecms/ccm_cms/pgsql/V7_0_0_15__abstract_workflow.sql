drop constraint FKrx08cdjm9tutrp5lvfhgslw48;

alter table CCM_CMS.CONTENT_SECTION_WORKFLOW_TEMPLATES 
        add constraint FK1t85m4jehnhd6tyx5dtpavr15 
        foreign key (WORKFLOW_TEMPLATE_ID) 
        references CCM_CORE.WORKFLOWS;

alter table CCM_CMS.CONTENT_TYPES
drop constraint if exists FKhnu9oikw8rpf22lt5fmk41t7k;

alter table CCM_CMS.CONTENT_TYPES 
        add constraint FKpgeccqsr50xwb268ypmfx0r66 
        foreign key (DEFAULT_WORKFLOW) 
        references CCM_CORE.WORKFLOWS;

alter table if exists FKeixdxau4jebw682gd49tdbsjy
drop constraint FKeixdxau4jebw682gd49tdbsjy;
