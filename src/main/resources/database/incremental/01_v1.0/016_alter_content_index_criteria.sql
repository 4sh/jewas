ALTER TABLE CONTENT_INDEX_CRITERIA ADD CONSTRAINT cic_content_fk
FOREIGN KEY (CONTENT_REF)
REFERENCES CONTENT (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CONTENT_INDEX_CRITERIA ADD CONSTRAINT cic_index_criteria_fk
FOREIGN KEY (INDEX_CRITERIA_REF)
REFERENCES INDEX_CRITERIA (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;