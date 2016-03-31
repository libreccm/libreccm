create table CCM_CORE.ONE_TIME_AUTH_TOKENS (
        TOKEN_ID bigint not null,
        PURPOSE varchar(255),
        TOKEN varchar(255),
        VALID_UNTIL timestamp,
        USER_ID bigint,
        primary key (TOKEN_ID)
    );

alter table CCM_CORE.ONE_TIME_AUTH_TOKENS 
        add constraint FK_fvr3t6w3nsm3u29mjuh4tplno 
        foreign key (USER_ID) 
        references CCM_CORE.USERS;