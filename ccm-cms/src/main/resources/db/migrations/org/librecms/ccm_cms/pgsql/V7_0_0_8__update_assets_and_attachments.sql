alter table CCM_CMS.ASSETS
    drop constraint UK_9l2v1u9beyemgjwqx7isbumwh;

alter table CCM_CMS.ASSETS_AUD 
    drop constraint FK4m4op9s9h5qhndcsssbu55gr2;

alter table CCM_CMS.AttachmentList_ItemAttachment_AUD 
    drop constraint FKduowjilu7dqfs2oja88tr5oim;

alter table CCM_CMS.AttachmentList_ItemAttachment_AUD 
    drop constraint FKdtm7qp3n6cojbm9b916plsgtx;

alter table CCM_CMS.ASSETS_AUD 
    drop constraint FK586j3p95nw2oa6yd06xdw0o5u;

alter table CCM_CMS.ATTACHMENTS 
    drop constraint FK622uanry14vw27de3d2v9uy57;

alter table CCM_CMS.ASSETS
    rename column ASSET_ID to OBJECT_ID; 

alter table CCM_CMS.ASSETS 
    drop column if exists UUID;

alter table CCM_CMS.ASSETS_AUD
    rename column ASSET_ID to object_id;

alter table CCM_CMS.ASSETS_AUD
    drop column if exists revtype;

alter table CCM_CMS.ASSETS_AUD
    drop column if exists revend;

alter table CCM_CMS.ASSETS_AUD
    drop column if exists uuid;

alter table CCM_CMS.BOOKMARKS 
    drop constraint FKhs1tohpjry5sqdpl3cijl5ppk;

alter table CCM_CMS.BOOKMARKS_AUD 
    drop constraint FKjrk2tah1gyn67acth3g7olvuc;

alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS 
    drop constraint FK5sxviewjxfgk0at60emrpuh3n;

alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD 
    drop constraint FK6814o1fnh49p5ij9cfvr7y00s;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSET 
    drop constraint FKc6m77lkbfa5ym2s8sq00jkyjf;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSET_AUD 
    drop constraint FKd5efitnmsrko2vq48ei1mclfv;

alter table CCM_CMS.FILES 
    drop constraint FK4e8p3tu8ocy43ofs9uifuk8gh;

alter table CCM_CMS.FILES_AUD 
    drop constraint FK3c9xf8w1dr3q0grxslguvviqn;

alter table CCM_CMS.IMAGES 
    drop constraint FK9mgknvtu1crw4el5d4sqy8d6c;

alter table CCM_CMS.IMAGES_AUD 
    drop constraint FK6xggeoexci2har3mceo9naqiy;

alter table CCM_CMS.LEGAL_METADATA 
    drop constraint FKjxss2fb6khhn68e8ccuksl9hk;

alter table CCM_CMS.LEGAL_METADATA_AUD 
    drop constraint FKofjkwpepeyb6e8tytnhjfvx49;

alter table CCM_CMS.NOTE_TEXTS 
    drop constraint FKa0yp21m25o7omtnag0eet8v8q;

alter table CCM_CMS.RELATED_LINKS 
    drop constraint FKf4r30ra4a2ajuky0tk4lc06n5;

alter table CCM_CMS.RELATED_LINKS_AUD 
    drop constraint FKkda2cf5ynu7v7udi0ytfmr9ij;

alter table CCM_CMS.REUSABLE_ASSETS 
    drop constraint FKngdq6f077q6ndqn9o3jc6k14a;

alter table CCM_CMS.REUSABLE_ASSETS 
    drop constraint FKhvf4mfltp8abbr5u0qgjm2jk2;

alter table CCM_CMS.REUSABLE_ASSETS_AUD 
    drop constraint FKgyc5gd3cffox4wvjoir6i4gxt;

alter table CCM_CMS.VIDEO_ASSET 
    drop constraint FKdjjbp8p48xwfqhw0oo79tkyjy;

alter table CCM_CMS.VIDEO_ASSET 
    drop constraint FK9cynf36vykykyaga2j1xs7jkx;

alter table CCM_CMS.VIDEO_ASSET_AUD 
    drop constraint FK7qsbfxxg6ixpkjjor4nbkd63i;

create table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS (
    LIST_ID int8 not null,
     LOCALIZED_VALUE text,
     LOCALE varchar(255) not null,
     primary key (LIST_ID, LOCALE)
);

create table CCM_CMS.ATTACHMENT_LIST_DESCRIPTIONS_AUD (
    REV integer not null,
    LIST_ID int8 not null,
    LOCALIZED_VALUE text not null,
    LOCALE varchar(255) not null,
    REVTYPE int2,
    REVEND integer,
    primary key (REV, LIST_ID, LOCALIZED_VALUE, LOCALE)
);

alter table CCM_CMS.ATTACHMENT_LISTS 
    drop column asset_type;

alter table CCM_CMS.ATTACHMENT_LISTS
    add column NAME varchar(255);

alter table CCM_CMS.ATTACHMENT_LISTS
    add column ITEM_ID int8;

alter table CCM_CMS.ATTACHMENT_LISTS
    add column LIST_ORDER int8;

alter table CCM_CMS.ATTACHMENT_LISTS_AUD 
    drop column ASSET_TYPE;

alter table CCM_CMS.ATTACHMENT_LISTS_AUD 
    add column NAME varchar(255);

alter table CCM_CMS.ATTACHMENT_LISTS_AUD
    add column ITEM_ID int8;

alter table CCM_CMS.ATTACHMENT_LISTS_AUD
    add column LIST_ORDER int8;

alter table CCM_CMS.AUDIO_ASSETS 
    drop constraint FKa1m18ejmeknjiibvh2dac6tas;

alter table CCM_CMS.AUDIO_ASSETS_AUD 
    drop constraint FKaf381a7d420ru9114rqcpr2b4;

alter table CCM_CMS.BINARY_ASSETS
    drop constraint FK65sp6cl7d8qjmqgku1bjtpsdy;

alter table CCM_CMS.BINARY_ASSETS_AUD 
    drop constraint FKovfkjrq3eka9fsfe5sidw07p3;

drop table if exists CCM_CMS.AttachmentList_ItemAttachment_AUD;

alter table CCM_CMS.AUDIO_ASSETS 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.AUDIO_ASSETS_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.BINARY_ASSETS 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.BINARY_ASSETS_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.BOOKMARKS 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.BOOKMARKS_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSET
    rename to EXTERNAL_VIDEO_ASSETS;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSET_AUD
    rename to EXTERNAL_VIDEO_ASSETS_AUD;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.FILES 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.FILES_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.IMAGES 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.IMAGES_AUD
   rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.LEGAL_METADATA rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.LEGAL_METADATA_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.NOTES 
    rename to SIDE_NOTES;

alter table CCM_CMS.NOTES_AUD 
    rename to SIDE_NOTES_AUD;

alter table CCM_CMS.SIDE_NOTES
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.SIDE_NOTES_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.RELATED_LINKS 
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.RELATED_LINKS_AUD rename column ASSET_ID to OBJECT_ID;

drop table if exists CCM_CMS.REUSABLE_ASSETS;

drop table if exists CCM_CMS.REUSABLE_ASSETS_AUD;

alter table CCM_CMS.VIDEO_ASSET
    rename to VIDEO_ASSETS;

alter table CCM_CMS.VIDEO_ASSET_AUD
    rename to VIDEO_ASSETS_AUD;

alter table CCM_CMS.VIDEO_ASSETS 
     rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.VIDEO_ASSETS_AUD
    rename column ASSET_ID to OBJECT_ID;

alter table CCM_CMS.NOTE_TEXTS 
    rename to SIDE_NOTE_TEXTS;

alter table CCM_CMS.SIDE_NOTE_TEXTS
    rename column ASSET_ID to SIDE_NOTE_ID;

create table CCM_CMS.SIDE_NOTE_TITLES (
    SIDE_NOTE_ID int8 not null,
    LOCALIZED_VALUE text,
    LOCALE varchar(255) not null,
    primary key (SIDE_NOTE_ID, LOCALE)
);

create table CCM_CMS.SIDE_NOTE_TITLES_AUD (
    REV int4 not null,
    SIDE_NOTE_ID int8 not null,
    LOCALIZED_VALUE text not null,
    LOCALE varchar(255) not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, SIDE_NOTE_ID, LOCALIZED_VALUE, LOCALE)
);

alter table CCM_CMS.ATTACHMENTS
    add column ATTACHMENT_LIST_ID int8;

alter table CCM_CMS.ASSETS 
    add constraint FKlbiojib44ujxv9eee1sjn67qk 
    foreign key (OBJECT_ID) 
    references CCM_CORE.CCM_OBJECTS;

alter table CCM_CMS.ASSETS_AUD 
    add constraint FKi5q560xg9357da8gc5sukqbw8 
    foreign key (OBJECT_ID, REV) 
    references CCM_CORE.CCM_OBJECTS_AUD;

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

alter table CCM_CMS.ATTACHMENTS 
    add constraint FK3mqbt13sbed2ae0esrps4p0oh 
    foreign key (ATTACHMENT_LIST_ID) 
    references CCM_CMS.ATTACHMENT_LISTS;

alter table CCM_CMS.AUDIO_ASSETS 
    add constraint FKgxpsfjlfsk609c0w2te18y90v 
    foreign key (OBJECT_ID) 
    references CCM_CMS.BINARY_ASSETS;

alter table CCM_CMS.AUDIO_ASSETS_AUD 
    add constraint FKbt11nwbde1en1upceratct6s3 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.BINARY_ASSETS_AUD;

alter table CCM_CMS.BINARY_ASSETS 
    add constraint FKltx0jq1u1aflrd20k1c77m8vh 
    foreign key (OBJECT_ID) 
    references CCM_CMS.ASSETS;

alter table CCM_CMS.BINARY_ASSETS_AUD 
    add constraint FK1qfap4mxprjk7gnjdcvdxr5mv 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.ASSETS_AUD;

alter table CCM_CMS.BOOKMARKS 
    add constraint FKksnngecvvxmsxdvri4shby2hy 
    foreign key (OBJECT_ID) 
    references CCM_CMS.ASSETS;

alter table CCM_CMS.BOOKMARKS_AUD 
    add constraint FK47cpxaw9vnnes2dbr6h3toirl 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.ASSETS_AUD;

alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS 
    add constraint FK36xjlvslk0vlekn9lsc7x1c7a 
    foreign key (OBJECT_ID) 
    references CCM_CMS.BOOKMARKS;

alter table CCM_CMS.EXTERNAL_AUDIO_ASSETS_AUD 
    add constraint FKp3jndaw4k35wb3d6hg5ng4xww 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.BOOKMARKS_AUD;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS 
    add constraint FK74al02r60wmjgpy009b2291l7 
    foreign key (OBJECT_ID) 
    references CCM_CMS.BOOKMARKS;

alter table CCM_CMS.EXTERNAL_VIDEO_ASSETS_AUD 
    add constraint FKg7q8dy5xbemdw7whdn68xv297 
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

alter table CCM_CMS.IMAGES 
    add constraint FKmdqranhdstkn6m6d73l15amxs 
    foreign key (OBJECT_ID) 
    references CCM_CMS.BINARY_ASSETS;

alter table CCM_CMS.IMAGES_AUD 
    add constraint FK4jsrdpe6d8is0ybx2p7sxivwf 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.BINARY_ASSETS_AUD;

alter table CCM_CMS.LEGAL_METADATA 
    add constraint FKnxl7uyv1ks0qabgeienx2t9d1 
    foreign key (OBJECT_ID) 
    references CCM_CMS.ASSETS;

alter table CCM_CMS.LEGAL_METADATA_AUD 
    add constraint FKpt3eqil7iij6t5h1lrnjbb5xs 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.ASSETS_AUD;

alter table CCM_CMS.SIDE_NOTE_TEXTS 
    add constraint FK79g6eg2csjaixrjr2xgael8lm 
    foreign key (SIDE_NOTE_ID) 
    references CCM_CMS.SIDE_NOTES;

alter table CCM_CMS.RELATED_LINKS 
    add constraint FK35tv60a9kflo17h6xduvwvgis 
    foreign key (OBJECT_ID) 
    references CCM_CMS.ASSETS;

alter table CCM_CMS.RELATED_LINKS_AUD 
    add constraint FKiuwk6mcj3h5gccu2aviq3d8lt 
    foreign key (OBJECT_ID, REV) 
    references CCM_CMS.ASSETS_AUD;

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

alter table CCM_CMS.SIDE_NOTE_TITLES 
    add constraint FKf8c9mw6p4ijiba77t32uh7i0o 
    foreign key (SIDE_NOTE_ID) 
    references CCM_CMS.SIDE_NOTES;

alter table CCM_CMS.SIDE_NOTE_TITLES_AUD 
    add constraint FKkuw32q22sotku83khh1xda7sf 
    foreign key (REV) 
    references CCM_CORE.CCM_REVISIONS;

alter table CCM_CMS.SIDE_NOTE_TITLES_AUD 
    add constraint FKbqgawobyevpbgxsnbbs9vwooq 
    foreign key (REVEND) 
    references CCM_CORE.CCM_REVISIONS;
