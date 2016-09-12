DROP SCHEMA IF EXISTS ccm_core CASCADE;

DROP SEQUENCE IF EXISTS hibernate_sequence;

CREATE SCHEMA ccm_core;

    create table CCM_CORE.APPLICATIONS (
        APPLICATION_TYPE varchar(1024) not null,
        PRIMARY_URL varchar(1024) not null,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.ATTACHMENTS (
        ATTACHMENT_ID int8 not null,
        ATTACHMENT_DATA oid,
        DESCRIPTION varchar(255),
        MIME_TYPE varchar(255),
        TITLE varchar(255),
        MESSAGE_ID int8,
        primary key (ATTACHMENT_ID)
    );

    create table CCM_CORE.CATEGORIES (
        ABSTRACT_CATEGORY boolean,
        CATEGORY_ORDER int8,
        ENABLED boolean,
        NAME varchar(255) not null,
        UNIQUE_ID varchar(255),
        VISIBLE boolean,
        OBJECT_ID int8 not null,
        PARENT_CATEGORY_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CATEGORIZATIONS (
        CATEGORIZATION_ID int8 not null,
        type varchar(255),
        CATEGORY_ORDER int8,
        CATEGORY_INDEX boolean,
        OBJECT_ORDER int8,
        TYPE varchar(255),
        OBJECT_ID int8,
        CATEGORY_ID int8,
        primary key (CATEGORIZATION_ID)
    );

    create table CCM_CORE.CATEGORY_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.CATEGORY_DOMAINS (
        DOMAIN_KEY varchar(255) not null,
        RELEASED timestamp,
        URI varchar(1024),
        VERSION varchar(255),
        OBJECT_ID int8 not null,
        ROOT_CATEGORY_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CATEGORY_TITLES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.CCM_OBJECTS (
        OBJECT_ID int8 not null,
        DISPLAY_NAME varchar(255),
        UUID varchar(255),
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CCM_OBJECTS_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        REVTYPE int2,
        REVEND int4,
        DISPLAY_NAME varchar(255),
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CORE.CCM_REVISIONS (
        id int4 not null,
        timestamp int8 not null,
        USER_NAME varchar(255),
        primary key (id)
    );

    create table CCM_CORE.CCM_ROLES (
        ROLE_ID int8 not null,
        NAME varchar(512) not null,
        primary key (ROLE_ID)
    );

    create table CCM_CORE.DIGESTS (
        FREQUENCY int4,
        HEADER varchar(4096) not null,
        NEXT_RUN timestamp,
        DIGEST_SEPARATOR varchar(128) not null,
        SIGNATURE varchar(4096) not null,
        SUBJECT varchar(255) not null,
        OBJECT_ID int8 not null,
        FROM_PARTY_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.DOMAIN_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.DOMAIN_OWNERSHIPS (
        OWNERSHIP_ID int8 not null,
        CONTEXT varchar(255),
        DOMAIN_ORDER int8,
        OWNER_ORDER int8,
        domain_OBJECT_ID int8 not null,
        owner_OBJECT_ID int8 not null,
        primary key (OWNERSHIP_ID)
    );

    create table CCM_CORE.DOMAIN_TITLES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_COMPONENT_DESCRIPTIONS (
        COMPONENT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (COMPONENT_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_COMPONENTS (
        ACTIVE boolean,
        ADMIN_NAME varchar(255),
        ATTRIBUTE_STRING varchar(255),
        COMPONENT_ORDER int8,
        SELECTED boolean,
        OBJECT_ID int8 not null,
        parentComponent_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_CONFIRM_EMAIL_LISTENER (
        BODY text,
        FROM_EMAIL varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_CONFIRM_REDIRECT_LISTENERS (
        URL varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_DATA_DRIVEN_SELECTS (
        MULTIPLE boolean,
        QUERY varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERIES (
        QUERY_ID varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERY_DESCRIPTIONS (
        DATA_QUERY_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (DATA_QUERY_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERY_NAMES (
        DATA_QUERY_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (DATA_QUERY_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_FORMSECTIONS (
        FORMSECTION_ACTION varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_LISTENERS (
        ATTRIBUTE_STRING varchar(255),
        CLASS_NAME varchar(255),
        OBJECT_ID int8 not null,
        widget_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_METAOBJECTS (
        CLASS_NAME varchar(255),
        PRETTY_NAME varchar(255),
        PRETTY_PLURAL varchar(255),
        PROPERTIES_FORM varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_OBJECT_TYPES (
        APP_NAME varchar(255),
        CLASS_NAME varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_OPTION_LABELS (
        OPTION_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OPTION_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_OPTIONS (
        PARAMETER_VALUE varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_DESCRIPTIONS (
        PROCESS_LISTENER_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (PROCESS_LISTENER_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_NAMES (
        PROCESS_LISTENER_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (PROCESS_LISTENER_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS (
        LISTENER_CLASS varchar(255),
        PROCESS_LISTENER_ORDER int8,
        OBJECT_ID int8 not null,
        formSection_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_REMOTE_SERVER_POST_LISTENER (
        REMOTE_URL varchar(2048),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_SIMPLE_EMAIL_LISTENERS (
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_TEMPLATE_EMAIL_LISTENERS (
        BODY text,
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_WIDGET_LABELS (
        OBJECT_ID int8 not null,
        widget_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_WIDGETS (
        DEFAULT_VALUE varchar(255),
        PARAMETER_MODEL varchar(255),
        PARAMETER_NAME varchar(255),
        OBJECT_ID int8 not null,
        label_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_XML_EMAIL_LISTENERS (
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.GROUP_MEMBERSHIPS (
        MEMBERSHIP_ID int8 not null,
        GROUP_ID int8,
        MEMBER_ID int8,
        primary key (MEMBERSHIP_ID)
    );

    create table CCM_CORE.GROUPS (
        PARTY_ID int8 not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.HOSTS (
        HOST_ID int8 not null,
        SERVER_NAME varchar(512),
        SERVER_PORT int8,
        primary key (HOST_ID)
    );

    create table CCM_CORE.INITS (
        INITIALIZER_ID int8 not null,
        CLASS_NAME varchar(255),
        REQUIRED_BY_ID int8,
        primary key (INITIALIZER_ID)
    );

    create table CCM_CORE.INSTALLED_MODULES (
        MODULE_ID int4 not null,
        MODULE_CLASS_NAME varchar(2048),
        STATUS varchar(255),
        primary key (MODULE_ID)
    );

    create table CCM_CORE.LUCENE_DOCUMENTS (
        DOCUMENT_ID int8 not null,
        CONTENT text,
        CONTENT_SECTION varchar(512),
        COUNTRY varchar(8),
        CREATED timestamp,
        DIRTY int8,
        DOCUMENT_LANGUAGE varchar(8),
        LAST_MODIFIED timestamp,
        SUMMARY varchar(4096),
        DOCUMENT_TIMESTAMP timestamp,
        TITLE varchar(4096),
        TYPE varchar(255),
        TYPE_SPECIFIC_INFO varchar(512),
        CREATED_BY_PARTY_ID int8,
        LAST_MODIFIED_BY int8,
        primary key (DOCUMENT_ID)
    );

    create table CCM_CORE.LUCENE_INDEXES (
        INDEX_ID int8 not null,
        LUCENE_INDEX_ID int8,
        HOST_ID int8,
        primary key (INDEX_ID)
    );

    create table CCM_CORE.MESSAGES (
        BODY varchar(255),
        BODY_MIME_TYPE varchar(255),
        SENT timestamp,
        SUBJECT varchar(255),
        OBJECT_ID int8 not null,
        IN_REPLY_TO_ID int8,
        SENDER_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.NOTIFICATIONS (
        EXPAND_GROUP boolean,
        EXPUNGE boolean,
        EXPUNGE_MESSAGE boolean,
        FULFILL_DATE timestamp,
        HEADER varchar(4096),
        MAX_RETRIES int8,
        REQUEST_DATE timestamp,
        SIGNATURE varchar(4096),
        STATUS varchar(32),
        OBJECT_ID int8 not null,
        DIGEST_ID int8,
        MESSAGE_ID int8,
        RECEIVER_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.ONE_TIME_AUTH_TOKENS (
        TOKEN_ID int8 not null,
        PURPOSE varchar(255),
        TOKEN varchar(255),
        VALID_UNTIL timestamp,
        USER_ID int8,
        primary key (TOKEN_ID)
    );

    create table CCM_CORE.PARTIES (
        PARTY_ID int8 not null,
        NAME varchar(256) not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.PERMISSIONS (
        PERMISSION_ID int8 not null,
        CREATION_DATE timestamp,
        CREATION_IP varchar(255),
        granted_privilege varchar(255),
        CREATION_USER_ID int8,
        GRANTEE_ID int8,
        OBJECT_ID int8,
        primary key (PERMISSION_ID)
    );

    create table CCM_CORE.PORTALS (
        TEMPLATE boolean,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.PORTLETS (
        CELL_NUMBER int8,
        SORT_KEY int8,
        OBJECT_ID int8 not null,
        PORTAL_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.QUEUE_ITEMS (
        QUEUE_ITEM_ID int8 not null,
        HEADER varchar(4096),
        RECEIVER_ADDRESS varchar(512),
        RETRY_COUNT int8,
        SIGNATURE varchar(4096),
        SUCCESSFUL_SENDED boolean,
        MESSAGE_ID int8,
        RECEIVER_ID int8,
        primary key (QUEUE_ITEM_ID)
    );

    create table CCM_CORE.RESOURCE_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TITLES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TYPE_DESCRIPTIONS (
        RESOURCE_TYPE_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (RESOURCE_TYPE_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TYPES (
        RESOURCE_TYPE_ID int8 not null,
        SINGLETON boolean,
        TITLE varchar(254) not null,
        EMBEDDED_VIEW boolean,
        FULL_PAGE_VIEW boolean,
        WORKSPACE_APP boolean,
        primary key (RESOURCE_TYPE_ID)
    );

    create table CCM_CORE.RESOURCES (
        CREATED timestamp,
        OBJECT_ID int8 not null,
        parent_OBJECT_ID int8,
        resourceType_RESOURCE_TYPE_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.ROLE_MEMBERSHIPS (
        MEMBERSHIP_ID int8 not null,
        MEMBER_ID int8,
        ROLE_ID int8,
        primary key (MEMBERSHIP_ID)
    );

    create table CCM_CORE.SETTINGS (
        DTYPE varchar(31) not null,
        SETTING_ID int8 not null,
        CONFIGURATION_CLASS varchar(512) not null,
        NAME varchar(512) not null,
        SETTING_VALUE_BOOLEAN boolean,
        SETTING_VALUE_DOUBLE float8,
        SETTING_VALUE_STRING varchar(1024),
        SETTING_VALUE_BIG_DECIMAL numeric(19, 2),
        SETTING_VALUE_LONG int8,
        primary key (SETTING_ID)
    );

    create table CCM_CORE.SETTINGS_ENUM_VALUES (
        ENUM_ID int8 not null,
        value varchar(255)
    );

    create table CCM_CORE.SETTINGS_L10N_STR_VALUES (
        ENTRY_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ENTRY_ID, LOCALE)
    );

    create table CCM_CORE.SETTINGS_STRING_LIST (
        LIST_ID int8 not null,
        value varchar(255)
    );

    create table CCM_CORE.TASK_ASSIGNMENTS (
        TASK_ASSIGNMENT_ID int8 not null,
        ROLE_ID int8,
        TASK_ID int8,
        primary key (TASK_ASSIGNMENT_ID)
    );

    create table CCM_CORE.THREADS (
        OBJECT_ID int8 not null,
        ROOT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.USER_EMAIL_ADDRESSES (
        USER_ID int8 not null,
        EMAIL_ADDRESS varchar(512) not null,
        BOUNCING boolean,
        VERIFIED boolean
    );

    create table CCM_CORE.USERS (
        BANNED boolean,
        FAMILY_NAME varchar(512),
        GIVEN_NAME varchar(512),
        PASSWORD varchar(2048),
        PASSWORD_RESET_REQUIRED boolean,
        EMAIL_ADDRESS varchar(512) not null,
        BOUNCING boolean,
        VERIFIED boolean,
        PARTY_ID int8 not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.WORKFLOW_DESCRIPTIONS (
        WORKFLOW_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (WORKFLOW_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_NAMES (
        WORKFLOW_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (WORKFLOW_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASK_COMMENTS (
        TASK_ID int8 not null,
        COMMENT text
    );

    create table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES (
        DEPENDS_ON_TASK_ID int8 not null,
        DEPENDENT_TASK_ID int8 not null
    );

    create table CCM_CORE.WORKFLOW_TASK_LABELS (
        TASK_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (TASK_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASKS (
        TASK_ID int8 not null,
        ACTIVE boolean,
        TASK_STATE varchar(512),
        WORKFLOW_ID int8,
        primary key (TASK_ID)
    );

    create table CCM_CORE.WORKFLOW_TASKS_DESCRIPTIONS (
        TASK_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (TASK_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TEMPLATES (
        WORKFLOW_ID int8 not null,
        primary key (WORKFLOW_ID)
    );

    create table CCM_CORE.WORKFLOW_USER_TASKS (
        DUE_DATE timestamp,
        DURATION_MINUTES int8,
        LOCKED boolean,
        START_DATE timestamp,
        TASK_ID int8 not null,
        LOCKING_USER_ID int8,
        NOTIFICATION_SENDER int8,
        primary key (TASK_ID)
    );

    create table CCM_CORE.WORKFLOWS (
        WORKFLOW_ID int8 not null,
        primary key (WORKFLOW_ID)
    );

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint UK_mb1riernf8a88u3mwl0bgfj8y unique (DOMAIN_KEY);

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint UK_i1xqotjvml7i6ro2jq22fxf5g unique (URI);

    alter table CCM_CORE.CCM_OBJECTS 
        add constraint UK_1cm71jlagvyvcnkqvxqyit3wx unique (UUID);

    alter table CCM_CORE.HOSTS 
        add constraint UK9ramlv6uxwt13v0wj7q0tucsx unique (SERVER_NAME, SERVER_PORT);

    alter table CCM_CORE.INSTALLED_MODULES 
        add constraint UK_11imwgfojyi4hpr18uw9g3jvx unique (MODULE_CLASS_NAME);

    alter table CCM_CORE.SETTINGS 
        add constraint UK5whinfxdaepqs09e5ia9y71uk unique (CONFIGURATION_CLASS, NAME);
create sequence hibernate_sequence start 1 increment 1;

    alter table CCM_CORE.APPLICATIONS 
        add constraint FKatcp9ij6mbkx0nfeig1o6n3lm 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.ATTACHMENTS 
        add constraint FK8ju9hm9baceridp803nislkwb 
        foreign key (MESSAGE_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.CATEGORIES 
        add constraint FKrj3marx99nheur4fqanm0ylur 
        foreign key (PARENT_CATEGORY_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORIES 
        add constraint FKpm291swli2musd0204phta652 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.CATEGORIZATIONS 
        add constraint FKejp0ubk034nfq60v1po6srkke 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.CATEGORIZATIONS 
        add constraint FKoyeipswl876wa6mqwbx0uy83h 
        foreign key (CATEGORY_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORY_DESCRIPTIONS 
        add constraint FKhiwjlmh5vkbu3v3vng1la1qum 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint FKf25vi73cji01w8fgo6ow1dgg 
        foreign key (ROOT_CATEGORY_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint FK58xpmnvciohkom1c16oua4xha 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.CATEGORY_TITLES 
        add constraint FKka9bt9f5br0kji5bcjxcmf6ch 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CCM_OBJECTS_AUD 
        add constraint FKr00eauutiyvocno8ckx6h9nw6 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CORE.CCM_OBJECTS_AUD 
        add constraint FKo5s37ctcdny7tmewjwv7705h5 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CORE.DIGESTS 
        add constraint FKc53g09agnye3w1v4euy3e0gsi 
        foreign key (FROM_PARTY_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.DIGESTS 
        add constraint FK845r9ep6xu6nbt1mvxulwybym 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.DOMAIN_DESCRIPTIONS 
        add constraint FKn4i2dxgn8cqysa62dds6eih6a 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CORE.DOMAIN_OWNERSHIPS 
        add constraint FK47nsasr7jrdwlky5gx0u6e9py 
        foreign key (domain_OBJECT_ID) 
        references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CORE.DOMAIN_OWNERSHIPS 
        add constraint FK3u4hq6yqau4m419b1xva3xpwq 
        foreign key (owner_OBJECT_ID) 
        references CCM_CORE.APPLICATIONS;

    alter table CCM_CORE.DOMAIN_TITLES 
        add constraint FK5p526dsdwn94els6lp5w0hdn4 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CORE.FORMBUILDER_COMPONENT_DESCRIPTIONS 
        add constraint FKfh0k9lj3pf4amfc9bbbss0tr1 
        foreign key (COMPONENT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_COMPONENTS 
        add constraint FKpcpmvyiix023b4g5n4q8nkfca 
        foreign key (parentComponent_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_COMPONENTS 
        add constraint FKt0e0uv00pp1rwhyaltrytghnm 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_CONFIRM_EMAIL_LISTENER 
        add constraint FK48khrbud3xhi2gvsvnlttd8tg 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_CONFIRM_REDIRECT_LISTENERS 
        add constraint FKbyjjt2ufendvje2obtge2l7et 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_DATA_DRIVEN_SELECTS 
        add constraint FK8oriyta1957u7dvbrqk717944 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGETS;

    alter table CCM_CORE.FORMBUILDER_DATA_QUERIES 
        add constraint FKhhaxpeddbtmrnjr5o0fopju3a 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_DATA_QUERY_DESCRIPTIONS 
        add constraint FKsmduu1opoiulkeo2gc8v7lsbn 
        foreign key (DATA_QUERY_ID) 
        references CCM_CORE.FORMBUILDER_DATA_QUERIES;

    alter table CCM_CORE.FORMBUILDER_DATA_QUERY_NAMES 
        add constraint FKju1x82inrw3kguyjuxoetn6gn 
        foreign key (DATA_QUERY_ID) 
        references CCM_CORE.FORMBUILDER_DATA_QUERIES;

    alter table CCM_CORE.FORMBUILDER_FORMSECTIONS 
        add constraint FKnfhsgxp4lvigq2pm33pn4afac 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_LISTENERS 
        add constraint FK33ilyirwoux28yowafgd5xx0o 
        foreign key (widget_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGETS;

    alter table CCM_CORE.FORMBUILDER_LISTENERS 
        add constraint FKlqm76746nq5yrt8ganm474uu0 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_METAOBJECTS 
        add constraint FKf963v6u9mw8pwjmasrw51w8dx 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_OBJECT_TYPES 
        add constraint FKkv337e83rsecf0h3qy8bu7l9w 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_OPTION_LABELS 
        add constraint FKatlsylsvln6yse55eof6wwkj6 
        foreign key (OPTION_ID) 
        references CCM_CORE.FORMBUILDER_OPTIONS;

    alter table CCM_CORE.FORMBUILDER_OPTIONS 
        add constraint FKhe5q71wby9g4i56sotc501h11 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_DESCRIPTIONS 
        add constraint FKcv3iu04gxjk9c0pn6tl8rqqv3 
        foreign key (PROCESS_LISTENER_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_NAMES 
        add constraint FK8rnyb1m6ij3b9hhmhr7klgd4p 
        foreign key (PROCESS_LISTENER_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS 
        add constraint FK7uiaeax8qafm82e5k729ms5ku 
        foreign key (formSection_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_FORMSECTIONS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS 
        add constraint FKbdnloo884qk6gn36jwiqv5rlp 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_REMOTE_SERVER_POST_LISTENER 
        add constraint FKpajvu9m6fj1enm67a9gcb5ii9 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_SIMPLE_EMAIL_LISTENERS 
        add constraint FKsn82ktlq0c9ikijyv8k2bfv4f 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_TEMPLATE_EMAIL_LISTENERS 
        add constraint FK8kjyu72btjsuaaqh4bvd8npns 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_WIDGET_LABELS 
        add constraint FKb1q9bfshcrkwlj7r8w5jb4y8l 
        foreign key (widget_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGETS;

    alter table CCM_CORE.FORMBUILDER_WIDGET_LABELS 
        add constraint FKm1huo6ghk9l5o8buku9v8y6q7 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_WIDGETS 
        add constraint FKs7qq6vxblhmq0rlf87re65jdp 
        foreign key (label_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGET_LABELS;

    alter table CCM_CORE.FORMBUILDER_WIDGETS 
        add constraint FK1wosr4ujbfckdc50u5fgmrhrk 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_XML_EMAIL_LISTENERS 
        add constraint FKjie9co03m7ow4ihig5rk7l8oj 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.GROUP_MEMBERSHIPS 
        add constraint FKq4qnny8ri3eo7eqh4olxco8nk 
        foreign key (GROUP_ID) 
        references CCM_CORE.GROUPS;

    alter table CCM_CORE.GROUP_MEMBERSHIPS 
        add constraint FKc8u86ivkhvoiw6ju8b2p365he 
        foreign key (MEMBER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.GROUPS 
        add constraint FK4f61mlqxw0ct6s7wwpi9m0735 
        foreign key (PARTY_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.INITS 
        add constraint FK3nvvxk10nmq9nfuko8yklqdgc 
        foreign key (REQUIRED_BY_ID) 
        references CCM_CORE.INITS;

    alter table CCM_CORE.LUCENE_DOCUMENTS 
        add constraint FK942kl4yff8rdiwr0pjk2a9g8 
        foreign key (CREATED_BY_PARTY_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.LUCENE_DOCUMENTS 
        add constraint FKc5rs6afx4p9fidabfqsxr5ble 
        foreign key (LAST_MODIFIED_BY) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.LUCENE_INDEXES 
        add constraint FK6gu0yrlviqk07dtb3r02iw43f 
        foreign key (HOST_ID) 
        references CCM_CORE.HOSTS;

    alter table CCM_CORE.MESSAGES 
        add constraint FKph10aehmg9f20pn2w4buki97q 
        foreign key (IN_REPLY_TO_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.MESSAGES 
        add constraint FKjufsx3c3h538fj35h8hgfnb1p 
        foreign key (SENDER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.MESSAGES 
        add constraint FK6w20ao7scwecd9mfwpun2ddqx 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FKqk70c1x1dklhty9ju5t4wukd9 
        foreign key (DIGEST_ID) 
        references CCM_CORE.DIGESTS;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FKtt4fjr2p75og79jxxgd8q8mr 
        foreign key (MESSAGE_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FK2vlnma0ox43j0clx8ead08n5s 
        foreign key (RECEIVER_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FKf423hhiaw1bexpxeh1pnas7qt 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.ONE_TIME_AUTH_TOKENS 
        add constraint FKtplfuphkiorfkttaewb4wmfjc 
        foreign key (USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.PERMISSIONS 
        add constraint FKj9di7pawxgtouxmu2k44bj5c4 
        foreign key (CREATION_USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.PERMISSIONS 
        add constraint FKikx3x0kn9fito23g50v6xbr9f 
        foreign key (GRANTEE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.PERMISSIONS 
        add constraint FKkamckexjnffnt8lay9nqeawhm 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.PORTALS 
        add constraint FK5a2hdrbw03mmgr74vj5nxlpvk 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.PORTLETS 
        add constraint FK9gr5xjt3rx4uhtw7vl6adruol 
        foreign key (PORTAL_ID) 
        references CCM_CORE.PORTALS;

    alter table CCM_CORE.PORTLETS 
        add constraint FKjmx9uebt0gwxkw3xv34niy35f 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.QUEUE_ITEMS 
        add constraint FKtgkwfruv9kjdybf46l02da088 
        foreign key (MESSAGE_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.QUEUE_ITEMS 
        add constraint FKs9aq1hyxstwmvx7fmfifp4x7r 
        foreign key (RECEIVER_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.RESOURCE_DESCRIPTIONS 
        add constraint FKk9arvj5u21rv23ce3cav4opqx 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.RESOURCE_TITLES 
        add constraint FKto4p6n2wklljyf7tmuxtmyfe0 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.RESOURCE_TYPE_DESCRIPTIONS 
        add constraint FKckpihjtv23iahbg3imnpbsr2 
        foreign key (RESOURCE_TYPE_ID) 
        references CCM_CORE.RESOURCE_TYPES;

    alter table CCM_CORE.RESOURCES 
        add constraint FKbo7ibfgodicn9flv2gfo11g5a 
        foreign key (parent_OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.RESOURCES 
        add constraint FK262fbwetpjx3k4uuvw24wsiv 
        foreign key (resourceType_RESOURCE_TYPE_ID) 
        references CCM_CORE.RESOURCE_TYPES;

    alter table CCM_CORE.RESOURCES 
        add constraint FKbjdf8pm4frth8r06ev2qjm88f 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.ROLE_MEMBERSHIPS 
        add constraint FK9m88ywi7rcin7b7jrgh53emrq 
        foreign key (MEMBER_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.ROLE_MEMBERSHIPS 
        add constraint FKcsyogv5m2rgsrmtgnhgkjhfw7 
        foreign key (ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.SETTINGS_ENUM_VALUES 
        add constraint FK8mw4p92s0h3h8bmo8saowu32i 
        foreign key (ENUM_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_L10N_STR_VALUES 
        add constraint FK5knjq7cisej0qfx5dw1y93rou 
        foreign key (ENTRY_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_STRING_LIST 
        add constraint FKqeclqa5sf1g53vxs857tpwrus 
        foreign key (LIST_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.TASK_ASSIGNMENTS 
        add constraint FKe29uwmvxdmol1fjob3auej4qv 
        foreign key (ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.TASK_ASSIGNMENTS 
        add constraint FKc1vovbjg9mp5yegx2fdoutx7u 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_USER_TASKS;

    alter table CCM_CORE.THREADS 
        add constraint FKsx08mpwvwnw97uwdgjs76q39g 
        foreign key (ROOT_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.THREADS 
        add constraint FKp97b1sy1kop07rtapeh5l9fb2 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.USER_EMAIL_ADDRESSES 
        add constraint FKr900l79erul95seyyccf04ufc 
        foreign key (USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.USERS 
        add constraint FKosh928q71aonu6l1kurb417r 
        foreign key (PARTY_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.WORKFLOW_DESCRIPTIONS 
        add constraint FKgx7upkqky82dpxvbs95imfl9l 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_NAMES 
        add constraint FKkxedy9p48avfk45r0bn4uc09i 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
        add constraint FKkfqrf9jdvm7livu5if06w0r5t 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
        add constraint FK1htp420ki24jaswtcum56iawe 
        foreign key (DEPENDENT_TASK_ID) 
        references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
        add constraint FK8rbggnp4yjpab8quvvx800ymy 
        foreign key (DEPENDS_ON_TASK_ID) 
        references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_LABELS 
        add constraint FKf715qud6g9xv2xeb8rrpnv4xs 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASKS 
        add constraint FK1693cbc36e4d8gucg8q7sc57e 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_TASKS_DESCRIPTIONS 
        add constraint FK2s2498d2tpojjrtghq7iyaosv 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TEMPLATES 
        add constraint FK8692vdme4yxnkj1m0k1dw74pk 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_USER_TASKS 
        add constraint FKf09depwj5rgso2dair07vnu33 
        foreign key (LOCKING_USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_USER_TASKS 
        add constraint FK6evo9y34awhdfcyl8gv78qb7f 
        foreign key (NOTIFICATION_SENDER) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_USER_TASKS 
        add constraint FKefpdf9ojplu7loo31hfm0wl2h 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOWS 
        add constraint FKol71r1t83h0qe65gglq43far2 
        foreign key (template_id) 
        references CCM_CORE.WORKFLOW_TEMPLATES;