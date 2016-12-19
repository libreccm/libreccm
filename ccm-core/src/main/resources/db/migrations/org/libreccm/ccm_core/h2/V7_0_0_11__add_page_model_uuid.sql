alter table CCM_CORE.PAGE_MODELS
    add column MODEL_UUID varchar(255) not null;

alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS
    add column MODEL_UUID varchar(255) not null;