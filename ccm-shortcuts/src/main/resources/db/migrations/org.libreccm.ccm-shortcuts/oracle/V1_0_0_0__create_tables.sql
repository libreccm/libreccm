    
    create table shortcuts (
        shortcut_id number(19,0) not null,
        redirect varchar2(1024 char),
        url_key varchar2(1024 char),
        primary key (shortcut_id)
    );

    create table shortcuts_app (
        object_id number(19,0) not null,
        primary key (object_id)
    );
