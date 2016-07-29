

    create table CCM_CMS.ARTICLE_LEADS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.ARTICLE_TEXTS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.ARTICLE_TEXTS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ARTICLES (
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.ARTICLES_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.ASSET_TITLES (
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.ASSET_TITLES_AUD (
        REV int4 not null,
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ASSETS (
        ASSET_ID int8 not null,
        UUID varchar(255),
        primary key (ASSET_ID)
    );

    create table CCM_CMS.ASSETS_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        REVTYPE int2,
        REVEND int4,
        UUID varchar(255),
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.ATTACHMENT_LIST_CAPTIONS (
        LIST_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (LIST_ID, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LIST_CAPTIONS_AUD (
        REV int4 not null,
        LIST_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, LIST_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.ATTACHMENT_LISTS (
        LIST_ID int8 not null,
        ASSET_TYPE varchar(1024),
        UUID varchar(255),
        CONTENT_ITEM_ID int8,
        primary key (LIST_ID)
    );

    create table CCM_CMS.ATTACHMENT_LISTS_AUD (
        LIST_ID int8 not null,
        REV int4 not null,
        REVTYPE int2,
        REVEND int4,
        ASSET_TYPE varchar(1024),
        UUID varchar(255),
        primary key (LIST_ID, REV)
    );

    create table CCM_CMS.AttachmentList_ItemAttachment_AUD (
        REV int4 not null,
        LIST_ID int8 not null,
        ATTACHMENT_ID int8 not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, LIST_ID, ATTACHMENT_ID)
    );

    create table CCM_CMS.ATTACHMENTS (
        ATTACHMENT_ID int8 not null,
        SORT_KEY int8,
        uuid varchar(255),
        ASSET_ID int8,
        LIST_ID int8,
        primary key (ATTACHMENT_ID)
    );

    create table CCM_CMS.ATTACHMENTS_AUD (
        ATTACHMENT_ID int8 not null,
        REV int4 not null,
        REVTYPE int2,
        REVEND int4,
        SORT_KEY int8,
        uuid varchar(255),
        ASSET_ID int8,
        primary key (ATTACHMENT_ID, REV)
    );

    create table CCM_CMS.AUDIO_ASSETS (
        ASSET_ID int8 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.AUDIO_ASSETS_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.BINARY_ASSET_DESCRIPTIONS (
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.BINARY_ASSET_DESCRIPTIONS_AUD (
        REV int4 not null,
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.BINARY_ASSETS (
        ASSET_DATA oid,
        FILENAME varchar(512) not null,
        MIME_TYPE varchar(512) not null,
        DATA_SIZE int8,
        ASSET_ID int8 not null,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.BINARY_ASSETS_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        ASSET_DATA oid,
        FILENAME varchar(512),
        MIME_TYPE varchar(512),
        DATA_SIZE int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.BOOKMARK_DESCRIPTIONS (
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.BOOKMARK_DESCRIPTIONS_AUD (
        REV int4 not null,
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.BOOKMARKS (
        URL varchar(2048) not null,
        ASSET_ID int8 not null,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.BOOKMARKS_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        URL varchar(2048),
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_DESCRIPTIONS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_NAMES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_NAMES_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_TITLES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEM_TITLES_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.CONTENT_ITEMS (
        ANCESTORS varchar(1024),
        launchDate date,
        version varchar(255),
        OBJECT_ID int8 not null,
        contentType_OBJECT_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTENT_ITEMS_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        ANCESTORS varchar(1024),
        launchDate date,
        version varchar(255),
        contentType_OBJECT_ID int8,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.CONTENT_SECTION_ROLES (
        SECTION_ID int8 not null,
        ROLE_ID int8 not null
    );

    create table CCM_CMS.CONTENT_SECTIONS (
        DEFAULT_LOCALE varchar(255),
        ITEM_RESOLVER_CLASS varchar(1024),
        LABEL varchar(512),
        PAGE_RESOLVER_CLASS varchar(1024),
        TEMPLATE_RESOLVER_CLASS varchar(1024),
        XML_GENERATOR_CLASS varchar(1024),
        OBJECT_ID int8 not null,
        ROOT_ASSETS_FOLDER_ID int8,
        ROOT_DOCUMENTS_FOLDER_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.CONTENT_TYPE_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_TYPE_LABELS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.CONTENT_TYPES (
        ANCESTORS varchar(1024),
        CONTENT_ITEM_CLASS varchar(1024),
        DESCENDANTS varchar(1024),
        mode varchar(255),
        OBJECT_ID int8 not null,
        CONTENT_SECTION_ID int8,
        DEFAULT_LIFECYCLE_ID int8,
        DEFAULT_WORKFLOW int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.ContentItem_AttachmentList_AUD (
        REV int4 not null,
        CONTENT_ITEM_ID int8 not null,
        LIST_ID int8 not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, CONTENT_ITEM_ID, LIST_ID)
    );

    create table CCM_CMS.EVENT_COSTS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_COSTS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_DATES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_DATES_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_LOCATIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_LOCATIONS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_MAIN_CONTRIBUTORS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_MAIN_CONTRIBUTORS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_TEXTS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_TEXTS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENT_TYPES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.EVENT_TYPES_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.EVENTS (
        END_DATE date,
        MAP_LINK varchar(255),
        START_DATE date not null,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.EVENTS_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        END_DATE date,
        MAP_LINK varchar(255),
        START_DATE date,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.EXTERNAL_AUDIO_ASSETS (
        ASSET_ID int8 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.EXTERNAL_VIDEO_ASSET (
        ASSET_ID int8 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.EXTERNAL_VIDEO_ASSET_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.FILES (
        ASSET_ID int8 not null,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.FILES_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.IMAGES (
        HEIGHT int8,
        WIDTH int8,
        ASSET_ID int8 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.IMAGES_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        HEIGHT int8,
        WIDTH int8,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.LEGAL_METADATA (
        CREATOR varchar(255),
        PUBLISHER varchar(255),
        RIGHTS_HOLDER varchar(512),
        ASSET_ID int8 not null,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.LEGAL_METADATA_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        CREATOR varchar(255),
        PUBLISHER varchar(255),
        RIGHTS_HOLDER varchar(512),
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS (
        LEGAL_METADATA_ID int8 not null,
        CONTRIBUTORS varchar(255)
    );

    create table CCM_CMS.LEGAL_METADATA_CONTRIBUTORS_AUD (
        REV int4 not null,
        LEGAL_METADATA_ID int8 not null,
        CONTRIBUTORS varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, LEGAL_METADATA_ID, CONTRIBUTORS)
    );

    create table CCM_CMS.LEGAL_METADATA_RIGHTS (
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.LEGAL_METADATA_RIGHTS_AUD (
        REV int4 not null,
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_DEFINITION_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_DEFINITION_LABELS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_PHASE_DEFINITION_DESCRIPTIONS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_PHASE_DEFINITION_LABELS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.LIFECYCLE_PHASE_DEFINITIONS (
        PHASE_DEFINITION_ID int8 not null,
        DEFAULT_DELAY int8,
        DEFAULT_DURATION int8,
        DEFAULT_LISTENER varchar(1024),
        LIFECYCLE_DEFINITION_ID int8,
        primary key (PHASE_DEFINITION_ID)
    );

    create table CCM_CMS.LIFECYCLES (
        LIFECYCLE_ID int8 not null,
        END_DATE_TIME date,
        FINISHED boolean,
        LISTENER varchar(1024),
        START_DATE_TIME date,
        STARTED boolean,
        DEFINITION_ID int8,
        primary key (LIFECYCLE_ID)
    );

    create table CCM_CMS.LIFECYLE_DEFINITIONS (
        LIFECYCLE_DEFINITION_ID int8 not null,
        DEFAULT_LISTENER varchar(1024),
        primary key (LIFECYCLE_DEFINITION_ID)
    );

    create table CCM_CMS.LIFECYLE_PHASES (
        PHASE_ID int8 not null,
        END_DATE_TIME date,
        FINISHED boolean,
        LISTENER varchar(1024),
        START_DATE_TIME date,
        STARTED boolean,
        DEFINITION_ID int8,
        lifecycle_LIFECYCLE_ID int8,
        primary key (PHASE_ID)
    );

    create table CCM_CMS.MPA_SECTION_TEXTS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.MPA_SECTION_TEXTS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.MPA_SECTION_TITLES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.MPA_SECTION_TITLES_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.MPA_SUMMARIES (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.MPA_SUMMARIES_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.MULTIPART_ARTICLE_SECTIONS (
        SECTION_ID int8 not null,
        PAGE_BREAK boolean,
        RANK int4,
        MULTIPART_ARTICLE_ID int8,
        primary key (SECTION_ID)
    );

    create table CCM_CMS.MULTIPART_ARTICLE_SECTIONS_AUD (
        SECTION_ID int8 not null,
        REV int4 not null,
        REVTYPE int2,
        REVEND int4,
        PAGE_BREAK boolean,
        RANK int4,
        primary key (SECTION_ID, REV)
    );

    create table CCM_CMS.MULTIPART_ARTICLES (
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.MULTIPART_ARTICLES_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.MultiPartArticle_MultiPartArticleSection_AUD (
        REV int4 not null,
        MULTIPART_ARTICLE_ID int8 not null,
        SECTION_ID int8 not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, MULTIPART_ARTICLE_ID, SECTION_ID)
    );

    create table CCM_CMS.NEWS (
        HOMEPAGE boolean,
        NEWS_DATE date not null,
        OBJECT_ID int8 not null,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.NEWS_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        HOMEPAGE boolean,
        NEWS_DATE date,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.NEWS_TEXTS (
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (OBJECT_ID, LOCALE)
    );

    create table CCM_CMS.NEWS_TEXTS_AUD (
        REV int4 not null,
        OBJECT_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, OBJECT_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.NOTE_TEXTS (
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text,
        LOCALE varchar(255) not null,
        primary key (ASSET_ID, LOCALE)
    );

    create table CCM_CMS.NOTE_TEXTS_AUD (
        REV int4 not null,
        ASSET_ID int8 not null,
        LOCALIZED_VALUE text not null,
        LOCALE varchar(255) not null,
        REVTYPE int2,
        REVEND int4,
        primary key (REV, ASSET_ID, LOCALIZED_VALUE, LOCALE)
    );

    create table CCM_CMS.NOTES (
        ASSET_ID int8 not null,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.NOTES_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.RELATED_LINKS (
        ASSET_ID int8 not null,
        BOOKMARK_ID int8,
        TARGET_ITEM int8,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.RELATED_LINKS_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        BOOKMARK_ID int8,
        TARGET_ITEM int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.REUSABLE_ASSETS (
        OBJECT_ID int8 not null,
        ASSET_ID int8,
        primary key (OBJECT_ID)
    );

    create table CCM_CMS.REUSABLE_ASSETS_AUD (
        OBJECT_ID int8 not null,
        REV int4 not null,
        ASSET_ID int8,
        primary key (OBJECT_ID, REV)
    );

    create table CCM_CMS.TASK_EVENT_URL_GENERATOR (
        GENERATOR_ID int8 not null,
        EVENT varchar(256),
        URL_GENERATOR_CLASS varchar(1024),
        CONTENT_TYPE_ID int8,
        TASK_TYPE_ID int8,
        primary key (GENERATOR_ID)
    );

    create table CCM_CMS.VIDEO_ASSET (
        HEIGHT int8,
        WIDTH int8,
        ASSET_ID int8 not null,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID)
    );

    create table CCM_CMS.VIDEO_ASSET_AUD (
        ASSET_ID int8 not null,
        REV int4 not null,
        HEIGHT int8,
        WIDTH int8,
        LEGAL_METADATA_ID int8,
        primary key (ASSET_ID, REV)
    );

    create table CCM_CMS.WORKFLOW_TASK_TYPES (
        TASK_TYPE_ID int8 not null,
        DEFAULT_URL_GENERATOR_CLASS varchar(1024),
        PRIVILEGE varchar(256),
        primary key (TASK_TYPE_ID)
    );

    create table CCM_CMS.WORKFLOW_TASKS (
        TASK_ID int8 not null,
        TASK_TYPE_ID int8,
        primary key (TASK_ID)
    );

    alter table CCM_CMS.ASSETS 
        add constraint UK_9l2v1u9beyemgjwqx7isbumwh unique (UUID);

    alter table CCM_CMS.ARTICLE_LEADS 
        add constraint FK4g66u3qtfyepw0f733kuiiaul 
        foreign key (OBJECT_ID) 
        references CCM_CMS.WORKFLOW_TASK_TYPES;

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

    alter table CCM_CMS.ASSETS_AUD 
        add constraint FK4m4op9s9h5qhndcsssbu55gr2 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ASSETS_AUD 
        add constraint FK586j3p95nw2oa6yd06xdw0o5u 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

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

    alter table CCM_CMS.ATTACHMENT_LISTS 
        add constraint FK4c7jp8622b8m8nvdvdajnt0am 
        foreign key (CONTENT_ITEM_ID) 
        references CCM_CMS.CONTENT_ITEMS;

    alter table CCM_CMS.ATTACHMENT_LISTS_AUD 
        add constraint FKgdt5p8huh1lhk299hkrytqmqc 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.ATTACHMENT_LISTS_AUD 
        add constraint FKdn502yobchapgcyj1bu00u67a 
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
        add constraint FK622uanry14vw27de3d2v9uy57 
        foreign key (LIST_ID) 
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
        add constraint FKa1m18ejmeknjiibvh2dac6tas 
        foreign key (ASSET_ID) 
        references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.AUDIO_ASSETS_AUD 
        add constraint FKaf381a7d420ru9114rqcpr2b4 
        foreign key (ASSET_ID, REV) 
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
        add constraint FK65sp6cl7d8qjmqgku1bjtpsdy 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.BINARY_ASSETS_AUD 
        add constraint FKovfkjrq3eka9fsfe5sidw07p3 
        foreign key (ASSET_ID, REV) 
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
        add constraint FKhs1tohpjry5sqdpl3cijl5ppk 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.BOOKMARKS_AUD 
        add constraint FKjrk2tah1gyn67acth3g7olvuc 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.ASSETS_AUD;

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

    alter table CCM_CMS.CONTENT_SECTION_ROLES 
        add constraint FKkn5nygbmub9wd5lxw3402t82d 
        foreign key (ROLE_ID) 
        references CCM_CORE.CCM_ROLES;

    alter table CCM_CMS.CONTENT_SECTION_ROLES 
        add constraint FKgcn76piocmkmvl3b0omv9vkv9 
        foreign key (SECTION_ID) 
        references CCM_CMS.CONTENT_SECTIONS;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FKajweudfxaf7g2ydr2hcgqwcib 
        foreign key (ROOT_ASSETS_FOLDER_ID) 
        references CCM_CORE.CATEGORIES;

    alter table CCM_CMS.CONTENT_SECTIONS 
        add constraint FK6g7kw4b6diqa0nks45ilp0vhs 
        foreign key (ROOT_DOCUMENTS_FOLDER_ID) 
        references CCM_CORE.CATEGORIES;

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
        add constraint FKoqvcvktnvt4ncx5k6daqat4u8 
        foreign key (DEFAULT_LIFECYCLE_ID) 
        references CCM_CMS.LIFECYCLES;

    alter table CCM_CMS.CONTENT_TYPES 
        add constraint FKpgeccqsr50xwb268ypmfx0r66 
        foreign key (DEFAULT_WORKFLOW) 
        references CCM_CORE.WORKFLOWS;

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
        add constraint FK5sxviewjxfgk0at60emrpuh3n 
        foreign key (ASSET_ID) 
        references CCM_CMS.BOOKMARKS;

    alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD 
        add constraint FK6814o1fnh49p5ij9cfvr7y00s 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.BOOKMARKS_AUD;

    alter table CCM_CMS.EXTERNAL_VIDEO_ASSET 
        add constraint FK3jia7ctpjs0u510hi0qqexu5t 
        foreign key (LEGAL_METADATA_ID) 
        references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.EXTERNAL_VIDEO_ASSET 
        add constraint FKc6m77lkbfa5ym2s8sq00jkyjf 
        foreign key (ASSET_ID) 
        references CCM_CMS.BOOKMARKS;

    alter table CCM_CMS.EXTERNAL_VIDEO_ASSET_AUD 
        add constraint FKd5efitnmsrko2vq48ei1mclfv 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.BOOKMARKS_AUD;

    alter table CCM_CMS.FILES 
        add constraint FK4e8p3tu8ocy43ofs9uifuk8gh 
        foreign key (ASSET_ID) 
        references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.FILES_AUD 
        add constraint FK3c9xf8w1dr3q0grxslguvviqn 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.IMAGES 
        add constraint FK51ja1101epvl74auenv6sqyev 
        foreign key (LEGAL_METADATA_ID) 
        references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.IMAGES 
        add constraint FK9mgknvtu1crw4el5d4sqy8d6c 
        foreign key (ASSET_ID) 
        references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.IMAGES_AUD 
        add constraint FK6xggeoexci2har3mceo9naqiy 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.LEGAL_METADATA 
        add constraint FKjxss2fb6khhn68e8ccuksl9hk 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.LEGAL_METADATA_AUD 
        add constraint FKofjkwpepeyb6e8tytnhjfvx49 
        foreign key (ASSET_ID, REV) 
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
        add constraint FKerihqw4gpb0lwap6x73us7wos 
        foreign key (lifecycle_LIFECYCLE_ID) 
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

    alter table CCM_CMS.NOTE_TEXTS 
        add constraint FKa0yp21m25o7omtnag0eet8v8q 
        foreign key (ASSET_ID) 
        references CCM_CMS.NOTES;

    alter table CCM_CMS.NOTE_TEXTS_AUD 
        add constraint FKoxggogvr9ek831d7y3omu8wvw 
        foreign key (REV) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.NOTE_TEXTS_AUD 
        add constraint FKg1kfbj306ufy8034a83a7ft2o 
        foreign key (REVEND) 
        references CCM_CORE.CCM_REVISIONS;

    alter table CCM_CMS.NOTES 
        add constraint FKb5msdqwvlj4ipd1r8f8uxuoy4 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.NOTES_AUD 
        add constraint FKjr3tek35d3f0xm4xp0s811cah 
        foreign key (ASSET_ID, REV) 
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
        add constraint FKf4r30ra4a2ajuky0tk4lc06n5 
        foreign key (ASSET_ID) 
        references CCM_CMS.ASSETS;

    alter table CCM_CMS.RELATED_LINKS_AUD 
        add constraint FKkda2cf5ynu7v7udi0ytfmr9ij 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.ASSETS_AUD;

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

    alter table CCM_CMS.TASK_EVENT_URL_GENERATOR 
        add constraint FKjjasedpc2ef91iknmiyqwhxrs 
        foreign key (CONTENT_TYPE_ID) 
        references CCM_CMS.CONTENT_TYPES;

    alter table CCM_CMS.TASK_EVENT_URL_GENERATOR 
        add constraint FKi3tnip5gr0i5hvw8skw21pveh 
        foreign key (TASK_TYPE_ID) 
        references CCM_CMS.WORKFLOW_TASK_TYPES;

    alter table CCM_CMS.VIDEO_ASSET 
        add constraint FKdjjbp8p48xwfqhw0oo79tkyjy 
        foreign key (LEGAL_METADATA_ID) 
        references CCM_CMS.LEGAL_METADATA;

    alter table CCM_CMS.VIDEO_ASSET 
        add constraint FK9cynf36vykykyaga2j1xs7jkx 
        foreign key (ASSET_ID) 
        references CCM_CMS.BINARY_ASSETS;

    alter table CCM_CMS.VIDEO_ASSET_AUD 
        add constraint FK7qsbfxxg6ixpkjjor4nbkd63i 
        foreign key (ASSET_ID, REV) 
        references CCM_CMS.BINARY_ASSETS_AUD;

    alter table CCM_CMS.WORKFLOW_TASKS 
        add constraint FK1sk7ouwhx9r3buxvbfvfa7nnm 
        foreign key (TASK_TYPE_ID) 
        references CCM_CMS.WORKFLOW_TASK_TYPES;

    alter table CCM_CMS.WORKFLOW_TASKS 
        add constraint FKge2x94m1y9tr7mk26ensyn674 
        foreign key (TASK_ID) 
        references CCM_CORE.WORKFLOW_USER_TASKS;
