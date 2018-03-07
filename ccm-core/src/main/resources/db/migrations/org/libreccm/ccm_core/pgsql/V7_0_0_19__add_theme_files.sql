    alter table CCM_CORE.THEME_DATA_FILES 
        add constraint FK630m2y2p7pp487ofowbefrm89 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.theme_directories 
        add constraint FKrmgyslvw22j87n4cxau5jvsou 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.THEME_FILES 
        add constraint FKfsycb4bt8d0wye7r3n06ekfeu 
        foreign key (PARENT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEMES 
        add constraint FKlat55c5l3fxbykkibrmv7qi4x 
        foreign key (ROOT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEME_DATA_FILES 
        add constraint FK630m2y2p7pp487ofowbefrm89 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.theme_directories 
        add constraint FKrmgyslvw22j87n4cxau5jvsou 
        foreign key (FILE_ID) 
        references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.THEME_FILES 
        add constraint FKfsycb4bt8d0wye7r3n06ekfeu 
        foreign key (PARENT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEMES 
        add constraint FKlat55c5l3fxbykkibrmv7qi4x 
        foreign key (ROOT_DIRECTORY_ID) 
        references CCM_CORE.theme_directories;