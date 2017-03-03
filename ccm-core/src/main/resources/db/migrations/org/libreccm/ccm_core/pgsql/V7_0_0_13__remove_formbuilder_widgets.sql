ALTER TABLE ccm_core.formbuilder_widgets 
DROP CONSTRAINT FK_nei20rvwsnawx4u0ywrh22df1;

DROP TABLE ccm_core.formbuilder_widget_labels;

ALTER TABLE ccm_core.formbuilder_widgets DROP COLUMN label_object_id;
