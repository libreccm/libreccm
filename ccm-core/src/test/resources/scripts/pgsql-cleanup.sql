DELETE FROM ccm_core.settings_big_decimal;

DELETE FROM ccm_core.settings_boolean;

DELETE FROM ccm_core.settings_double;

DELETE FROM ccm_core.settings_enum_values;

DELETE FROM ccm_core.settings_enum;

DELETE FROM ccm_core.settings_l10n_string;

DELETE FROM ccm_core.settings_l10n_str_values;

DELETE FROM ccm_core.settings_long;

DELETE FROM ccm_core.settings_string;

DELETE FROM ccm_core.settings;

DELETE FROM ccm_core.categorizations;

DELETE FROM ccm_core.category_domains;

DELETE FROM ccm_core.categories;

DELETE FROM ccm_core.permissions;

DELETE FROM ccm_core.ccm_objects;

DELETE FROM ccm_core.role_memberships;

DELETE FROM ccm_core.group_memberships;

DELETE FROM ccm_core.groups;

DELETE FROM ccm_core.users;

DELETE FROM ccm_core.user_email_addresses;

DELETE FROM ccm_core.parties;

DELETE FROM ccm_core.ccm_roles;

ALTER SEQUENCE hibernate_sequence RESTART;