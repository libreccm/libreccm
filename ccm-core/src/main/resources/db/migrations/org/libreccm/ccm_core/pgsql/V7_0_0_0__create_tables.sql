
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
        UNIQUE_ID varchar(255) not null,
        VISIBLE boolean,
        OBJECT_ID int8 not null,
        PARENT_CATEGORY_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.CATEGORIZATIONS (
        CATEGORIZATION_ID int8 not null,
        CATEGORY_ORDER int8,
        CATEGORY_INDEX boolean,
        OBJECT_ORDER int8,
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
        VERSION varchar(255) not null,
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
        primary key (OBJECT_ID)
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

    create table CCM_CORE.CONF_ENTRIES_L10N_STR_VALUES (
        ENTRY_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ENTRY_ID, LOCALE)
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

    create table CCM_CORE.ENUM_CONFIGURATION_ENTRIES_VALUES (
        ENUM_ID int8 not null,
        value varchar(255)
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

    create table CCM_CORE.FORMBUILDER_COMPONENT_DESCRIPTIONS (
        COMPONENT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (COMPONENT_ID, LOCALE)
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

    create table CCM_CORE.FORMBUILDER_OPTIONS (
        PARAMETER_VALUE varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_OPTION_LABELS (
        OPTION_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OPTION_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS (
        LISTENER_CLASS varchar(255),
        PROCESS_LISTENER_ORDER int8,
        OBJECT_ID int8 not null,
        formSection_OBJECT_ID int8,
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

    create table CCM_CORE.FORMBUILDER_WIDGETS (
        DEFAULT_VALUE varchar(255),
        PARAMETER_MODEL varchar(255),
        PARAMETER_NAME varchar(255),
        OBJECT_ID int8 not null,
        label_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_WIDGET_LABELS (
        OBJECT_ID int8 not null,
        widget_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.FORMBUILDER_XML_EMAIL_LISTENERS (
        RECIPIENT varchar(255),
        SUBJECT varchar(255),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.GROUPS (
        PARTY_ID int8 not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.GROUP_MEMBERSHIPS (
        MEMBERSHIP_ID int8 not null,
        GROUP_ID int8,
        MEMBER_ID int8,
        primary key (MEMBERSHIP_ID)
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

    create table CCM_CORE.RESOURCES (
        CREATED timestamp,
        OBJECT_ID int8 not null,
        parent_OBJECT_ID int8,
        resourceType_RESOURCE_TYPE_ID int8,
        primary key (OBJECT_ID)
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

    create table CCM_CORE.RESOURCE_TYPES (
        RESOURCE_TYPE_ID int8 not null,
        SINGLETON boolean,
        TITLE varchar(254) not null,
        EMBEDDED_VIEW boolean,
        FULL_PAGE_VIEW boolean,
        WORKSPACE_APP boolean,
        primary key (RESOURCE_TYPE_ID)
    );

    create table CCM_CORE.RESOURCE_TYPE_DESCRIPTIONS (
        RESOURCE_TYPE_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (RESOURCE_TYPE_ID, LOCALE)
    );

    create table CCM_CORE.ROLE_MEMBERSHIPS (
        MEMBERSHIP_ID int8 not null,
        MEMBER_ID int8,
        ROLE_ID int8,
        primary key (MEMBERSHIP_ID)
    );

    create table CCM_CORE.SETTINGS (
        name varchar(512) not null,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_BIG_DECIMAL (
        entry_value numeric(19, 2),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_BOOLEAN (
        entry_value boolean,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_DOUBLE (
        entry_value float8,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_ENUM (
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_L10N_STRING (
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_LONG (
        entry_value int8,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SETTINGS_STRING (
        entry_value varchar(1024),
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
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

    create table CCM_CORE.USER_EMAIL_ADDRESSES (
        USER_ID int8 not null,
        EMAIL_ADDRESS varchar(512) not null,
        BOUNCING boolean,
        VERIFIED boolean
    );

    create table CCM_CORE.WORKFLOWS (
        WORKFLOW_ID int8 not null,
        primary key (WORKFLOW_ID)
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

    create table CCM_CORE.WORKFLOW_USER_TASKS (
        TASK_ID int8 not null,
        ACTIVE boolean,
        TASK_STATE varchar(512),
        WORKFLOW_ID int8,
        DUE_DATE timestamp,
        DURATION_MINUTES int8,
        LOCKED boolean,
        START_DATE timestamp,
        LOCKING_USER_ID int8,
        NOTIFICATION_SENDER int8,
        primary key (TASK_ID)
    );

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint UK_mb1riernf8a88u3mwl0bgfj8y  unique (DOMAIN_KEY);

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint UK_i1xqotjvml7i6ro2jq22fxf5g  unique (URI);

    alter table CCM_CORE.HOSTS 
        add constraint UK_9ramlv6uxwt13v0wj7q0tucsx  unique (SERVER_NAME, SERVER_PORT);

    alter table CCM_CORE.INSTALLED_MODULES 
        add constraint UK_11imwgfojyi4hpr18uw9g3jvx  unique (MODULE_CLASS_NAME);

    alter table CCM_CORE.APPLICATIONS 
        add constraint FK_sn1sqtx94nhxgv282ymoqiock 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.ATTACHMENTS 
        add constraint FK_fwm2uvhmqg8bmo1d66g0b6be9 
        foreign key (MESSAGE_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.CATEGORIES 
        add constraint FK_4sghd3hxh69xgu68m8uh2axej 
        foreign key (PARENT_CATEGORY_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORIES 
        add constraint FK_pvjwyfbuwafc1mlyevgwwyg49 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.CATEGORIZATIONS 
        add constraint FK_2onruptfmyn5mu8f5j2o4h8i3 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.CATEGORIZATIONS 
        add constraint FK_k43sltpj69u3y5eltkjhumc4p 
        foreign key (CATEGORY_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORY_DESCRIPTIONS 
        add constraint FK_55equbyl81ut4yyt6jms57jwr 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint FK_jyt6c67quitehuh5xe7ulhqvu 
        foreign key (ROOT_CATEGORY_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CATEGORY_DOMAINS 
        add constraint FK_40h1mx7tdlmjvb6x2e04jqgi7 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.CATEGORY_TITLES 
        add constraint FK_954p2g6kwhef5h41pfcda812u 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CORE.CONF_ENTRIES_L10N_STR_VALUES 
        add constraint FK_ftb5yqeoli1m932yp3p8ho74g 
        foreign key (ENTRY_ID) 
        references CCM_CORE.SETTINGS_L10N_STRING;

    alter table CCM_CORE.DIGESTS 
        add constraint FK_3xrcpufumqnh4ke4somt89rvh 
        foreign key (FROM_PARTY_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.DIGESTS 
        add constraint FK_4sxl35dvaj54ck0ikf850h58x 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.DOMAIN_DESCRIPTIONS 
        add constraint FK_12rneohwyp6p66ioyoyobvkxr 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CORE.DOMAIN_OWNERSHIPS 
        add constraint FK_m53bm8ecspukj3qj99q9xa8ox 
        foreign key (domain_OBJECT_ID) 
        references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CORE.DOMAIN_OWNERSHIPS 
        add constraint FK_ce4xhu9ilpdvjsmrsjb739t64 
        foreign key (owner_OBJECT_ID) 
        references CCM_CORE.APPLICATIONS;

    alter table CCM_CORE.DOMAIN_TITLES 
        add constraint FK_98kfhafuv6lmhnpkhurwp9bgm 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CORE.ENUM_CONFIGURATION_ENTRIES_VALUES 
        add constraint FK_ao3evxajxd8y4gy5a6e8ua49j 
        foreign key (ENUM_ID) 
        references CCM_CORE.SETTINGS_ENUM;

    alter table CCM_CORE.FORMBUILDER_COMPONENTS 
        add constraint FK_72108sd6vsqt88g3fb4kl6o81 
        foreign key (parentComponent_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_COMPONENTS 
        add constraint FK_f9xo42yrxdjxqedrk3t2upm9e 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_COMPONENT_DESCRIPTIONS 
        add constraint FK_2njuft67tbfnkxsr62r0bmhh3 
        foreign key (COMPONENT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_CONFIRM_EMAIL_LISTENER 
        add constraint FK_qm4q6qc2p81e349jgpoyxpq10 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_CONFIRM_REDIRECT_LISTENERS 
        add constraint FK_cq44p887dqh2ycd0htku119wf 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_DATA_DRIVEN_SELECTS 
        add constraint FK_qeyxu4t8aqosmoup7ho9qrtae 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGETS;

    alter table CCM_CORE.FORMBUILDER_DATA_QUERIES 
        add constraint FK_6xtng7pfv18ixfpid57grfh4 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_DATA_QUERY_DESCRIPTIONS 
        add constraint FK_2rlo453aslip0ng1fpyv022ld 
        foreign key (DATA_QUERY_ID) 
        references CCM_CORE.FORMBUILDER_DATA_QUERIES;

    alter table CCM_CORE.FORMBUILDER_DATA_QUERY_NAMES 
        add constraint FK_9nqk2rpq4exw708vobkmdcr1s 
        foreign key (DATA_QUERY_ID) 
        references CCM_CORE.FORMBUILDER_DATA_QUERIES;

    alter table CCM_CORE.FORMBUILDER_FORMSECTIONS 
        add constraint FK_anavw6ab288yo2d90axcebv1p 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_LISTENERS 
        add constraint FK_lnlrrafk9r9v072vqtmnkwkou 
        foreign key (widget_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGETS;

    alter table CCM_CORE.FORMBUILDER_LISTENERS 
        add constraint FK_2ynw5cse8kayvi9wqdgg477w0 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_METAOBJECTS 
        add constraint FK_9bx162hal2lqub5m5c21hh31r 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_OBJECT_TYPES 
        add constraint FK_qaj6yd47l5trvvxtnxeao1c33 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_OPTIONS 
        add constraint FK_6s1dxx8lfky4l5ibtd20ouvuj 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_OPTION_LABELS 
        add constraint FK_90c86qtfefh98jcche7rtk5ms 
        foreign key (OPTION_ID) 
        references CCM_CORE.FORMBUILDER_OPTIONS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS 
        add constraint FK_2a4hflqpujuxvx90bsnie3s33 
        foreign key (formSection_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_FORMSECTIONS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENERS 
        add constraint FK_dth0onqirda98fvvpo1rtpjxi 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_DESCRIPTIONS 
        add constraint FK_cynaaq1405ih7epmt4k6vv5m1 
        foreign key (PROCESS_LISTENER_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_NAMES 
        add constraint FK_gpc3rhvwhy9038k7or5ud8mim 
        foreign key (PROCESS_LISTENER_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_REMOTE_SERVER_POST_LISTENER 
        add constraint FK_b6b0wn2j0mps0ml4jh8s46y4r 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_SIMPLE_EMAIL_LISTENERS 
        add constraint FK_33n9b1q1goybwbvvaotnq4n7 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_TEMPLATE_EMAIL_LISTENERS 
        add constraint FK_iqwglkvml7y4yevaq8s1936im 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.FORMBUILDER_WIDGETS 
        add constraint FK_nei20rvwsnawx4u0ywrh22df1 
        foreign key (label_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGET_LABELS;

    alter table CCM_CORE.FORMBUILDER_WIDGETS 
        add constraint FK_rr1oge60scu4a564h7rcra507 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_WIDGET_LABELS 
        add constraint FK_7lp5ywog1suhe11jr3bl28cwg 
        foreign key (widget_OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_WIDGETS;

    alter table CCM_CORE.FORMBUILDER_WIDGET_LABELS 
        add constraint FK_ieiewnctdo2hdqeuxiv7cl1ru 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_COMPONENTS;

    alter table CCM_CORE.FORMBUILDER_XML_EMAIL_LISTENERS 
        add constraint FK_kcfevkdytrk81gj08f4aeh3qu 
        foreign key (OBJECT_ID) 
        references CCM_CORE.FORMBUILDER_PROCESS_LISTENERS;

    alter table CCM_CORE.GROUPS 
        add constraint FK_bm1g1sp4aav32ghhbo04gkakl 
        foreign key (PARTY_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.GROUP_MEMBERSHIPS 
        add constraint FK_8fitvs176l2fpsoplbbsaxpjo 
        foreign key (GROUP_ID) 
        references CCM_CORE.GROUPS;

    alter table CCM_CORE.GROUP_MEMBERSHIPS 
        add constraint FK_7ttmeu1wo1bhgnxvqm5hksbwm 
        foreign key (MEMBER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.INITS 
        add constraint FK_jm1ulcmd86shcy83907ojny4q 
        foreign key (REQUIRED_BY_ID) 
        references CCM_CORE.INITS;

    alter table CCM_CORE.LUCENE_DOCUMENTS 
        add constraint FK_hhbqgpg0ocewhlr2cclrtsj7r 
        foreign key (CREATED_BY_PARTY_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.LUCENE_DOCUMENTS 
        add constraint FK_mp7nlc3u4t38x0cevx0bg022s 
        foreign key (LAST_MODIFIED_BY) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.LUCENE_INDEXES 
        add constraint FK_f5ddcxpneculqmctmixjus42k 
        foreign key (HOST_ID) 
        references CCM_CORE.HOSTS;

    alter table CCM_CORE.MESSAGES 
        add constraint FK_pymp95s2bsv5dke8dxbdmdx1d 
        foreign key (IN_REPLY_TO_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.MESSAGES 
        add constraint FK_7w5nh4eo1l5idhvfwvkv02yyi 
        foreign key (SENDER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.MESSAGES 
        add constraint FK_t98lp1382qxby5c7b34j238pc 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FK_a2hr4wa8qqnoj0njlrkuak3s6 
        foreign key (DIGEST_ID) 
        references CCM_CORE.DIGESTS;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FK_ck8hytjcms2iwen7q538n49nu 
        foreign key (MESSAGE_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FK_lp67f9mq0basheao3o81xj0xh 
        foreign key (RECEIVER_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.NOTIFICATIONS 
        add constraint FK_2aqx4bgfyhhh4g3pvvjh8hy0w 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.PERMISSIONS 
        add constraint FK_7f7dd6k54fi1vy3llbvrer061 
        foreign key (CREATION_USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.PERMISSIONS 
        add constraint FK_cnt8ay16396ldn10w9yqfvtib 
        foreign key (GRANTEE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.PERMISSIONS 
        add constraint FK_5d855uu7512wakcver0bvdc3f 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.PORTALS 
        add constraint FK_2san7d6vxf5jhesvar5hq57v4 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.PORTLETS 
        add constraint FK_46ty07r54th9qc87pyi31jdqs 
        foreign key (PORTAL_ID) 
        references CCM_CORE.PORTALS;

    alter table CCM_CORE.PORTLETS 
        add constraint FK_r0tybwnahtdoo68tbna9q3s75 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.QUEUE_ITEMS 
        add constraint FK_kskdba7a8ytgc5fxen06peg7 
        foreign key (MESSAGE_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.QUEUE_ITEMS 
        add constraint FK_iccfxv2glwbqa465s8125ftgm 
        foreign key (RECEIVER_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.RESOURCES 
        add constraint FK_ceqi7mfjyk4vdoiyie09kmgj 
        foreign key (parent_OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.RESOURCES 
        add constraint FK_eodj9xd1rmdokm4c3ir1l7s4d 
        foreign key (resourceType_RESOURCE_TYPE_ID) 
        references CCM_CORE.RESOURCE_TYPES;

    alter table CCM_CORE.RESOURCES 
        add constraint FK_f600trvtav1r0n6oy7nri9wry 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.RESOURCE_DESCRIPTIONS 
        add constraint FK_pcahs6vr1ajb3a4mh0vi4stuy 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.RESOURCE_TITLES 
        add constraint FK_brvlxvpy2f1n67562twvvux7s 
        foreign key (OBJECT_ID) 
        references CCM_CORE.RESOURCES;

    alter table CCM_CORE.RESOURCE_TYPE_DESCRIPTIONS 
        add constraint FK_7860pdhhck6opa22gc9u0pgfu 
        foreign key (RESOURCE_TYPE_ID) 
        references CCM_CORE.RESOURCE_TYPES;

    alter table CCM_CORE.ROLE_MEMBERSHIPS 
        add constraint FK_hueyk522he8t6fa1blnpcslap 
        foreign key (MEMBER_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.ROLE_MEMBERSHIPS 
        add constraint FK_eykbm84ndwgpqsr48wekhdoqj 
        foreign key (ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.SETTINGS 
        add constraint FK_3k0t3in140j6wj6eq5olwjgu 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.SETTINGS_BIG_DECIMAL 
        add constraint FK_9mbdc1rjkm80edyuijnkwl6ak 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_BOOLEAN 
        add constraint FK_1mjjvpjxpwicyv8im6mumc7ug 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_DOUBLE 
        add constraint FK_kejnkuyk89tw59xg550kugwb5 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_ENUM 
        add constraint FK_fgrfc2qbl2f2t1l0ku8wo2e5r 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_L10N_STRING 
        add constraint FK_evnyfg9udprxmbginhc4o0is9 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_LONG 
        add constraint FK_2l4bw7pbq3koj81cjyoqpenjj 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.SETTINGS_STRING 
        add constraint FK_naonte6jut7b842icvp9ahino 
        foreign key (OBJECT_ID) 
        references CCM_CORE.SETTINGS;

    alter table CCM_CORE.TASK_ASSIGNMENTS 
        add constraint FK_klh64or0yq26c63181j1tps2o 
        foreign key (ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.TASK_ASSIGNMENTS 
        add constraint FK_fu6ukne6hj8ihlfxtmp17xpfj 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_USER_TASKS;

    alter table CCM_CORE.THREADS 
        add constraint FK_oopqroe5a8fg932teo0cyifcv 
        foreign key (ROOT_ID) 
        references CCM_CORE.MESSAGES;

    alter table CCM_CORE.THREADS 
        add constraint FK_n86cmt6poesgsr4g4c4q07i9f 
        foreign key (OBJECT_ID) 
        references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.USERS 
        add constraint FK_9gwih54tm0rn63e536f6s9oti 
        foreign key (PARTY_ID) 
        references CCM_CORE.PARTIES;

    alter table CCM_CORE.USER_EMAIL_ADDRESSES 
        add constraint FK_tp5wms6tgfl827ihqbcgskusy 
        foreign key (USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_DESCRIPTIONS 
        add constraint FK_sp01mgi5mi5wbwrh8ivnfpw2n 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_NAMES 
        add constraint FK_rmkgykysvk7su7h5tij67p2r3 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_TASKS 
        add constraint FK_bawikoiw1k0bil1bvwq5qpa0j 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_USER_TASKS 
        add constraint FK_byuic3urkanoiqjnf6awfqmyk 
        foreign key (LOCKING_USER_ID) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_USER_TASKS 
        add constraint FK_2dtlvmuapubq81quny4elndh 
        foreign key (NOTIFICATION_SENDER) 
        references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_USER_TASKS 
        add constraint FK_bg60xxg9kerqsxyphbfxulg8y 
        foreign key (WORKFLOW_ID) 
        references CCM_CORE.WORKFLOWS;

    create sequence hibernate_sequence start 1 increment 1;