alter table CCM_CORE.THEME_FILES 
    add column THEME_ID BIGINT;


alter table CCM_CORE.THEME_FILES 
    add constraint FKke2jj04kjqh91h347g1ut0yff 
    foreign key (THEME_ID) 
    references CCM_CORE.THEMES;