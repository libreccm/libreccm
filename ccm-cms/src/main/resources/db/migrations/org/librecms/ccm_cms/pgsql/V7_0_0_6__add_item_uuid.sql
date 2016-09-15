ALTER TABLE ccm_cms.content_items
    ADD COLUMN item_uuid varchar(255) not null;

ALTER TABLE ccm_cms.content_items_aud
    ADD COLUMN item_uuid varchar(255) not null;