-- drop obsolete columns

alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS 
    drop constraint FKo696ch035fe7rrueol1po13od;

alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS drop column PAGE_MODEL_ID;

