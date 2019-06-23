DELETE FROM ccm_cms.attachments_aud;

DELETE FROM ccm_cms.attachments;

DELETE FROM ccm_cms.attachment_lists_aud;

DELETE FROM ccm_cms.attachment_lists;

DELETE FROM ccm_cms.images_aud;

DELETE FROM ccm_cms.images;

DELETE FROM ccm_cms.files_aud;

DELETE FROM ccm_cms.files;

DELETE FROM ccm_cms.binary_assets_aud;

DELETE FROM ccm_cms.binary_assets;

DELETE FROM ccm_cms.contact_entries;

DELETE FROM ccm_cms.organizations;

DELETE FROM ccm_cms.persons;

DELETE FROM ccm_cms.contactable_entities;

DELETE FROM ccm_cms.postal_addresses;

DELETE FROM ccm_cms.asset_titles_aud;

DELETE FROM ccm_cms.asset_titles;

DELETE FROM ccm_cms.assets_aud;

DELETE FROM ccm_cms.assets;

DELETE FROM ccm_cms.news_texts;

DELETE FROM ccm_cms.news;

DELETE FROM ccm_cms.article_texts;

DELETE FROM ccm_cms.article_texts_aud;

DELETE FROM ccm_cms.articles;

DELETE FROM ccm_cms.articles_aud;

DELETE FROM ccm_cms.content_item_descriptions;

DELETE FROM ccm_cms.content_item_descriptions_aud;

DELETE FROM ccm_cms.content_item_names;

DELETE FROM ccm_cms.content_item_names_aud;

DELETE FROM ccm_cms.content_item_titles;

DELETE FROM ccm_cms.content_item_titles_aud;

DELETE FROM ccm_cms.content_items;

DELETE FROM ccm_cms.content_items_aud;

DELETE FROM ccm_cms.content_section_lifecycle_definitions;

DELETE FROM ccm_cms.content_type_labels;

DELETE FROM ccm_cms.content_type_descriptions;

DELETE FROM ccm_cms.content_types;

DELETE FROM ccm_cms.workflow_tasks;

DELETE FROM ccm_core.workflow_descriptions;

DELETE FROM ccm_core.workflow_names;

DELETE FROM ccm_core.workflow_task_dependencies;

DELETE FROM ccm_core.workflow_task_labels;

DELETE FROM ccm_core.workflow_task_assignments;

DELETE FROM ccm_core.workflow_assignable_tasks;

DELETE FROM ccm_core.workflow_tasks;

DELETE FROM ccm_core.workflow_task_descriptions;

DELETE FROM ccm_cms.content_section_workflow_templates;

DELETE FROM ccm_core.workflows;

DELETE FROM ccm_cms.lifecycle_phase_definition_labels;

DELETE FROM ccm_cms.lifecycle_phase_definition_descriptions;

DELETE FROM ccm_cms.lifecycle_definition_labels;

DELETE FROM ccm_cms.lifecycle_definition_descriptions;

DELETE FROM ccm_cms.lifecyle_phases;

DELETE FROM ccm_cms.lifecycles;

DELETE FROM ccm_cms.lifecycle_phase_definitions;

DELETE FROM ccm_cms.lifecyle_definitions;

DELETE FROM ccm_cms.folder_content_section_map;

DELETE FROM ccm_cms.content_section_roles;

DELETE FROM ccm_cms.content_sections;

DELETE FROM ccm_cms.folders;

DELETE FROM ccm_core.settings_string_list;

DELETE FROM ccm_core.settings_l10n_str_values;

DELETE FROM ccm_core.settings_enum_values;

DELETE FROM ccm_core.settings_enum_values;

DELETE FROM ccm_core.settings;

DELETE FROM ccm_core.categorizations;

DELETE FROM ccm_core.category_domains;

DELETE FROM ccm_core.category_titles;

DELETE FROM ccm_core.categories;

DELETE FROM ccm_core.permissions;

DELETE FROM ccm_core.applications;

DELETE FROM ccm_core.resource_titles;

DELETE FROM ccm_core.resources;

DELETE FROM ccm_core.ccm_objects;

DELETE FROM ccm_core.role_memberships;

DELETE FROM ccm_core.group_memberships;

DELETE FROM ccm_core.groups;

DELETE FROM ccm_core.one_time_auth_tokens;

DELETE FROM ccm_core.users;

DELETE FROM ccm_core.user_email_addresses;

DELETE FROM ccm_core.parties;

DELETE FROM ccm_core.ccm_roles;

DELETE FROM ccm_core.ccm_objects_aud;

DELETE FROM ccm_core.ccm_revisions;

ALTER SEQUENCE hibernate_sequence RESTART WITH 1;

