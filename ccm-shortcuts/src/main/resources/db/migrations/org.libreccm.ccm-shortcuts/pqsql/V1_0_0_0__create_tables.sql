
    create table shortcuts (
        shortcut_id int8 not null,
        redirect varchar(1024),
        url_key varchar(1024),
        primary key (shortcut_id)
    );

    create table shortcuts_app (
        object_id int8 not null,
        primary key (object_id)
    );