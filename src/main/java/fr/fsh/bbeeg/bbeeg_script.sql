CREATE SEQUENCE INDEX_CRITERIA_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE DOMAIN_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE USER_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE CONTENT_SEQ INCREMENT BY 1 START WITH 1000;



CREATE TABLE I18N_TABLE (
                I18N_KEY CHAR(50) NOT NULL,
                LANGUAGE CHAR(2) NOT NULL,
                LABEL VARCHAR(200) NOT NULL,
                PRIMARY KEY (I18N_KEY, LANGUAGE)
);


CREATE TABLE INDEX_CRITERIA (
                ID INT(10) NOT NULL,
                I18N_KEY CHAR(50) NOT NULL,
                PARENT_REF INT(10) NOT NULL,
                PRIMARY KEY (ID)
);


CREATE TABLE DOMAIN (
                ID INT(10) NOT NULL,
                I18N_KEY CHAR(50) NOT NULL,
                PRIMARY KEY (ID)
);


CREATE TABLE USER (
                ID INT(10) NOT NULL,
                NAME CHAR(50) NOT NULL,
                SURNAME CHAR(50) NOT NULL,
                EMAIL CHAR(50) NOT NULL,
                PRIMARY KEY (ID)
);


CREATE TABLE CONTENT (
                ID INT(10) NOT NULL,
                TITLE CHAR(100),
                CREATION_DATE DATE NOT NULL,
                LAST_MODIFICATION_DATE DATE NOT NULL,
                STATUS INT(2) NOT NULL,
                DESCRIPTION VARCHAR(200),
				CONTENT_TYPE INT(2) NOT NULL,
                AUTHOR_REF INT(10) NOT NULL,
                FILE_URI CHAR(100),
                PRIMARY KEY (ID)
);

CREATE TABLE CONTENT_DOMAIN (
                CONTENT_REF INT(10) NOT NULL,
                DOMAIN_REF INT(10) NOT NULL,
                PRIMARY KEY (CONTENT_REF, DOMAIN_REF)
);

CREATE TABLE CONTENT_INDEX_CRITERIA (
                CONTENT_REF INT(10) NOT NULL,
                INDEX_CRITERIA_REF INT(10) NOT NULL,
                PRIMARY KEY (CONTENT_REF, INDEX_CRITERIA_REF)
);

ALTER TABLE INDEX_CRITERIA ADD CONSTRAINT index_criteria_index_criteria_fk
FOREIGN KEY (PARENT_REF)
REFERENCES INDEX_CRITERIA (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CONTENT ADD CONSTRAINT user_content_fk
FOREIGN KEY (AUTHOR_REF)
REFERENCES USER (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CONTENT_DOMAIN ADD CONSTRAINT cd_content_fk
FOREIGN KEY (CONTENT_REF)
REFERENCES CONTENT (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CONTENT_DOMAIN ADD CONSTRAINT cd_domain_fk
FOREIGN KEY (DOMAIN_REF)
REFERENCES DOMAIN (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

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


INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.domain1', 'fr', 'Domaine A');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.domain2', 'fr', 'Domaine B');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.domain3', 'fr', 'Domaine C');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.text', 'fr', 'Texte');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.image', 'fr', 'Image');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.video', 'fr', 'Video');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.audio', 'fr', 'Audio');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.document', 'fr', 'Document');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.eeg', 'fr', 'EEG');

INSERT INTO USER (ID, NAME, SURNAME, EMAIL) VALUES (USER_SEQ.nextval, 'Sponge', 'Bob', 'bob.sponge@sea.fr');

INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.domain1');
INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.domain2');
INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.domain3');

INSERT INTO CONTENT (ID, TITLE, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, DESCRIPTION, CONTENT_TYPE, AUTHOR_REF, FILE_URI) VALUES (CONTENT_SEQ.nextval, 'Mon premier contenu', CURRENT_DATE, CURRENT_DATE, 0, 'Ceci est mon premier contenu :)', 0, USER_SEQ.currval, '');
INSERT INTO CONTENT (ID, TITLE, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, DESCRIPTION, CONTENT_TYPE, AUTHOR_REF, FILE_URI) VALUES (CONTENT_SEQ.nextval, 'Mon deuxième contenu', CURRENT_DATE, CURRENT_DATE, 0, 'Ceci est mon deuxième contenu :)', 0, USER_SEQ.currval, '');
INSERT INTO CONTENT (ID, TITLE, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, DESCRIPTION, CONTENT_TYPE, AUTHOR_REF, FILE_URI) VALUES (CONTENT_SEQ.nextval, 'Understand EEG', CURRENT_DATE, CURRENT_DATE, 0, 'A guide to understand EEGs', 0, USER_SEQ.currval, '');