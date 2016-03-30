   create table ONE_TIME_AUTH_TOKENS (
        TOKEN_ID int8 not null,
        PURPOSE varchar(255),
        TOKEN varchar(255),
        VALID_UNIT date,
        USER_ID int8,
        primary key (TOKEN_ID)
    );

    alter table ONE_TIME_AUTH_TOKENS 
        add constraint FK_fvr3t6w3nsm3u29mjuh4tplno 
        foreign key (USER_ID) 
        references CCM_CORE.USERS;