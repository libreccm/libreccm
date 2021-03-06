
    create table CCM_CMS.ARTICLE_TEXTS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.ARTICLE_TEXTS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

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
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.ASSET_TITLES_AUD (
       REV integer not null,
        ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ASSETS (
       OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.ASSETS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.ATTACHMENT_LIST_CAPTIONS (
       LIST_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (LIST_ID, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD (
       REV integer not null,
        LIST_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, LIST_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS (
       LIST_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (LIST_ID, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS_AUD (
       REV integer not null,
        LIST_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, LIST_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LISTS (
       LIST_ID bigint not null,
        NAME varchar(255),
        LIST_ORDER bigint,
        UUID varchar(255),
        ITEM_ID bigint,
        primary key (LIST_ID)
    );

    create table CCM_CMS.ATTACHMENT_LISTS_AUD (
       LIST_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        NAME varchar(255),
        LIST_ORDER bigint,
        UUID varchar(255),
        ITEM_ID bigint,
        primary key (LIST_ID, REV)
    );

    create table CCM_CMS.ATTACHMENTS (
       ATTACHMENT_ID bigint not null,
        SORT_KEY bigint,
        uuid varchar(255),
        ASSET_ID bigint,
        ATTACHMENT_LIST_ID bigint,
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
        ATTACHMENT_LIST_ID bigint,
        primary key (ATTACHMENT_ID, REV)
    );

    create table CCM_CMS.AUDIO_ASSETS (
       OBJECT_ID bigint not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.AUDIO_ASSETS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.BINARY_ASSET_DESCRIPTIONS (
       ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.BINARY_ASSET_DESCRIPTIONS_AUD (
       REV integer not null,
        ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.BINARY_ASSETS (
       ASSET_DATA blob,
        FILENAME varchar(512) not null,
        MIME_TYPE varchar(512) not null,
        DATA_SIZE bigint,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.BINARY_ASSETS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        ASSET_DATA blob,
        FILENAME varchar(512),
        MIME_TYPE varchar(512),
        DATA_SIZE bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.BOOKMARK_DESCRIPTIONS (
       ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.BOOKMARK_DESCRIPTIONS_AUD (
       REV integer not null,
        ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.BOOKMARKS (
       URL varchar(2048) not null,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.BOOKMARKS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        URL varchar(2048),
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.CATEGORIZED_ITEM_COMPONENTS (
       COMPONENT_MODEL_ID bigint not null,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CMS.CATEGORY_TREE_COMPONENTS (
       SHOW_FULL_TREE boolean,
        COMPONENT_MODEL_ID bigint not null,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CMS.CONTACT_ENTRIES (
       CONTACT_ENTRY_ID bigint not null,
        ENTRY_ORDER bigint,
        ENTRY_VALUE varchar(4096),
        CONTACT_ENTRY_KEY_ID bigint,
        CONTACTABLE_ID bigint,
        primary key (CONTACT_ENTRY_ID)
    );

    create table CCM_CMS.CONTACT_ENTRIES_AUD (
       CONTACT_ENTRY_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        ENTRY_ORDER bigint,
        ENTRY_VALUE varchar(4096),
        CONTACT_ENTRY_KEY_ID bigint,
        primary key (CONTACT_ENTRY_ID, REV)
    );

    create table CCM_CMS.CONTACT_ENTRY_KEY_LABELS (
       KEY_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (KEY_ID, LOCALE)
    );

    create table CCM_CMS.CONTACT_ENTRY_KEY_LABELS_AUD (
       REV integer not null,
        KEY_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, KEY_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTACT_ENTRY_KEYS (
       KEY_ID bigint not null,
        ENTRY_KEY varchar(255),
        primary key (KEY_ID)
    );

    create table CCM_CMS.CONTACT_ENTRY_KEYS_AUD (
       KEY_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        ENTRY_KEY varchar(255),
        primary key (KEY_ID, REV)
    );

    create table CCM_CMS.CONTACTABLE_ENTITIES (
       OBJECT_ID bigint not null,
        POSTAL_ADDRESS_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTACTABLE_ENTITIES_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        POSTAL_ADDRESS_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.ContactableEntity_ContactEntry_AUD (
       REV integer not null,
        CONTACTABLE_ID bigint not null,
        CONTACT_ENTRY_ID bigint not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, CONTACTABLE_ID, CONTACT_ENTRY_ID)
    );

    create table CCM_CMS.CONTENT_ITEM_COMPONENTS (
       MODE varchar(255),
        COMPONENT_MODEL_ID bigint not null,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_NAMES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_NAMES_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_TITLES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_TITLES_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEMS (
       ANCESTORS varchar(1024),
        CREATION_DATE timestamp,
        CREATION_USER_NAME varchar(255),
        ITEM_UUID varchar(255) not null,
        LAST_MODIFIED timestamp,
        LAST_MODIFYING_USER_NAME varchar(255),
        LAUNCH_DATE date,
        VERSION varchar(255),
        OBJECT_ID bigint not null,
        CONTENT_TYPE_ID bigint,
        LIFECYCLE_ID bigint,
        WORKFLOW_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTENT_ITEMS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        ANCESTORS varchar(1024),
        ITEM_UUID varchar(255),
        LAUNCH_DATE date,
        VERSION varchar(255),
        CONTENT_TYPE_ID bigint,
        LIFECYCLE_ID bigint,
        WORKFLOW_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.CONTENT_SECTION_LIFECYCLE_DEFINITIONS (
       CONTENT_SECTION_ID bigint not null,
        LIFECYCLE_DEFINITION_ID bigint not null
    );

    create table CCM_CMS.CONTENT_SECTION_ROLES (
       SECTION_ID bigint not null,
        ROLE_ID bigint not null
    );

    create table CCM_CMS.CONTENT_SECTION_WORKFLOW_TEMPLATES (
       CONTENT_SECTION_ID bigint not null,
        WORKFLOW_TEMPLATE_ID bigint not null
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
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTENT_TYPE_DESCRIPTIONS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_TYPE_LABELS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_TYPES (
       ANCESTORS varchar(1024),
        CONTENT_ITEM_CLASS varchar(1024),
        DESCENDANTS varchar(1024),
        TYPE_MODE varchar(255),
        OBJECT_ID bigint not null,
        CONTENT_SECTION_ID bigint,
        DEFAULT_LIFECYCLE_ID bigint,
        DEFAULT_WORKFLOW bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.EVENT_COSTS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_COSTS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_DATES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_DATES_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_LOCATIONS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_LOCATIONS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_MAIN_CONTRIBUTORS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_MAIN_CONTRIBUTORS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_TEXTS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_TEXTS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_TYPES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_TYPES_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENTS (
       END_DATE timestamp,
        MAP_LINK varchar(255),
        START_DATE timestamp not null,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.EVENTS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        END_DATE timestamp,
        MAP_LINK varchar(255),
        START_DATE timestamp,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.EXTERNAL_AUDIO_ASSETS (
       OBJECT_ID bigint not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.EXTERNAL_VIDEO_ASSETS (
       OBJECT_ID bigint not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.EXTERNAL_VIDEO_ASSETS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.FILES (
       OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.FILES_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.FIXED_CONTENT_ITEM_COMPONENTS (
       COMPONENT_MODEL_ID bigint not null,
        CONTENT_ITEM_ID bigint,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CMS.FOLDER_CONTENT_SECTION_MAP (
       CONTENT_SECTION_ID bigint,
        FOLDER_ID bigint not null,
        primary key (FOLDER_ID)
    );

    create table CCM_CMS.FOLDERS (
       TYPE varchar(255) not null,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.GREETING_ITEM_COMPONENTS (
       COMPONENT_MODEL_ID bigint not null,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CMS.IMAGES (
       HEIGHT bigint,
        WIDTH bigint,
        OBJECT_ID bigint not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.IMAGES_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        HEIGHT bigint,
        WIDTH bigint,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.ITEM_LIST_COMPONENTS (
       DESCINDING boolean,
        LIMIT_TO_TYPE varchar(255),
        PAGE_SIZE integer,
        COMPONENT_MODEL_ID bigint not null,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CMS.ITEM_LIST_ORDER (
       ITEM_LIST_ID bigint not null,
        LIST_ORDER varchar(255)
    );

    create table CCM_CMS.LEGAL_METADATA (
       CREATOR varchar(255),
        PUBLISHER varchar(255),
        RIGHTS_HOLDER varchar(512),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.LEGAL_METADATA_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        CREATOR varchar(255),
        PUBLISHER varchar(255),
        RIGHTS_HOLDER varchar(512),
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS (
       LEGAL_METADATA_ID bigint not null,
        CONTRIBUTORS varchar(255)
    );

    create table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS_AUD (
       REV integer not null,
        LEGAL_METADATA_ID bigint not null,
        CONTRIBUTORS varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, LEGAL_METADATA_ID, CONTRIBUTORS)
    );

    create table CCM_CMS.LEGAL_METADATA_RIGHTS (
       ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.LEGAL_METADATA_RIGHTS_AUD (
       REV integer not null,
        ASSET_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_DEFINITION_DESCRIPTIONS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_DEFINITION_LABELS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_PHASE_DEFINITION_DESCRIPTIONS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_PHASE_DEFINITION_LABELS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_PHASE_DEFINITIONS (
       PHASE_DEFINITION_ID bigint not null,
        DEFAULT_DELAY bigint,
        DEFAULT_DURATION bigint,
        DEFAULT_LISTENER varchar(1024),
        LIFECYCLE_DEFINITION_ID bigint,
        primary key (PHASE_DEFINITION_ID)
    );

    create table CCM_CMS.LIFECYCLES (
       LIFECYCLE_ID bigint not null,
        END_DATE_TIME date,
        FINISHED boolean,
        LISTENER varchar(1024),
        START_DATE_TIME date,
        STARTED boolean,
        UUID varchar(255),
        DEFINITION_ID bigint,
        primary key (LIFECYCLE_ID)
    );

    create table CCM_CMS.LIFECYLE_DEFINITIONS (
       LIFECYCLE_DEFINITION_ID bigint not null,
        DEFAULT_LISTENER varchar(1024),
        UUID varchar(255),
        primary key (LIFECYCLE_DEFINITION_ID)
    );

    create table CCM_CMS.LIFECYLE_PHASES (
       PHASE_ID bigint not null,
        END_DATE_TIME date,
        FINISHED boolean,
        LISTENER varchar(1024),
        START_DATE_TIME date,
        STARTED boolean,
        DEFINITION_ID bigint,
        LIFECYCLE_ID bigint,
        primary key (PHASE_ID)
    );

    create table CCM_CMS.MPA_SECTION_TEXTS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.MPA_SECTION_TEXTS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.MPA_SECTION_TITLES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.MPA_SECTION_TITLES_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.MPA_SUMMARIES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.MPA_SUMMARIES_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.MULTIPART_ARTICLE_SECTIONS (
       SECTION_ID bigint not null,
        PAGE_BREAK boolean,
        RANK integer,
        MULTIPART_ARTICLE_ID bigint,
        primary key (SECTION_ID)
    );

    create table CCM_CMS.MULTIPART_ARTICLE_SECTIONS_AUD (
       SECTION_ID bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        REVEND integer,
        PAGE_BREAK boolean,
        RANK integer,
        primary key (SECTION_ID, REV)
    );

    create table CCM_CMS.MULTIPART_ARTICLES (
       OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.MULTIPART_ARTICLES_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.MultiPartArticle_MultiPartArticleSection_AUD (
       REV integer not null,
        MULTIPART_ARTICLE_ID bigint not null,
        SECTION_ID bigint not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, MULTIPART_ARTICLE_ID, SECTION_ID)
    );

    create table CCM_CMS.NEWS (
       HOMEPAGE boolean,
        NEWS_DATE date not null,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.NEWS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        HOMEPAGE boolean,
        NEWS_DATE date,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.NEWS_TEXTS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.NEWS_TEXTS_AUD (
       REV integer not null,
        OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ORGANIZATIONS (
       NAME varchar(1024),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.ORGANIZATIONS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        NAME varchar(1024),
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.PAGE_THEME_CONFIGURATIONS (
       PAGE_ID bigint not null,
        INDEX_PAGE_TEMPLATE varchar(255),
        ITEM_PAGE_TEMPLATE varchar(255),
        THEME_NAME varchar(255),
        THEME varchar(255) not null,
        primary key (PAGE_ID, THEME)
    );

    create table CCM_CMS.PAGES (
       OBJECT_ID bigint not null,
        INDEX_PAGE_MODEL_ID bigint,
        ITEM_PAGE_MODEL_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.PAGES_APP (
       OBJECT_ID bigint not null,
        CATEGORY_DOMAIN_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.PERSON_NAMES (
       PERSON_ID bigint not null,
        GIVEN_NAME varchar(255),
        NAME_PREFIX varchar(255),
        SUFFIX varchar(255),
        SURNAME varchar(255)
    );

    create table CCM_CMS.PERSON_NAMES_AUD (
       REV integer not null,
        REVTYPE tinyint not null,
        PERSON_ID bigint not null,
        REVEND integer,
        SURNAME varchar(255),
        NAME_PREFIX varchar(255),
        GIVEN_NAME varchar(255),
        SUFFIX varchar(255),
        primary key (REV, REVTYPE, PERSON_ID)
    );

    create table CCM_CMS.PERSONS (
       BIRTHDATE date,
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.PERSONS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        BIRTHDATE date,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.POSTAL_ADDRESSES (
       ADDRESS varchar(2048),
        CITY varchar(512),
        ISO_COUNTRY_CODE varchar(10),
        POSTAL_CODE varchar(255),
        ADDRESS_STATE varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.POSTAL_ADDRESSES_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        ADDRESS varchar(2048),
        CITY varchar(512),
        ISO_COUNTRY_CODE varchar(10),
        POSTAL_CODE varchar(255),
        ADDRESS_STATE varchar(255),
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.RELATED_LINKS (
       OBJECT_ID bigint not null,
        BOOKMARK_ID bigint,
        TARGET_ITEM bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.RELATED_LINKS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        BOOKMARK_ID bigint,
        TARGET_ITEM bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.SIDE_NOTE_TEXTS (
       SIDE_NOTE_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (SIDE_NOTE_ID, LOCALE)
    );

    create table CCM_CMS.SIDE_NOTE_TEXTS_AUD (
       REV integer not null,
        SIDE_NOTE_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647) not null,
        LOCALE varchar(255) not null,
        REVTYPE tinyint,
        REVEND integer,
        primary key (REV, SIDE_NOTE_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.SIDE_NOTES (
       OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.SIDE_NOTES_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.VIDEO_ASSETS (
       HEIGHT bigint,
        WIDTH bigint,
        OBJECT_ID bigint not null,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.VIDEO_ASSETS_AUD (
       OBJECT_ID bigint not null,
        REV integer not null,
        HEIGHT bigint,
        WIDTH bigint,
        LEGAL_METADATA_ID bigint,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.WORKFLOW_TASKS (
       TASK_TYPE varchar(255),
        TASK_ID bigint not null,
        primary key (TASK_ID)
    );

    alter table CCM_CMS.CONTENT_SECTION_LIFECYCLE_DEFINITIONS 
       add constraint UK_dhbp1f81iaw6sl7tg36xh439e unique (LIFECYCLE_DEFINITION_ID);

    alter table CCM_CMS.CONTENT_SECTION_WORKFLOW_TEMPLATES 
       add constraint UK_goj42ghwu4tf1akfb2r6ensns unique (WORKFLOW_TEMPLATE_ID);

    alter table CCM_CMS.LIFECYCLES 
       add constraint UK_40o4njo54m8c4xlwq6ctnsimd unique (UUID);

    alter table CCM_CMS.LIFECYLE_DEFINITIONS 
       add constraint UK_n6ki3s5im2k2nccpocuctqqe3 unique (UUID);

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
        TYPE varchar(255),
        UUID varchar(255) not null,
        OBJECT_ID bigint,
        CATEGORY_ID bigint,
        primary key (CATEGORIZATION_ID)
    );

    create table CCM_CORE.CATEGORY_DESCRIPTIONS (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
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
        LOCALIZED_VALUE varchar(2147483647),
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
        UUID varchar(255) not null,
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
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.DOMAIN_OWNERSHIPS (
       OWNERSHIP_ID bigint not null,
        CONTEXT varchar(255),
        DOMAIN_ORDER bigint,
        OWNER_ORDER bigint,
        UUID varchar(255) not null,
        domain_OBJECT_ID bigint not null,
        owner_OBJECT_ID bigint not null,
        primary key (OWNERSHIP_ID)
    );

    create table CCM_CORE.DOMAIN_TITLES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_COMPONENT_DESCRIPTIONS (
       COMPONENT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
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
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (DATA_QUERY_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_DATA_QUERY_NAMES (
       DATA_QUERY_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
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
        LOCALIZED_VALUE varchar(2147483647),
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
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (PROCESS_LISTENER_ID, LOCALE)
    );

    create table CCM_CORE.FORMBUILDER_PROCESS_LISTENER_NAMES (
       PROCESS_LISTENER_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
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

    create table CCM_CORE.FORMBUILDER_WIDGETS (
       DEFAULT_VALUE varchar(255),
        PARAMETER_MODEL varchar(255),
        PARAMETER_NAME varchar(255),
        OBJECT_ID bigint not null,
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
        UUID varchar(255) not null,
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

    create table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS (
       COMPONENT_MODEL_ID bigint not null,
        CLASS_ATTRIBUTE varchar(512),
        ID_ATTRIBUTE varchar(255),
        COMPONENT_KEY varchar(255),
        MODEL_UUID varchar(255) not null,
        STYLE_ATTRIBUTE varchar(1024),
        UUID varchar(255) not null,
        CONTAINER_ID bigint,
        primary key (COMPONENT_MODEL_ID)
    );

    create table CCM_CORE.PAGE_MODEL_CONTAINER_MODELS (
       CONTAINER_ID bigint not null,
        CONTAINER_UUID varchar(255) not null,
        CONTAINER_KEY varchar(255),
        UUID varchar(255) not null,
        PAGE_MODEL_ID bigint,
        STYLE_ID bigint,
        primary key (CONTAINER_ID)
    );

    create table CCM_CORE.PAGE_MODEL_DESCRIPTIONS (
       PAGE_MODEL_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (PAGE_MODEL_ID, LOCALE)
    );

    create table CCM_CORE.PAGE_MODEL_TITLES (
       PAGE_MODEL_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (PAGE_MODEL_ID, LOCALE)
    );

    create table CCM_CORE.PAGE_MODELS (
       PAGE_MODEL_ID bigint not null,
        LAST_MODIFIED timestamp,
        MODEL_UUID varchar(255) not null,
        NAME varchar(255),
        TYPE varchar(255) not null,
        UUID varchar(255) not null,
        VERSION varchar(255) not null,
        APPLICATION_ID bigint,
        primary key (PAGE_MODEL_ID)
    );

    create table CCM_CORE.PARTIES (
       PARTY_ID bigint not null,
        NAME varchar(256) not null,
        UUID varchar(255) not null,
        primary key (PARTY_ID)
    );

    create table CCM_CORE.PERMISSIONS (
       PERMISSION_ID bigint not null,
        CREATION_DATE timestamp,
        CREATION_IP varchar(255),
        GRANTED_PRIVILEGE varchar(255),
        INHERITED boolean,
        UUID varchar(255) not null,
        CREATION_USER_ID bigint,
        GRANTEE_ID bigint,
        INHERITED_FROM_ID bigint,
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
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TITLES (
       OBJECT_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TYPE_DESCRIPTIONS (
       RESOURCE_TYPE_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (RESOURCE_TYPE_ID, LOCALE)
    );

    create table CCM_CORE.RESOURCE_TYPES (
       RESOURCE_TYPE_ID bigint not null,
        SINGLETON boolean,
        TITLE varchar(254) not null,
        UUID varchar(255) not null,
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

    create table CCM_CORE.ROLE_DESCRIPTIONS (
       ROLE_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (ROLE_ID, LOCALE)
    );

    create table CCM_CORE.ROLE_MEMBERSHIPS (
       MEMBERSHIP_ID bigint not null,
        UUID varchar(255) not null,
        MEMBER_ID bigint,
        ROLE_ID bigint,
        primary key (MEMBERSHIP_ID)
    );

    create table CCM_CORE.SETTINGS (
       DTYPE varchar(31) not null,
        SETTING_ID bigint not null,
        CONFIGURATION_CLASS varchar(512) not null,
        NAME varchar(512) not null,
        SETTING_VALUE_BIG_DECIMAL decimal(19,2),
        SETTING_VALUE_BOOLEAN boolean,
        SETTING_VALUE_LONG bigint,
        SETTING_VALUE_STRING varchar(1024),
        SETTING_VALUE_DOUBLE double,
        primary key (SETTING_ID)
    );

    create table CCM_CORE.SETTINGS_ENUM_VALUES (
       ENUM_ID bigint not null,
        value varchar(255)
    );

    create table CCM_CORE.SETTINGS_L10N_STR_VALUES (
       ENTRY_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (ENTRY_ID, LOCALE)
    );

    create table CCM_CORE.SETTINGS_STRING_LIST (
       LIST_ID bigint not null,
        value varchar(255)
    );

    create table CCM_CORE.SITE_AWARE_APPLICATIONS (
       OBJECT_ID bigint not null,
        SITE_ID bigint,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.SITES (
       DEFAULT_SITE boolean,
        DEFAULT_THEME varchar(255),
        DOMAIN_OF_SITE varchar(255),
        OBJECT_ID bigint not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CORE.STYLE_MEDIA_QUERIES (
       MEDIA_QUERY_ID bigint not null,
        MAX_WIDTH_UNIT varchar(255),
        MAX_WIDTH_VALUE double,
        MEDIA_TYPE varchar(255),
        MIN_WIDTH_UNIT varchar(255),
        MIN_WIDTH_VALUE double,
        primary key (MEDIA_QUERY_ID)
    );

    create table CCM_CORE.STYLE_MEDIA_RULES (
       MEDIA_RULE_ID bigint not null,
        MEDIA_QUERY_ID bigint,
        STYLE_ID bigint,
        primary key (MEDIA_RULE_ID)
    );

    create table CCM_CORE.STYLE_PROPERTIES (
       PROPERTY_ID bigint not null,
        NAME varchar(256),
        PROPERTY_VALUE varchar(4096),
        RULE_ID bigint,
        primary key (PROPERTY_ID)
    );

    create table CCM_CORE.STYLE_RULES (
       RULE_ID bigint not null,
        SELECTOR varchar(2048),
        STYLE_ID bigint,
        primary key (RULE_ID)
    );

    create table CCM_CORE.STYLES (
       STYLE_ID bigint not null,
        STYLENAME varchar(255),
        primary key (STYLE_ID)
    );

    create table CCM_CORE.THEME_DATA_FILES (
       CREATION_DATE timestamp,
        FILE_DATA blob,
        LAST_MODIFIED timestamp,
        FILE_SIZE bigint,
        TYPE varchar(255),
        FILE_ID bigint not null,
        primary key (FILE_ID)
    );

    create table CCM_CORE.theme_directories (
       FILE_ID bigint not null,
        primary key (FILE_ID)
    );

    create table CCM_CORE.THEME_FILES (
       FILE_ID bigint not null,
        NAME varchar(255) not null,
        FILE_PATH varchar(8192) not null,
        UUID varchar(255) not null,
        VERSION varchar(255),
        PARENT_DIRECTORY_ID bigint,
        THEME_ID bigint,
        primary key (FILE_ID)
    );

    create table CCM_CORE.THEMES (
       THEME_ID bigint not null,
        NAME varchar(255),
        UUID varchar(255),
        VERSION varchar(255),
        ROOT_DIRECTORY_ID bigint,
        primary key (THEME_ID)
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

    create table CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS (
       DUE_DATE timestamp,
        DURATION_MINUTES bigint,
        LOCKED boolean,
        START_DATE timestamp,
        TASK_ID bigint not null,
        LOCKING_USER_ID bigint,
        NOTIFICATION_SENDER bigint,
        primary key (TASK_ID)
    );

    create table CCM_CORE.WORKFLOW_DESCRIPTIONS (
       WORKFLOW_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (WORKFLOW_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_NAMES (
       WORKFLOW_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (WORKFLOW_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASK_ASSIGNMENTS (
       TASK_ASSIGNMENT_ID bigint not null,
        UUID varchar(255) not null,
        ROLE_ID bigint,
        TASK_ID bigint,
        primary key (TASK_ASSIGNMENT_ID)
    );

    create table CCM_CORE.WORKFLOW_TASK_COMMENTS (
       COMMENT_ID bigint not null,
        COMMENT varchar(2147483647),
        UUID varchar(255) not null,
        AUTHOR_ID bigint,
        TASK_ID bigint,
        primary key (COMMENT_ID)
    );

    create table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES (
       TASK_DEPENDENCY_ID bigint not null,
        uuid varchar(255) not null,
        BLOCKED_TASK_ID bigint,
        BLOCKING_TASK_ID bigint,
        primary key (TASK_DEPENDENCY_ID)
    );

    create table CCM_CORE.WORKFLOW_TASK_DESCRIPTIONS (
       TASK_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (TASK_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASK_LABELS (
       TASK_ID bigint not null,
        LOCALIZED_VALUE varchar(2147483647),
        LOCALE varchar(255) not null,
        primary key (TASK_ID, LOCALE)
    );

    create table CCM_CORE.WORKFLOW_TASKS (
       TASK_ID bigint not null,
        ACTIVE boolean,
        TASK_STATE varchar(512),
        UUID varchar(255) not null,
        WORKFLOW_ID bigint,
        primary key (TASK_ID)
    );

    create table CCM_CORE.WORKFLOWS (
       WORKFLOW_ID bigint not null,
        abstract_workflow boolean,
        ACTIVE boolean,
        WORKFLOW_STATE varchar(255),
        TASKS_STATE varchar(255),
        UUID varchar(255) not null,
        OBJECT_ID bigint,
        TEMPLATE_ID bigint,
        primary key (WORKFLOW_ID)
    );

    alter table CCM_CORE.CATEGORIZATIONS 
       add constraint UK_da7jus3wn1tr8poyaw9btxbrc unique (UUID);

    alter table CCM_CORE.CATEGORY_DOMAINS 
       add constraint UK_mb1riernf8a88u3mwl0bgfj8y unique (DOMAIN_KEY);

    alter table CCM_CORE.CATEGORY_DOMAINS 
       add constraint UK_i1xqotjvml7i6ro2jq22fxf5g unique (URI);

    alter table CCM_CORE.CCM_OBJECTS 
       add constraint UK_1cm71jlagvyvcnkqvxqyit3wx unique (UUID);

    alter table CCM_CORE.CCM_ROLES 
       add constraint UK_rfmsjqsq6kagolsod3ufkugll unique (UUID);

    alter table CCM_CORE.DOMAIN_OWNERSHIPS 
       add constraint UK_j86gai9740v9hshascbsboudb unique (UUID);

    alter table CCM_CORE.GROUP_MEMBERSHIPS 
       add constraint UK_kkdoia60bmiwhhdru169p3n9g unique (UUID);

    alter table CCM_CORE.HOSTS 
       add constraint UK9ramlv6uxwt13v0wj7q0tucsx unique (SERVER_NAME, SERVER_PORT);

    alter table CCM_CORE.INSTALLED_MODULES 
       add constraint UK_11imwgfojyi4hpr18uw9g3jvx unique (MODULE_CLASS_NAME);

    alter table CCM_CORE.PARTIES 
       add constraint UK_1hv061qace2mn4loroe3fwdel unique (UUID);

    alter table CCM_CORE.PERMISSIONS 
       add constraint UK_p50se7rdexv7xnkiqsl6ijyti unique (UUID);

    alter table CCM_CORE.RESOURCE_TYPES 
       add constraint UK_ioax2ix2xmq3nw7el5k6orggb unique (UUID);

    alter table CCM_CORE.ROLE_MEMBERSHIPS 
       add constraint UK_82wdq214bfs99eii71fp50s97 unique (UUID);

    alter table CCM_CORE.SETTINGS 
       add constraint UK5whinfxdaepqs09e5ia9y71uk unique (CONFIGURATION_CLASS, NAME);

    alter table CCM_CORE.SITES 
       add constraint UK_kou1h4y4st2m173he44yy8grx unique (DOMAIN_OF_SITE);

    alter table CCM_CORE.WORKFLOW_TASK_ASSIGNMENTS 
       add constraint UK_gv93k167pe9qy3go9vjau1q2t unique (UUID);

    alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
       add constraint UK_4nnedf08odyjxalfkg16fmjoi unique (UUID);

    alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
       add constraint UK_787va2ep8ucoul29qgsoaxnub unique (uuid);

    alter table CCM_CORE.WORKFLOW_TASKS 
       add constraint UK_2u6ruatxij8wfojl8a1eigqqd unique (UUID);

    alter table CCM_CORE.WORKFLOWS 
       add constraint UK_o113id7d1cxql0edsrohlnn9x unique (UUID);
create sequence hibernate_sequence start with 1 increment by 1;

    alter table CCM_CMS.ARTICLE_TEXTS 
       add constraint FK1pel1j53h3t3adh9o5cbje2d3 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ARTICLES;

    alter table CCM_CMS.ARTICLE_TEXTS_AUD 
       add constraint FKa06qks62tieeba607ykdrv3ry 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ARTICLE_TEXTS_AUD 
       add constraint FKljfof07259eofkub5g2dx0jlq 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

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

    alter table CCM_CMS.ASSETS 
       add constraint FKlbiojib44ujxv9eee1sjn67qk 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.ASSETS_AUD 
       add constraint FKi5q560xg9357da8gc5sukqbw8 
       foreign key (OBJECT_ID, REV) 
       references CCM_CORE.CCM_OBJECTS_AUD;

    alter table CCM_CMS.ATTACHMENT_LIST_CAPTIONS 
       add constraint FKeqcryerscpnmqpipwyrvd0lae 
       foreign key (LIST_ID) 
       references CCM_CMS.ATTACHMENT_LISTS;

    alter table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD 
       add constraint FK727detagt51wmejywhteq4jfs 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD 
       add constraint FK7589vpkxegxs8y3wqjx37tig3 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS 
       add constraint FKixgpo00r1cqq5jw1s7v6fchpn 
       foreign key (LIST_ID) 
       references CCM_CMS.ATTACHMENT_LISTS;

    alter table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS_AUD 
       add constraint FKqhqkm6tas9fdmggv4k1vj0nc7 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS_AUD 
       add constraint FKqv2o9jffgok4518fb5c85552l 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LISTS 
       add constraint FKqyj7ifjfyp7kmsj8fiyxn0am3 
       foreign key (ITEM_ID) 
       references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.ATTACHMENT_LISTS_AUD 
       add constraint FKgdt5p8huh1lhk299hkrytqmqc 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LISTS_AUD 
       add constraint FKdn502yobchapgcyj1bu00u67a 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENTS 
       add constraint FKmn0bm137vwr61iy5nb59cjm22 
       foreign key (ASSET_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.ATTACHMENTS 
       add constraint FK3mqbt13sbed2ae0esrps4p0oh 
       foreign key (ATTACHMENT_LIST_ID) 
       references CCM_CMS.ATTACHMENT_LISTS;

    alter table CCM_CMS.ATTACHMENTS_AUD 
       add constraint FKl19663g6todb5d1e9lok7fl9e 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENTS_AUD 
       add constraint FK4n28sostn1hc8bf43qsp1pyuf 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.AUDIO_ASSETS 
       add constraint FKg9tos3it7lflk5o90jluonpev 
       foreign key (LEGAL_METADATA_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.AUDIO_ASSETS 
       add constraint FKgxpsfjlfsk609c0w2te18y90v 
       foreign key (OBJECT_ID) 
       references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.AUDIO_ASSETS_AUD 
       add constraint FKbt11nwbde1en1upceratct6s3 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.BINARY_ASSET_DESCRIPTIONS 
       add constraint FK31kl9gu49nvhcku7gfsro6hqq 
       foreign key (ASSET_ID) 
       references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.BINARY_ASSET_DESCRIPTIONS_AUD 
       add constraint FKhehi2pvqliq0s2jhv661lar7g 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.BINARY_ASSET_DESCRIPTIONS_AUD 
       add constraint FK9f5n81i6j0yopog1hvua2wmxc 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.BINARY_ASSETS 
       add constraint FKltx0jq1u1aflrd20k1c77m8vh 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.BINARY_ASSETS_AUD 
       add constraint FK1qfap4mxprjk7gnjdcvdxr5mv 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.BOOKMARK_DESCRIPTIONS 
       add constraint FKmeydpwmlq0wqw3gab4auiyrqg 
       foreign key (ASSET_ID) 
       references CCM_CMS.BOOKMARKS;

    alter table CCM_CMS.BOOKMARK_DESCRIPTIONS_AUD 
       add constraint FKfff2ein3uhgwyyyajamy3hfwy 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.BOOKMARK_DESCRIPTIONS_AUD 
       add constraint FKtl48flnrkr0upvrc1ksy1o92m 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.BOOKMARKS 
       add constraint FKksnngecvvxmsxdvri4shby2hy 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.BOOKMARKS_AUD 
       add constraint FK47cpxaw9vnnes2dbr6h3toirl 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.CATEGORIZED_ITEM_COMPONENTS 
       add constraint FKlraxqtl9cnntdo0qovq340y7b 
       foreign key (COMPONENT_MODEL_ID) 
       references CCM_CMS.CONTENT_ITEM_COMPONENTS;

    alter table CCM_CMS.CATEGORY_TREE_COMPONENTS 
       add constraint FKfhc51tkdf705o0sy8sndqpkqa 
       foreign key (COMPONENT_MODEL_ID) 
       references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

    alter table CCM_CMS.CONTACT_ENTRIES 
       add constraint FKirtfj8sm4y5myworl5hvs1l78 
       foreign key (CONTACT_ENTRY_KEY_ID) 
       references CCM_CMS.CONTACT_ENTRY_KEYS;

    alter table CCM_CMS.CONTACT_ENTRIES 
       add constraint FKljrrfco44damal9eaqrnfam0m 
       foreign key (CONTACTABLE_ID) 
       references CCM_CMS.CONTACTABLE_ENTITIES;

    alter table CCM_CMS.CONTACT_ENTRIES_AUD 
       add constraint FKib8xp3ab8kdkc0six36f99e2g 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTACT_ENTRIES_AUD 
       add constraint FKrse7ibjqsfnny5t1b2tqqs3pt 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTACT_ENTRY_KEY_LABELS 
       add constraint FK243nk3buqm0pskkr5ifjqfxn5 
       foreign key (KEY_ID) 
       references CCM_CMS.CONTACT_ENTRY_KEYS;

    alter table CCM_CMS.CONTACT_ENTRY_KEY_LABELS_AUD 
       add constraint FK6n995k5gao6v63gfcga3yaxcw 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTACT_ENTRY_KEY_LABELS_AUD 
       add constraint FKdr8ujdpn1ej8l6omlxq8bsxbd 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTACT_ENTRY_KEYS_AUD 
       add constraint FKcvn2b1h1d4uvvmtbf4qf81l0y 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTACT_ENTRY_KEYS_AUD 
       add constraint FKkyy4v3tax8w5htnpkmmt8aec1 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTACTABLE_ENTITIES 
       add constraint FKqefwowr9adclj3xvpfje9rddr 
       foreign key (POSTAL_ADDRESS_ID) 
       references CCM_CMS.POSTAL_ADDRESSES;

    alter table CCM_CMS.CONTACTABLE_ENTITIES 
       add constraint FKhdwlhf3jp8wf5wxjkoynrcspj 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.CONTACTABLE_ENTITIES_AUD 
       add constraint FKjx8trfvt96fkdn6bafnh839id 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.ContactableEntity_ContactEntry_AUD 
       add constraint FKs5tfdp1auj9ocgvfa9ivec517 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ContactableEntity_ContactEntry_AUD 
       add constraint FKskn2ovg24tnnnwd2o8y0biyje 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.CONTENT_ITEM_COMPONENTS 
       add constraint FKp83o82kxo2ipa0xo03wxp4dcr 
       foreign key (COMPONENT_MODEL_ID) 
       references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

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
       add constraint FKg83y3asxi1jr7larwven7ueu0 
       foreign key (CONTENT_TYPE_ID) 
       references CCM_CMS.CONTENT_TYPES;

    alter table CCM_CMS.CONTENT_ITEMS 
       add constraint FKfh1nm46qpw6xcwkmgaqw2iu3h 
       foreign key (LIFECYCLE_ID) 
       references CCM_CMS.LIFECYCLES;

    alter table CCM_CMS.CONTENT_ITEMS 
       add constraint FKl00ldjygr6as8gqbt3j14ke7j 
       foreign key (WORKFLOW_ID) 
       references CCM_CORE.WORKFLOWS;

    alter table CCM_CMS.CONTENT_ITEMS 
       add constraint FK1fr2q5y1wpmrufruja5ivfpuf 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.CONTENT_ITEMS_AUD 
       add constraint FKsfhj0qok0ksjplvgcaditqekl 
       foreign key (OBJECT_ID, REV) 
       references CCM_CORE.CCM_OBJECTS_AUD;

    alter table CCM_CMS.CONTENT_SECTION_LIFECYCLE_DEFINITIONS 
       add constraint FKqnsnk1eju8vrbm7x0wr5od4ll 
       foreign key (LIFECYCLE_DEFINITION_ID) 
       references CCM_CMS.LIFECYLE_DEFINITIONS;

    alter table CCM_CMS.CONTENT_SECTION_LIFECYCLE_DEFINITIONS 
       add constraint FK7daejlunqsnhgky4b92n019a9 
       foreign key (CONTENT_SECTION_ID) 
       references CCM_CMS.CONTENT_SECTIONS;

    alter table CCM_CMS.CONTENT_SECTION_ROLES 
       add constraint FKkn5nygbmub9wd5lxw3402t82d 
       foreign key (ROLE_ID) 
       references CCM_CORE.CCM_ROLES;

    alter table CCM_CMS.CONTENT_SECTION_ROLES 
       add constraint FKgcn76piocmkmvl3b0omv9vkv9 
       foreign key (SECTION_ID) 
       references CCM_CMS.CONTENT_SECTIONS;

    alter table CCM_CMS.CONTENT_SECTION_WORKFLOW_TEMPLATES 
       add constraint FK1t85m4jehnhd6tyx5dtpavr15 
       foreign key (WORKFLOW_TEMPLATE_ID) 
       references CCM_CORE.WORKFLOWS;

    alter table CCM_CMS.CONTENT_SECTION_WORKFLOW_TEMPLATES 
       add constraint FK6kuejkcl9hcbkr8q6bdlatt8q 
       foreign key (CONTENT_SECTION_ID) 
       references CCM_CMS.CONTENT_SECTIONS;

    alter table CCM_CMS.CONTENT_SECTIONS 
       add constraint FKavcn4aakxsb7kt7hmqlx0ecu6 
       foreign key (ROOT_ASSETS_FOLDER_ID) 
       references CCM_CMS.FOLDERS;

    alter table CCM_CMS.CONTENT_SECTIONS 
       add constraint FKd5sahsfsycq3i5icf3122ne8e 
       foreign key (ROOT_DOCUMENTS_FOLDER_ID) 
       references CCM_CMS.FOLDERS;

    alter table CCM_CMS.CONTENT_SECTIONS 
       add constraint FK72jh0axiiru87i61mppvaiv96 
       foreign key (OBJECT_ID) 
       references CCM_CORE.APPLICATIONS;

    alter table CCM_CMS.CONTENT_TYPE_DESCRIPTIONS 
       add constraint FKknyen2aw844b65grp7uys34cb 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTENT_TYPES;

    alter table CCM_CMS.CONTENT_TYPE_LABELS 
       add constraint FK3suusqws1xgffyk3yob7m7dge 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTENT_TYPES;

    alter table CCM_CMS.CONTENT_TYPES 
       add constraint FKriohuo8093its1k5rgoc5yrfc 
       foreign key (CONTENT_SECTION_ID) 
       references CCM_CMS.CONTENT_SECTIONS;

    alter table CCM_CMS.CONTENT_TYPES 
       add constraint FK8s83we1tuh9r3j57dyos69wfa 
       foreign key (DEFAULT_LIFECYCLE_ID) 
       references CCM_CMS.LIFECYLE_DEFINITIONS;

    alter table CCM_CMS.CONTENT_TYPES 
       add constraint FKpgeccqsr50xwb268ypmfx0r66 
       foreign key (DEFAULT_WORKFLOW) 
       references CCM_CORE.WORKFLOWS;

    alter table CCM_CMS.CONTENT_TYPES 
       add constraint FK96vwsbqfbdg33ujeeawajr0v4 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.EVENT_COSTS 
       add constraint FKrbmepytotc73h5inefeih6rea 
       foreign key (OBJECT_ID) 
       references CCM_CMS.EVENTS;

    alter table CCM_CMS.EVENT_COSTS_AUD 
       add constraint FKocok2fj1oflsi16i9guf8bpc6 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_COSTS_AUD 
       add constraint FKr17panho66n1ixh8tdms01e2c 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_DATES 
       add constraint FKfsfsbgoeoi511ll69iy1v7ujs 
       foreign key (OBJECT_ID) 
       references CCM_CMS.EVENTS;

    alter table CCM_CMS.EVENT_DATES_AUD 
       add constraint FK70p2ayg7fexrb9jogdu3vlwfb 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_DATES_AUD 
       add constraint FKklmki82kiy0hwwpfdur2s7l3e 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_LOCATIONS 
       add constraint FK8snwub57evwh6px3n265tcoiv 
       foreign key (OBJECT_ID) 
       references CCM_CMS.EVENTS;

    alter table CCM_CMS.EVENT_LOCATIONS_AUD 
       add constraint FKk5thpb1gaktsk213o53y97hno 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_LOCATIONS_AUD 
       add constraint FKivwe7h7k4myq4rhuh2wkepd9j 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_MAIN_CONTRIBUTORS 
       add constraint FKlmq881mxd08hthm5dy4ayjq0e 
       foreign key (OBJECT_ID) 
       references CCM_CMS.EVENTS;

    alter table CCM_CMS.EVENT_MAIN_CONTRIBUTORS_AUD 
       add constraint FKqgkj5almojvt913heh1f4kro5 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_MAIN_CONTRIBUTORS_AUD 
       add constraint FKh8vhg85li7c8yqjrg2plvkgho 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_TEXTS 
       add constraint FKc46r2g7ry50b9e875dldjhwxp 
       foreign key (OBJECT_ID) 
       references CCM_CMS.EVENTS;

    alter table CCM_CMS.EVENT_TEXTS_AUD 
       add constraint FK82mc7uswliij43std6gwyswj3 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_TEXTS_AUD 
       add constraint FK1s381t783dmpk0fup65mvma0w 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_TYPES 
       add constraint FKhdbj26ubbhmht44qpin7ony29 
       foreign key (OBJECT_ID) 
       references CCM_CMS.EVENTS;

    alter table CCM_CMS.EVENT_TYPES_AUD 
       add constraint FKgby7m27rnb6oeloqycyf4b1kx 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENT_TYPES_AUD 
       add constraint FKhcpvb5q2geclo5vxk0gt815x8 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.EVENTS 
       add constraint FKt56odfo39eq13gcj1bbtngoj7 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.EVENTS_AUD 
       add constraint FK9gofktd490afdwak49x15w6me 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.CONTENT_ITEMS_AUD;

    alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS 
       add constraint FKrwn3rdmqevi618fthojs0xkkq 
       foreign key (LEGAL_METADATA_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS 
       add constraint FK36xjlvslk0vlekn9lsc7x1c7a 
       foreign key (OBJECT_ID) 
       references CCM_CMS.BOOKMARKS;

    alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD 
       add constraint FKp3jndaw4k35wb3d6hg5ng4xww 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.BOOKMARKS_AUD;

    alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS 
       add constraint FK82gxr2se97dl902eu4wvhdvh3 
       foreign key (LEGAL_METADATA_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS 
       add constraint FKps8bq22n1fxy8svnsrui3f0t2 
       foreign key (OBJECT_ID) 
       references CCM_CMS.BOOKMARKS;

    alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS_AUD 
       add constraint FKilxwg8ppd64hl14tojfmupau9 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.BOOKMARKS_AUD;

    alter table CCM_CMS.FILES 
       add constraint FKpg74w39tfbbuqhcy21u61q138 
       foreign key (OBJECT_ID) 
       references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.FILES_AUD 
       add constraint FKdl876a4twd0gkranwqkdmxnwy 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.FIXED_CONTENT_ITEM_COMPONENTS 
       add constraint FKlfv2clu7ubk18unio8fyvlbnf 
       foreign key (CONTENT_ITEM_ID) 
       references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.FIXED_CONTENT_ITEM_COMPONENTS 
       add constraint FKkpiuth8e994phxy1x1drh2wf5 
       foreign key (COMPONENT_MODEL_ID) 
       references CCM_CMS.CONTENT_ITEM_COMPONENTS;

    alter table CCM_CMS.FOLDER_CONTENT_SECTION_MAP 
       add constraint FKnof2m7o4f0ufrugeh4g5wt3g9 
       foreign key (CONTENT_SECTION_ID) 
       references CCM_CMS.CONTENT_SECTIONS;

    alter table CCM_CMS.FOLDER_CONTENT_SECTION_MAP 
       add constraint FKmmb7728dp707dljq282ch47k3 
       foreign key (FOLDER_ID) 
       references CCM_CMS.FOLDERS;

    alter table CCM_CMS.FOLDERS 
       add constraint FK2ag06r5ywtuji2pkt68etlg48 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CATEGORIES;

    alter table CCM_CMS.GREETING_ITEM_COMPONENTS 
       add constraint FK3fble8pmmolb7lmsca8akmb94 
       foreign key (COMPONENT_MODEL_ID) 
       references CCM_CMS.CONTENT_ITEM_COMPONENTS;

    alter table CCM_CMS.IMAGES 
       add constraint FK51ja1101epvl74auenv6sqyev 
       foreign key (LEGAL_METADATA_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.IMAGES 
       add constraint FKmdqranhdstkn6m6d73l15amxs 
       foreign key (OBJECT_ID) 
       references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.IMAGES_AUD 
       add constraint FK4jsrdpe6d8is0ybx2p7sxivwf 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.ITEM_LIST_COMPONENTS 
       add constraint FKje8r8nvkqv8fj7i0eo1pew2yq 
       foreign key (COMPONENT_MODEL_ID) 
       references CCM_CORE.PAGE_MODEL_COMPONENT_MODELS;

    alter table CCM_CMS.ITEM_LIST_ORDER 
       add constraint FKisnil2ibh98y2ws8or6guij21 
       foreign key (ITEM_LIST_ID) 
       references CCM_CMS.ITEM_LIST_COMPONENTS;

    alter table CCM_CMS.LEGAL_METADATA 
       add constraint FKnxl7uyv1ks0qabgeienx2t9d1 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.LEGAL_METADATA_AUD 
       add constraint FKpt3eqil7iij6t5h1lrnjbb5xs 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS 
       add constraint FKf9s3kxi6y5r60wksv5bospmx1 
       foreign key (LEGAL_METADATA_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS_AUD 
       add constraint FKc3lonfk7mn3p14ix96k5u74om 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS_AUD 
       add constraint FKgxxsteesd2em96fj05f0u4men 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.LEGAL_METADATA_RIGHTS 
       add constraint FKhsy9u7nrh3slmkkri3nba7e1 
       foreign key (ASSET_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.LEGAL_METADATA_RIGHTS_AUD 
       add constraint FKe2da3kha2nl6sj0dllhepuxtq 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.LEGAL_METADATA_RIGHTS_AUD 
       add constraint FKr867xswbxlqq6diyqyqnrh670 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.LIFECYCLE_DEFINITION_DESCRIPTIONS 
       add constraint FKsdr5aoogu4b9x95m8qsbe4t0y 
       foreign key (OBJECT_ID) 
       references CCM_CMS.LIFECYLE_DEFINITIONS;

    alter table CCM_CMS.LIFECYCLE_DEFINITION_LABELS 
       add constraint FKt4h71sl91ue18b25pdjty7jex 
       foreign key (OBJECT_ID) 
       references CCM_CMS.LIFECYLE_DEFINITIONS;

    alter table CCM_CMS.LIFECYCLE_PHASE_DEFINITION_DESCRIPTIONS 
       add constraint FKafbeck8qm0nflpt9aedn196ou 
       foreign key (OBJECT_ID) 
       references CCM_CMS.LIFECYCLE_PHASE_DEFINITIONS;

    alter table CCM_CMS.LIFECYCLE_PHASE_DEFINITION_LABELS 
       add constraint FKqysn500b0sp7bu8gy2sf2q8b9 
       foreign key (OBJECT_ID) 
       references CCM_CMS.LIFECYCLE_PHASE_DEFINITIONS;

    alter table CCM_CMS.LIFECYCLE_PHASE_DEFINITIONS 
       add constraint FKq5cwomuc9s1f3fsriq9t35407 
       foreign key (LIFECYCLE_DEFINITION_ID) 
       references CCM_CMS.LIFECYLE_DEFINITIONS;

    alter table CCM_CMS.LIFECYCLES 
       add constraint FK5yx1a2f8g4w95p1ul77sfhow8 
       foreign key (DEFINITION_ID) 
       references CCM_CMS.LIFECYLE_DEFINITIONS;

    alter table CCM_CMS.LIFECYLE_PHASES 
       add constraint FKpqysexvd82e4xd4uibtdfn8j4 
       foreign key (DEFINITION_ID) 
       references CCM_CMS.LIFECYCLE_PHASE_DEFINITIONS;

    alter table CCM_CMS.LIFECYLE_PHASES 
       add constraint FKlh2b1nokqxhf790lt7lhgoisc 
       foreign key (LIFECYCLE_ID) 
       references CCM_CMS.LIFECYCLES;

    alter table CCM_CMS.MPA_SECTION_TEXTS 
       add constraint FKaruovr4oa07syyhvkixfwc17h 
       foreign key (OBJECT_ID) 
       references CCM_CMS.MULTIPART_ARTICLE_SECTIONS;

    alter table CCM_CMS.MPA_SECTION_TEXTS_AUD 
       add constraint FKs4kvqroybq9ldb2rwhr6v8kmt 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MPA_SECTION_TEXTS_AUD 
       add constraint FKpxvtsycad805c8u0vyh7pcb2c 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MPA_SECTION_TITLES 
       add constraint FK7qpmrj6yjvad50k5budn5rag4 
       foreign key (OBJECT_ID) 
       references CCM_CMS.MULTIPART_ARTICLE_SECTIONS;

    alter table CCM_CMS.MPA_SECTION_TITLES_AUD 
       add constraint FKk64mi5911ybptw4slxh8i0lgb 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MPA_SECTION_TITLES_AUD 
       add constraint FKq7f0y31r5tk2nesx0lv53d6sb 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MPA_SUMMARIES 
       add constraint FK5kx5ghrkh6tqa2vms2qabacx8 
       foreign key (OBJECT_ID) 
       references CCM_CMS.MULTIPART_ARTICLES;

    alter table CCM_CMS.MPA_SUMMARIES_AUD 
       add constraint FKmmrabpl2gsrdb2udc76x9o6q7 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MPA_SUMMARIES_AUD 
       add constraint FK3kebu6i1dtwfegp4409hhob4x 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MULTIPART_ARTICLE_SECTIONS 
       add constraint FK30tkd6xp4i1gg6nrse4di2yxx 
       foreign key (MULTIPART_ARTICLE_ID) 
       references CCM_CMS.MULTIPART_ARTICLES;

    alter table CCM_CMS.MULTIPART_ARTICLE_SECTIONS_AUD 
       add constraint FK8xq6k3a1kmnxv9nh5wae80k6k 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MULTIPART_ARTICLE_SECTIONS_AUD 
       add constraint FKsudhdaa9hs73447yik8mdy3ts 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MULTIPART_ARTICLES 
       add constraint FKj7j0ew378cmcta2dfdso4tmey 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.MULTIPART_ARTICLES_AUD 
       add constraint FKacl2u1cx6tmwfb9cpaxstw39k 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.CONTENT_ITEMS_AUD;

    alter table CCM_CMS.MultiPartArticle_MultiPartArticleSection_AUD 
       add constraint FK9vexjsvd62ufkgi4g24qiql70 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.MultiPartArticle_MultiPartArticleSection_AUD 
       add constraint FK4ds2fgwphr74869qkn4e2yia6 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.NEWS 
       add constraint FKl8jhpvtn0lx9drkhhbbuvqqis 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.NEWS_AUD 
       add constraint FK7akvtda3f51espb46xtjalcl2 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.CONTENT_ITEMS_AUD;

    alter table CCM_CMS.NEWS_TEXTS 
       add constraint FK1s5m60rf80iaidktawb3ebmf3 
       foreign key (OBJECT_ID) 
       references CCM_CMS.NEWS;

    alter table CCM_CMS.NEWS_TEXTS_AUD 
       add constraint FKrand9sf233sgkgp8wfoen468l 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.NEWS_TEXTS_AUD 
       add constraint FKotjtrajmmjxussl4pvy2vl7ho 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ORGANIZATIONS 
       add constraint FK77ig0to48xrlfx8qsc0vlfsp6 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTACTABLE_ENTITIES;

    alter table CCM_CMS.ORGANIZATIONS_AUD 
       add constraint FKp0k3bf008pih96sguio80siql 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.CONTACTABLE_ENTITIES_AUD;

    alter table CCM_CMS.PAGE_THEME_CONFIGURATIONS 
       add constraint FK6l6xp6ex6sh2uuxfmeekf6ckn 
       foreign key (PAGE_ID) 
       references CCM_CMS.PAGES;

    alter table CCM_CMS.PAGES 
       add constraint FKqweb08d151ot4ij9io72w3yhx 
       foreign key (INDEX_PAGE_MODEL_ID) 
       references CCM_CORE.PAGE_MODELS;

    alter table CCM_CMS.PAGES 
       add constraint FKg2p2ahbayc2coei72pk1lnenf 
       foreign key (ITEM_PAGE_MODEL_ID) 
       references CCM_CORE.PAGE_MODELS;

    alter table CCM_CMS.PAGES 
       add constraint FKmgmth087tmxwieujn2vs5opbo 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CMS.PAGES_APP 
       add constraint FK5swx0e8pj0mm5t1es0lj4nwlx 
       foreign key (CATEGORY_DOMAIN_ID) 
       references CCM_CORE.CATEGORY_DOMAINS;

    alter table CCM_CMS.PAGES_APP 
       add constraint FKk4jb5fylibg2pbbaypyt6f8lb 
       foreign key (OBJECT_ID) 
       references CCM_CORE.SITE_AWARE_APPLICATIONS;

    alter table CCM_CMS.PERSON_NAMES 
       add constraint FK2yluyhmpuhwxafcbna6u8txrt 
       foreign key (PERSON_ID) 
       references CCM_CMS.PERSONS;

    alter table CCM_CMS.PERSON_NAMES_AUD 
       add constraint FKtqtlwx8pa9ydh009sudtpfxie 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.PERSON_NAMES_AUD 
       add constraint FKs6m8tgbp8agrd5q3klwbtcujg 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.PERSONS 
       add constraint FKiv4ydysjekfx64pkb5v4vd9yj 
       foreign key (OBJECT_ID) 
       references CCM_CMS.CONTACTABLE_ENTITIES;

    alter table CCM_CMS.PERSONS_AUD 
       add constraint FKpup1q3295qkuovaptq8aj5lxp 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.CONTACTABLE_ENTITIES_AUD;

    alter table CCM_CMS.POSTAL_ADDRESSES 
       add constraint FK4vajjjjo8ro0wns58t8f3i782 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.POSTAL_ADDRESSES_AUD 
       add constraint FKcrxgaot6kcp9rbxlg8gpp4grg 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.RELATED_LINKS 
       add constraint FKb517dnfj56oby2s34jp1omuim 
       foreign key (BOOKMARK_ID) 
       references CCM_CMS.BOOKMARKS;

    alter table CCM_CMS.RELATED_LINKS 
       add constraint FK7ts8tmnwxi8kry7cer3egujsv 
       foreign key (TARGET_ITEM) 
       references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.RELATED_LINKS 
       add constraint FK35tv60a9kflo17h6xduvwvgis 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.RELATED_LINKS_AUD 
       add constraint FKiuwk6mcj3h5gccu2aviq3d8lt 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.SIDE_NOTE_TEXTS 
       add constraint FK4mvpioee23u1qswmn1fekipoh 
       foreign key (SIDE_NOTE_ID) 
       references CCM_CMS.SIDE_NOTES;

    alter table CCM_CMS.SIDE_NOTE_TEXTS_AUD 
       add constraint FKiu4ht1tipeal2csdkvws4fnws 
       foreign key (REV) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.SIDE_NOTE_TEXTS_AUD 
       add constraint FKacekagiqks1cj9otxdmryl934 
       foreign key (REVEND) 
       references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.SIDE_NOTES 
       add constraint FKea6cikleenmkgw5bwus22mfr3 
       foreign key (OBJECT_ID) 
       references CCM_CMS.ASSETS;

    alter table CCM_CMS.SIDE_NOTES_AUD 
       add constraint FKl5pkg9mp2ymc2uo4kmlubyp3m 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.ASSETS_AUD;

    alter table CCM_CMS.VIDEO_ASSETS 
       add constraint FKjuywvv7wq9pyid5b6ivyrc0yk 
       foreign key (LEGAL_METADATA_ID) 
       references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.VIDEO_ASSETS 
       add constraint FKqt2cx1r31kqbqkimdld312i9g 
       foreign key (OBJECT_ID) 
       references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.VIDEO_ASSETS_AUD 
       add constraint FKdrx9uu9a03ju7vqvkjretohpk 
       foreign key (OBJECT_ID, REV) 
       references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.WORKFLOW_TASKS 
       add constraint FKoon3rwfmg0lhgbj4un4q3otya 
       foreign key (TASK_ID) 
       references CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS;

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

    alter table CCM_CORE.PAGE_MODEL_COMPONENT_MODELS 
       add constraint FK1uvkayybawff8sqkmerqt60bk 
       foreign key (CONTAINER_ID) 
       references CCM_CORE.PAGE_MODEL_CONTAINER_MODELS;

    alter table CCM_CORE.PAGE_MODEL_CONTAINER_MODELS 
       add constraint FK1c6drneacxveol92vpum79fxb 
       foreign key (PAGE_MODEL_ID) 
       references CCM_CORE.PAGE_MODELS;

    alter table CCM_CORE.PAGE_MODEL_CONTAINER_MODELS 
       add constraint FKoi5wphv3vtwryc19akku28p24 
       foreign key (STYLE_ID) 
       references CCM_CORE.STYLES;

    alter table CCM_CORE.PAGE_MODEL_DESCRIPTIONS 
       add constraint FKcc5d6eqxu1369k8ycyyt6vn3e 
       foreign key (PAGE_MODEL_ID) 
       references CCM_CORE.PAGE_MODELS;

    alter table CCM_CORE.PAGE_MODEL_TITLES 
       add constraint FKj14q9911yhd4js9p6rs21rwjf 
       foreign key (PAGE_MODEL_ID) 
       references CCM_CORE.PAGE_MODELS;

    alter table CCM_CORE.PAGE_MODELS 
       add constraint FKk2lihllrxj89mn3tqv43amafe 
       foreign key (APPLICATION_ID) 
       references CCM_CORE.APPLICATIONS;

    alter table CCM_CORE.PERMISSIONS 
       add constraint FKj9di7pawxgtouxmu2k44bj5c4 
       foreign key (CREATION_USER_ID) 
       references CCM_CORE.USERS;

    alter table CCM_CORE.PERMISSIONS 
       add constraint FKikx3x0kn9fito23g50v6xbr9f 
       foreign key (GRANTEE_ID) 
       references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.PERMISSIONS 
       add constraint FKg56ujjoe0j30pq579rf0l5yc6 
       foreign key (INHERITED_FROM_ID) 
       references CCM_CORE.CCM_OBJECTS;

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

    alter table CCM_CORE.ROLE_DESCRIPTIONS 
       add constraint FKo09bh4j3k3k0ph3awvjwx31ft 
       foreign key (ROLE_ID) 
       references CCM_CORE.CCM_ROLES;

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

    alter table CCM_CORE.SITE_AWARE_APPLICATIONS 
       add constraint FKopo91c29jaunpcusjwlphhxkd 
       foreign key (SITE_ID) 
       references CCM_CORE.SITES;

    alter table CCM_CORE.SITE_AWARE_APPLICATIONS 
       add constraint FKslbu2qagg23dmdu01lun7oh7x 
       foreign key (OBJECT_ID) 
       references CCM_CORE.APPLICATIONS;

    alter table CCM_CORE.SITES 
       add constraint FKrca95c6p023men53b8ayu26kp 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.STYLE_MEDIA_RULES 
       add constraint FKdq24a4atxp4c1sbqs8g6lpkx0 
       foreign key (MEDIA_QUERY_ID) 
       references CCM_CORE.STYLE_MEDIA_QUERIES;

    alter table CCM_CORE.STYLE_MEDIA_RULES 
       add constraint FKf67h8q9kkjft9go2xo2572n17 
       foreign key (STYLE_ID) 
       references CCM_CORE.STYLES;

    alter table CCM_CORE.STYLE_PROPERTIES 
       add constraint FKg2g0n7jmce3vjmula0898yp94 
       foreign key (RULE_ID) 
       references CCM_CORE.STYLE_RULES;

    alter table CCM_CORE.STYLE_RULES 
       add constraint FKf6fb4k6y2d74p70ldmj8awqj3 
       foreign key (STYLE_ID) 
       references CCM_CORE.STYLE_MEDIA_RULES;

    alter table CCM_CORE.THEME_DATA_FILES 
       add constraint FK630m2y2p7pp487ofowbefrm89 
       foreign key (FILE_ID) 
       references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.theme_directories 
       add constraint FKrmgyslvw22j87n4cxau5jvsou 
       foreign key (FILE_ID) 
       references CCM_CORE.THEME_FILES;

    alter table CCM_CORE.THEME_FILES 
       add constraint FKfsycb4bt8d0wye7r3n06ekfeu 
       foreign key (PARENT_DIRECTORY_ID) 
       references CCM_CORE.theme_directories;

    alter table CCM_CORE.THEME_FILES 
       add constraint FKke2jj04kjqh91h347g1ut0yff 
       foreign key (THEME_ID) 
       references CCM_CORE.THEMES;

    alter table CCM_CORE.THEMES 
       add constraint FKlat55c5l3fxbykkibrmv7qi4x 
       foreign key (ROOT_DIRECTORY_ID) 
       references CCM_CORE.theme_directories;

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

    alter table CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS 
       add constraint FK1pnsq9ur3ylq0ghuj23p4cogs 
       foreign key (LOCKING_USER_ID) 
       references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS 
       add constraint FK9ngp088m8xa82swy7yg3qx6vh 
       foreign key (NOTIFICATION_SENDER) 
       references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS 
       add constraint FKt9ha3no3bj8a50pnw8cnqh2cq 
       foreign key (TASK_ID) 
       references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_DESCRIPTIONS 
       add constraint FKgx7upkqky82dpxvbs95imfl9l 
       foreign key (WORKFLOW_ID) 
       references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_NAMES 
       add constraint FKkxedy9p48avfk45r0bn4uc09i 
       foreign key (WORKFLOW_ID) 
       references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOW_TASK_ASSIGNMENTS 
       add constraint FKpq4paqtfbi5erhh98wl1ja005 
       foreign key (ROLE_ID) 
       references CCM_CORE.CCM_ROLES;

    alter table CCM_CORE.WORKFLOW_TASK_ASSIGNMENTS 
       add constraint FK3933ol31co3yn5ee75b2hmhgp 
       foreign key (TASK_ID) 
       references CCM_CORE.WORKFLOW_ASSIGNABLE_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
       add constraint FKd2ymdg8nay9pmh2nn2whba0j8 
       foreign key (AUTHOR_ID) 
       references CCM_CORE.USERS;

    alter table CCM_CORE.WORKFLOW_TASK_COMMENTS 
       add constraint FKkfqrf9jdvm7livu5if06w0r5t 
       foreign key (TASK_ID) 
       references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
       add constraint FKy88tppv7ihx0lsn6g64f5lfq 
       foreign key (BLOCKED_TASK_ID) 
       references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_DEPENDENCIES 
       add constraint FKrj80uilojn73u9a4xgk3vt0cj 
       foreign key (BLOCKING_TASK_ID) 
       references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_DESCRIPTIONS 
       add constraint FKeb7mqbdx3bk7t01vo7kp2hpf 
       foreign key (TASK_ID) 
       references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASK_LABELS 
       add constraint FKf715qud6g9xv2xeb8rrpnv4xs 
       foreign key (TASK_ID) 
       references CCM_CORE.WORKFLOW_TASKS;

    alter table CCM_CORE.WORKFLOW_TASKS 
       add constraint FK1693cbc36e4d8gucg8q7sc57e 
       foreign key (WORKFLOW_ID) 
       references CCM_CORE.WORKFLOWS;

    alter table CCM_CORE.WORKFLOWS 
       add constraint FKrm2yfrs6veoxoy304upq2wc64 
       foreign key (OBJECT_ID) 
       references CCM_CORE.CCM_OBJECTS;

    alter table CCM_CORE.WORKFLOWS 
       add constraint FK9ray5beiny6wm2mi0uwyecay2 
       foreign key (TEMPLATE_ID) 
       references CCM_CORE.WORKFLOWS;