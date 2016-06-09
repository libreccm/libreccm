create table SHORTCUTS (
    shortcut_id int8 not null,
    redirect varchar(1024),
    url_key varchar(1024),
    primary key (shortcut_id)
);

alter table CCM_SHORTCUTS.SHORTCUTS 
    add constraint UK_4otuwtog6qqdbg4e6p8xdpw8h unique (URL_KEY);
