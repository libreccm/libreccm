
    create table CCM_CMS.ARTICLES (
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.ARTICLES_AUD (
        OBJECT_ID bigint not null,
        REV integer not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.ASSET_TITLES (
        ASSET_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.ASSET_TITLES_AUD (
        REV integer not null,
        ASSET_ID bigint not null,
        LOCALIZED_VALUE clob not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ASSETS (
        ASSET_ID bigint not null,
        UUID varchar(255),
        primary key (ASSET_ID)
    );

    create table CCM_CMS.ASSETS_AUD (
        ASSET_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        UUID varchar(255),
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.ATTACHMENT_LIST_CAPTIONS (
        LIST_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (LIST_ID, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD (
        REV integer not null,
        LIST_ID bigint not null,
        LOCALIZED_VALUE clob not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, LIST_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.attachment_lists (
        LIST_ID bigint not null,
        ASSET_TYPE varchar(1024),
        UUID varchar(255),
        CONTENT_ITEM_ID bigint,
        primary key (LIST_ID)
    );

    create table CCM_CMS.attachment_lists_AUD (
        LIST_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        ASSET_TYPE varchar(1024),
        UUID varchar(255),
        primary key (LIST_ID, REV)
    );

    create table CCM_CMS.AttachmentList_ItemAttachment_AUD (
        REV integer not null,
        LIST_ID bigint not null,
        ATTACHMENT_ID bigint not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, LIST_ID, ATTACHMENT_ID)
    );

    create table CCM_CMS.ATTACHMENTS (
        ATTACHMENT_ID bigint not null,
        SORT_KEY bigint,
        uuid varchar(255),
        ASSET_ID bigint,
        LIST_ID bigint,
        primary key (ATTACHMENT_ID)
    );

    create table CCM_CMS.ATTACHMENTS_AUD (
        ATTACHMENT_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        SORT_KEY bigint,
        uuid varchar(255),
        ASSET_ID bigint,
        primary key (ATTACHMENT_ID, REV)
    );

    create table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS_AUD (
        REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_NAMES (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_NAMES_AUD (
        REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_TITLES (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_TITLES_AUD (
        REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEMS (
        ANCESTORS varchar(1024),
        launchDate date,
        version varchar(255),
        OBJECT_ID bigint not null,
        contentType_OBJECT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTENT_ITEMS_AUD (
        OBJECT_ID bigint not null,
        REV integer not null,
        ANCESTORS varchar(1024),
        launchDate date,
        version varchar(255),
        contentType_OBJECT_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.CONTENT_SECTIONS (
        DEFAULT_LOCALE varchar(255),
        ITEM_RESOLVER_CLASS varchar(1024),
        LABEL varchar(512),
        PAGE_RESOLVER_CLASS varchar(1024),
        TEMPLATE_RESOLVER_CLASS varchar(1024),
        XML_GENERATOR_CLASS varchar(1024),
        OBJECT_ID bigint not null,
        ROOT_ASSETS_FOLDER_ID bigint,
        ROOT_DOCUMENTS_FOLDER_ID bigint,
        STAFF_ROLE_ID bigint,
        VIEWERS_ROLE_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTENT_TYPES (
        ANCESTORS varchar(1024),
        CONTENT_ITEM_CLASS varchar(1024),
        DESCENDANTS varchar(1024),
        mode varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.ContentItem_AttachmentList_AUD (
        REV integer not null,
        CONTENT_ITEM_ID bigint not null,
        LIST_ID bigint not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, CONTENT_ITEM_ID, LIST_ID)
    );

    create table CCM_CMS.REUSABLE_ASSETS (
        OBJECT_ID bigint not null,
        ASSET_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.REUSABLE_ASSETS_AUD (
        OBJECT_ID bigint not null,
        REV integer not null,
        ASSET_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    alter table CCM_CMS.ASSETS 
        add constraint UK_9l2v1u9beyemgjwqx7isbumwh unique (UUID);

    create table CCM_CORE.APPLICATIONS (
        APPLICATION_TYPE varchar(1024) not null,
        PRIMARY_URL varchar(1024) not null,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.ATTACHMENTS (
        ATTACHMENT_ID bigint not null,
        ATTACHMENT_DATA blob,
        DESCRIPTION varchar(255),
        MIME_TYPE varchar(255),
        TITLE varchar(255),
        MESSAGE_ID bigint,
        primary key (ATTACHMENT_ID)
    );

    create table CCM_CORE.CATEGORIES (
        ABSTRACT_CATEGORY boolean,
        CATEGORY_ORDER bigint,
        ENABLED boolean,
        NAME varchar(255) not null,
        UNIQUE_ID varchar(255),
        VISIBLE boolean,
        OBJECT_ID bigint not null,
        PARENT_CATEGORY_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CATEGORIZATIONS (
        CATEGORIZATION_ID bigint not null,
        CATEGORY_ORDER bigint,
        CATEGORY_INDEX boolean,
        OBJECT_ORDER bigint,
        OBJECT_ID bigint,
        CATEGORY_ID bigint,
        primary key (CATEGORIZATION_ID)
    );

    create table CCM_CORE.CATEGORY_DESCRIPTIONS (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.CATEGORY_DOMAINS (
        DOMAIN_KEY varchar(255) not null,
        RELEASED timestamp,
        URI varchar(1024),
        VERSION varchar(255),
        OBJECT_ID bigint not null,
        ROOT_CATEGORY_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CATEGORY_TITLES (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.CCM_OBJECTS (
        OBJECT_ID bigint not null,
        DISPLAY_NAME varchar(255),
        UUID varchar(255),
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CCM_OBJECTS_AUD (
        OBJECT_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        DISPLAY_NAME varchar(255),
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CORE.CCM_REVISIONS (
        id integer not null,
        timestamp bigint not null,
        USER_NAME varchar(255),
        primary key (id)
    );

    create table CCM_CORE.CCM_ROLES (
        ROLE_ID bigint not null,
        NAME varchar(512) not null,
        primary key (ROLE_ID)
    );

    create table CCM_CORE.DIGESTS (
        FREQUENCY integer,
        HEADER varchar(4096) not null,
        NEXT_RUN timestamp,
        DIGEST_SEPARATOR varchar(128) not null,
        SIGNATURE varchar(4096) not null,
        SUBJECT varchar(255) not null,
        OBJECT_ID bigint not null,
        FROM_PARTY_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.DOMAIN_DESCRIPTIONS (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.DOMAIN_OWNERSHIPS (
        OWNERSHIP_ID bigint not null,
        CONTEXT varchar(255),
        DOMAIN_ORDER bigint,
        OWNER_ORDER bigint,
        domain_OBJECT_ID bigint not null,
        owner_OBJECT_ID bigint not null,
        primary key (OWNERSHIP_ID)
    );

    create table CCM_CORE.DOMAIN_TITLES (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_COMPONENT_DESCRIPTIONS (
        COMPONENT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (COMPONENT_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_COMPONENTS (
        ACTIVE boolean,
        ADMIN_NAME varchar(255),
        ATTRIBUTE_STRING varchar(255),
        COMPONENT_ORDER bigint,
        SELECTED boolean,
        OBJECT_ID bigint not null,
        parentComponent_OBJECT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_CONFIRM_EMAIL_LISTENER (
        BODY clob,
        FROM_EMAIL varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_CONFIRM_REDIRECT_LISTENERS (
        URL varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_DATA_DRIVEN_SELECTS (
        MULTIPLE boolean,
        QUERY varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERIES (
        QUERY_ID varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERY_DESCRIPTIONS (
        DATA_QUERY_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (DATA_QUERY_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERY_NAMES (
        DATA_QUERY_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (DATA_QUERY_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_FORMSECTIONS (
        FORMSECTION_ACTION varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_LISTENERS (
        ATTRIBUTE_STRING varchar(255),
        CLASS_NAME varchar(255),
        OBJECT_ID bigint not null,
        widget_OBJECT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_METAOBJECTS (
        CLASS_NAME varchar(255),
        PRETTY_NAME varchar(255),
        PRETTY_PLURAL varchar(255),
        PROPERTIES_FORM varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_OBJECT_TYPES (
        APP_NAME varchar(255),
        CLASS_NAME varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_OPTION_LABELS (
        OPTION_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OPTION_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_OPTIONS (
        PARAMETER_VALUE varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_DESCRIPTIONS (
        PROCESS_LISTENER_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (PROCESS_LISTENER_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_NAMES (
        PROCESS_LISTENER_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (PROCESS_LISTENER_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS (
        LISTENER_CLASS varchar(255),
        PROCESS_LISTENER_ORDER bigint,
        OBJECT_ID bigint not null,
        formSection_OBJECT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_REMOTE_SERVER_POST_LISTENER (
        REMOTE_URL varchar(2048),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_SIMPLE_EMAIL_LISTENERS (
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_TEMPLATE_EMAIL_LISTENERS (
        BODY clob,
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_WIDGET_LABELS (
        OBJECT_ID bigint not null,
        widget_OBJECT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_WIDGETS (
        DEFAULT_VALUE varchar(255),
        PARAMETER_MODEL varchar(255),
        PARAMETER_NAME varchar(255),
        OBJECT_ID bigint not null,
        label_OBJECT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_XML_EMAIL_LISTENERS (
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.GROUP_MEMBERSHIPS (
        MEMBERSHIP_ID bigint not null,
        GROUP_ID bigint,
        MEMBER_ID bigint,
        primary key (MEMBERSHIP_ID)
    );

    create table CCM_CORE.GROUPS (
        PARTY_ID bigint not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.HOSTS (
        HOST_ID bigint not null,
        SERVER_NAME varchar(512),
        SERVER_PORT bigint,
        primary key (HOST_ID)
    );

    create table CCM_CORE.INITS (
        INITIALIZER_ID bigint not null,
        CLASS_NAME varchar(255),
        REQUIRED_BY_ID bigint,
        primary key (INITIALIZER_ID)
    );

    create table CCM_CORE.INSTALLED_MODULES (
        MODULE_ID integer not null,
        MODULE_CLASS_NAME varchar(2048),
        STATUS varchar(255),
        primary key (MODULE_ID)
    );

    create table CCM_CORE.LUCENE_DOCUMENTS (
        DOCUMENT_ID bigint not null,
        CONTENT clob,
        CONTENT_SECTION varchar(512),
        COUNTRY varchar(8),
        CREATED timestamp,
        DIRTY bigint,
        DOCUMENT_LANGUAGE varchar(8),
        LAST_MODIFIED timestamp,
        SUMMARY varchar(4096),
        DOCUMENT_TIMESTAMP timestamp,
        TITLE varchar(4096),
        TYPE varchar(255),
        TYPE_SPECIFIC_INFO varchar(512),
        CREATED_BY_PARTY_ID bigint,
        LAST_MODIFIED_BY bigint,
        primary key (DOCUMENT_ID)
    );

    create table CCM_CORE.LUCENE_INDEXES (
        INDEX_ID bigint not null,
        LUCENE_INDEX_ID bigint,
        HOST_ID bigint,
        primary key (INDEX_ID)
    );

    create table CCM_CORE.MESSAGES (
        BODY varchar(255),
        BODY_MIME_TYPE varchar(255),
        SENT timestamp,
        SUBJECT varchar(255),
        OBJECT_ID bigint not null,
        IN_REPLY_TO_ID bigint,
        SENDER_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.NOTIFICATIONS (
        EXPAND_GROUP boolean,
        EXPUNGE boolean,
        EXPUNGE_MESSAGE boolean,
        FULFILL_DATE timestamp,
        HEADER varchar(4096),
        MAX_RETRIES bigint,
        REQUEST_DATE timestamp,
        SIGNATURE varchar(4096),
        STATUS varchar(32),
        OBJECT_ID bigint not null,
        DIGEST_ID bigint,
        MESSAGE_ID bigint,
        RECEIVER_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.ONE_TIME_AUTH_TOKENS (
        TOKEN_ID bigint not null,
        PURPOSE varchar(255),
        TOKEN varchar(255),
        VALID_UNTIL timestamp,
        USER_ID bigint,
        primary key (TOKEN_ID)
    );

    create table CCM_CORE.PARTIES (
        PARTY_ID bigint not null,
        NAME varchar(256) not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.PERMISSIONS (
        PERMISSION_ID bigint not null,
        CREATION_DATE timestamp,
        CREATION_IP varchar(255),
        granted_privilege varchar(255),
        CREATION_USER_ID bigint,
        GRANTEE_ID bigint,
        OBJECT_ID bigint,
        primary key (PERMISSION_ID)
    );

    create table CCM_CORE.PORTALS (
        TEMPLATE boolean,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.PORTLETS (
        CELL_NUMBER bigint,
        SORT_KEY bigint,
        OBJECT_ID bigint not null,
        PORTAL_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.QUEUE_ITEMS (
        QUEUE_ITEM_ID bigint not null,
        HEADER varchar(4096),
        RECEIVER_ADDRESS varchar(512),
        RETRY_COUNT bigint,
        SIGNATURE varchar(4096),
        SUCCESSFUL_SENDED boolean,
        MESSAGE_ID bigint,
        RECEIVER_ID bigint,
        primary key (QUEUE_ITEM_ID)
    );

    create table CCM_CORE.RESOURCE_DESCRIPTIONS (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TITLES (
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TYPE_DESCRIPTIONS (
        RESOURCE_TYPE_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (RESOURCE_TYPE_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TYPES (
        RESOURCE_TYPE_ID bigint not null,
        SINGLETON boolean,
        TITLE varchar(254) not null,
        EMBEDDED_VIEW boolean,
        FULL_PAGE_VIEW boolean,
        WORKSPACE_APP boolean,
        primary key (RESOURCE_TYPE_ID)
    );

    create table CCM_CORE.RESOURCES (
        CREATED timestamp,
        OBJECT_ID bigint not null,
        parent_OBJECT_ID bigint,
        resourceType_RESOURCE_TYPE_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.ROLE_MEMBERSHIPS (
        MEMBERSHIP_ID bigint not null,
        MEMBER_ID bigint,
        ROLE_ID bigint,
        primary key (MEMBERSHIP_ID)
    );

    create table CCM_CORE.SETTINGS (
        DTYPE varchar(31) not null,
        SETTING_ID bigint not null,
        CONFIGURATION_CLASS varchar(512) not null,
        NAME varchar(512) not null,
        SETTING_VALUE_BOOLEAN boolean,
        SETTING_VALUE_STRING varchar(1024),
        SETTING_VALUE_BIG_DECIMAL decimal(19,2),
        SETTING_VALUE_LONG bigint,
        SETTING_VALUE_DOUBLE double,
        primary key (SETTING_ID)
    );

    create table CCM_CORE.SETTINGS_ENUM_VALUES (
        ENUM_ID bigint not null,
        value varchar(255)
    );

    create table CCM_CORE.SETTINGS_L10N_STR_VALUES (
        ENTRY_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (ENTRY_ID, LOCALE)
    );

    create table CCM_CORE.SETTINGS_STRING_LIST (
        LIST_ID bigint not null,
        value varchar(255)
    );

    create table CCM_CORE.TASK_ASSIGNMENTS (
        TASK_ASSIGNMENT_ID bigint not null,
        ROLE_ID bigint,
        TASK_ID bigint,
        primary key (TASK_ASSIGNMENT_ID)
    );

    create table CCM_CORE.THREADS (
        OBJECT_ID bigint not null,
        ROOT_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.USER_EMAIL_ADDRESSES (
        USER_ID bigint not null,
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
        PARTY_ID bigint not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.WORKFLOW_DESCRIPTIONS (
        WORKFLOW_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (WORKFLOW_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_NAMES (
        WORKFLOW_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (WORKFLOW_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASK_COMMENTS (
        TASK_ID bigint not null,
        COMMENT clob
    );

    create table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES (
        DEPENDS_ON_TASK_ID bigint not null,
        DEPENDENT_TASK_ID bigint not null
    );

    create table CCM_CORE.WORKFLOW_TASK_LABELS (
        TASK_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (TASK_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASKS (
        TASK_ID bigint not null,
        ACTIVE boolean,
        TASK_STATE varchar(512),
        WORKFLOW_ID bigint,
        primary key (TASK_ID)
    );

    create table CCM_CORE.WORKFLOW_TASKS_DESCRIPTIONS (
        TASK_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (TASK_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_USER_TASKS (
        TASK_ID bigint not null,
        ACTIVE boolean,
        TASK_STATE varchar(512),
        WORKFLOW_ID bigint,
        DUE_DATE timestamp,
        DURATION_MINUTES bigint,
        LOCKED boolean,
        START_DATE timestamp,
        LOCKING_USER_ID bigint,
        NOTIFICATION_SENDER bigint,
        primary key (TASK_ID)
    );

    create table CCM_CORE.WORKFLOWS (
        WORKFLOW_ID bigint not null,
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
create sequence hibernate_sequence start with 1 increment by 1;

    create table ContentType_values (
        ContentType_OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (ContentType_OBJECT_ID, LOCALE)
    );

    create table GenericArticle_values (
        GenericArticle_OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob,
        LOCALE varchar(255) not null,
        primary key (GenericArticle_OBJECT_ID, LOCALE)
    );

    create table GenericArticle_values_AUD (
        REV integer not null,
        GenericArticle_OBJECT_ID bigint not null,
        LOCALIZED_VALUE clob not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, GenericArticle_OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    alter table CCM_CMS.ARTICLES 
        add constraint FK2pwvn9v2t2pikcw5hn2oq13q 
        foreign key (OBJECT_ID) 
        references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.ARTICLES_AUD 
        add constraint FKnevu4il5fu4vy2f5twh50kstr 
        foreign key (OBJECT_ID, REV) 
        references CCM_CMS.CONTENT_ITEMS_AUD;

    alter table CCM_CMS.ASSET_TITLES 
        add constraint FKj61sy509dv63u246wlau5f9pa 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.ASSET_TITLES_AUD 
        add constraint FK6yuimrre2oowjo0diw6b00nhe 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ASSET_TITLES_AUD 
        add constraint FKcaockxi21ve0irh06vegc77uu 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ASSETS_AUD 
        add constraint FK4m4op9s9h5qhndcsssbu55gr2 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ASSETS_AUD 
        add constraint FK586j3p95nw2oa6yd06xdw0o5u 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LIST_CAPTIONS 
        add constraint FKdtsxi1x0psj4rsfc15tea5ku5 
        foreign key (LIST_ID) 
        references CCM_CMS.attachment_lists;

    alter table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD 
        add constraint FK727detagt51wmejywhteq4jfs 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD 
        add constraint FK7589vpkxegxs8y3wqjx37tig3 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.attachment_lists 
        add constraint FKdjvwy4y3m6jd6c2ipkgsbwwc3 
        foreign key (CONTENT_ITEM_ID) 
        references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.attachment_lists_AUD 
        add constraint FK83ylk9d0wib4k3l9ob4bx2rgo 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.attachment_lists_AUD 
        add constraint FKrshwuqc6cnb4bnybqsmavsxh3 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.AttachmentList_ItemAttachment_AUD 
        add constraint FKduowjilu7dqfs2oja88tr5oim 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.AttachmentList_ItemAttachment_AUD 
        add constraint FKdtm7qp3n6cojbm9b916plsgtx 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENTS 
        add constraint FKmn0bm137vwr61iy5nb59cjm22 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.ATTACHMENTS 
        add constraint FK1rmlsdulpurab4nq89o5go9oa 
        foreign key (LIST_ID) 
        references CCM_CMS.attachment_lists;

    alter table CCM_CMS.ATTACHMENTS_AUD 
        add constraint FKl19663g6todb5d1e9lok7fl9e 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENTS_AUD 
        add constraint FK4n28sostn1hc8bf43qsp1pyuf 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS 
        add constraint FK6mt4tjnenr79o52wcj99tpeu4 
        foreign key (OBJECT_ID) 
        references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS_AUD 
        add constraint FK12yrysxv4fxa73ker40e883av 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS_AUD 
        add constraint FK4pxuq0pf2hrtireo902t21ocx 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_NAMES 
        add constraint FKijrfangf9s3lyncmod651xyg8 
        foreign key (OBJECT_ID) 
        references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.CONTENT_ITEM_NAMES_AUD 
        add constraint FKq631ee5ollx5xkliowcrt8wkj 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_NAMES_AUD 
        add constraint FKbjaycalit9pa2u7ae5dwjgtky 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_TITLES 
        add constraint FKbvf67lou4ep94pgi6tur6o2gf 
        foreign key (OBJECT_ID) 
        references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.CONTENT_ITEM_TITLES_AUD 
        add constraint FKfbno0rxshoi57y8aehwv3o42j 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_TITLES_AUD 
        add constraint FK4c3exifj1ghwg6htglynlo094 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEMS 
        add constraint FKi1ce005bvnavqy8xlyim60yav 
        foreign key (contentType_OBJECT_ID) 
        references CCM_CMS.CONTENT_TYPES;

    alter table CCM_CMS.CONTENT_ITEMS 
        add constraint FK1fr2q5y1wpmrufruja5ivfpuf 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.CONTENT_ITEMS_AUD 
        add constraint FKsfhj0qok0ksjplvgcaditqekl 
        foreign key (OBJECT_ID, REV) 
        references CCM_CORE.CCM_OBJECTS_AUD;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FKajweudfxaf7g2ydr2hcgqwcib 
        foreign key (ROOT_ASSETS_FOLDER_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FK6g7kw4b6diqa0nks45ilp0vhs 
        foreign key (ROOT_DOCUMENTS_FOLDER_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FKqb579yjmmqbergn0qv68a4rqb 
        foreign key (STAFF_ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FKjntqpyi3g4bx97ive5x8aipyi 
        foreign key (VIEWERS_ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FK72jh0axiiru87i61mppvaiv96 
        foreign key (OBJECT_ID) 
        references CCM_CORE.APPLICATIONS;

    alter table CCM_CMS.CONTENT_TYPES 
        add constraint FK96vwsbqfbdg33ujeeawajr0v4 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.ContentItem_AttachmentList_AUD 
        add constraint FK4notdhn18abev1asay7cmyy84 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ContentItem_AttachmentList_AUD 
        add constraint FK16sw895gdgghrymbirrgrvxsa 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.REUSABLE_ASSETS 
        add constraint FKngdq6f077q6ndqn9o3jc6k14a 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.REUSABLE_ASSETS 
        add constraint FKhvf4mfltp8abbr5u0qgjm2jk2 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.REUSABLE_ASSETS_AUD 
        add constraint FKgyc5gd3cffox4wvjoir6i4gxt 
        foreign key (OBJECT_ID, REV) 
        references CCM_CORE.CCM_OBJECTS_AUD;

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

    alter table CCM_CORE.WORKFLOW_TASKS 
        add constraint FK1693cbc36e4d8gucg8q7sc57e 
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
        add constraint FK_bg60xxg9kerqsxyphbfxulg8y 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table ContentType_values 
        add constraint FK1xrt2eg0gib7xiy65evato7wi 
        foreign key (ContentType_OBJECT_ID) 
        references CCM_CMS.CONTENT_TYPES;

    alter table GenericArticle_values 
        add constraint FK104jagl41apaxmj52tn0c9hl 
        foreign key (GenericArticle_OBJECT_ID) 
        references CCM_CMS.ARTICLES;

    alter table GenericArticle_values_AUD 
        add constraint FKsxpv88pqmp913gegakv7crgbx 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table GenericArticle_values_AUD 
        add constraint FKpk72t5b9rbjeufkv18e3raku2 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;
