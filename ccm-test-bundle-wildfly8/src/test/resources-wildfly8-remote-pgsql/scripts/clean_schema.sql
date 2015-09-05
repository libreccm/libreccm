-- Used by the org.libreccm.core.modules.CcmModulesTest to clean up the
-- schema after the test

DROP SCHEMA IF EXISTS ccm_core CASCADE;

DROP SEQUENCE IF EXISTS hibernate_sequence;

DROP TABLE IF EXISTS schema_version;