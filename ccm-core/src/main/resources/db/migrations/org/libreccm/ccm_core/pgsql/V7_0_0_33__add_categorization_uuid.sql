alter table CCM_CORE.CATEGORIZATIONS
    add column UUID varchar(255) not null;

alter table CCM_CORE.CATEGORIZATIONS 
    add constraint UK_da7jus3wn1tr8poyaw9btxbrc unique (UUID);




