alter table CCM_CORE.WORKFLOWS add column ABSTRACT_WORKFLOW boolean;

update CCM_CORE.WORKFLOWS set ABSTRACT_WORKFLOW = true 
where WORKFLOW_ID in (select WORKFLOW_ID from CCM_CORE.WORKFLOW_TEMPLATES);

alter table CCM_CORE.WORKFLOW_TEMPLATES 
drop constraint if exists FK8692vdme4yxnkj1m0k1dw74pk;

alter table CCM_CORE.WORKFLOWS 
drop constraint if exists FKeixdxau4jebw682gd49tdbsjy;

drop table CCM_CORE.WORKFLOW_TEMPLATES cascade;

