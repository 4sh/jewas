CREATE SEQUENCE INDEX_CRITERIA_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE DOMAIN_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE USER_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE CONTENT_SEQ INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE CONTENT_COMMENT_SEQ INCREMENT BY 1 START WITH 1000;


CREATE TABLE I18N_TABLE (
                I18N_KEY CHAR(50) NOT NULL,
                LANGUAGE CHAR(2) NOT NULL,
                LABEL VARCHAR(200) NOT NULL,
                PRIMARY KEY (I18N_KEY, LANGUAGE)
);


CREATE TABLE INDEX_CRITERIA (
                ID INT(10) NOT NULL,
                I18N_KEY CHAR(50) NOT NULL,
                PARENT_REF INT(10),
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
                CONTENT_ANCESTOR_REF INT(10),
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

CREATE TABLE CONTENT_COMMENT (
                ID INT(10) NOT NULL,
                CONTENT_REF INT(10) NOT NULL,
                COMMENT VARCHAR(200) NOT NULL,
                PRIMARY KEY (ID)
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

ALTER TABLE CONTENT ADD CONSTRAINT content_ancestor_content_fk
FOREIGN KEY (CONTENT_ANCESTOR_REF)
REFERENCES CONTENT (ID)
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

ALTER TABLE CONTENT_COMMENT ADD CONSTRAINT cc_content_fk
FOREIGN KEY (CONTENT_REF)
REFERENCES CONTENT (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;


INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.manipulation', 'fr', 'Manipulation');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.eegAnalyse', 'fr', 'Analyse EEG');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.eegInterpretation', 'fr', 'Interprétation EEG');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('domain.experimentalResearch', 'fr', 'Recherche Experimentale');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.text', 'fr', 'Texte');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.image', 'fr', 'Image');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.video', 'fr', 'Video');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.audio', 'fr', 'Audio');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.document', 'fr', 'Document');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('contentType.eeg', 'fr', 'EEG');

INSERT INTO USER (ID, NAME, SURNAME, EMAIL) VALUES (USER_SEQ.nextval, 'Sponge', 'Bob', 'bob.sponge@sea.fr');

INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.manipulation');
INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.eegAnalyse');
INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.eegInterpretation');
INSERT INTO DOMAIN (ID, I18N_KEY) VALUES (DOMAIN_SEQ.nextval, 'domain.experimentalResearch');

INSERT INTO CONTENT (ID, TITLE, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, DESCRIPTION, CONTENT_TYPE, AUTHOR_REF, FILE_URI) VALUES (CONTENT_SEQ.nextval, 'Mon premier contenu', CURRENT_DATE, CURRENT_DATE, 2, 'Ceci est mon premier contenu :)', 0, USER_SEQ.currval, '');
INSERT INTO CONTENT (ID, TITLE, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, DESCRIPTION, CONTENT_TYPE, AUTHOR_REF, FILE_URI) VALUES (CONTENT_SEQ.nextval, 'Mon deuxième contenu', CURRENT_DATE, CURRENT_DATE, 0, 'Ceci est mon deuxième contenu :)', 0, USER_SEQ.currval, '');
INSERT INTO CONTENT (ID, TITLE, CREATION_DATE, LAST_MODIFICATION_DATE, STATUS, DESCRIPTION, CONTENT_TYPE, AUTHOR_REF, FILE_URI) VALUES (CONTENT_SEQ.nextval, 'Understand EEG', CURRENT_DATE, CURRENT_DATE, 1, 'A guide to understand EEGs', 0, USER_SEQ.currval, '');

INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C1', 'fr', 'ENFANT');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C1', NULL);
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C2', 'fr', 'EXAMEN');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C2', NULL);
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C3', 'fr', 'SIGNAL');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C3', NULL);
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C4', 'fr', 'Terme de naissance');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C4', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C5', 'fr', 'Poids de naissance');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C5', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C6', 'fr', 'Genre');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C6', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C7', 'fr', 'Année de naissance');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C7', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C8', 'fr', 'Gestité');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C8', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C9', 'fr', 'Parité');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C9', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C10', 'fr', 'Nombre de fœtus');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C10', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C11', 'fr', 'Pathologie anténatale');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C11', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C12', 'fr', 'Pathologie périnatale');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C12', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C13', 'fr', 'Pathologie néonatale');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C13', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C14', 'fr', 'Imagerie cérébrale');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C14', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C15', 'fr', 'Suivi EEG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C15', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C16', 'fr', 'Devenir');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C16', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C1');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C17', 'fr', 'Type');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C17', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C2');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C18', 'fr', 'Conditions techniques');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C18', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C2');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C19', 'fr', 'Condition cliniques');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C19', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C2');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C20', 'fr', 'Résultats');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C20', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C2');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C21', 'fr', 'Evénements');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C21', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C3');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C22', 'fr', 'Aspects normaux');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C22', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C3');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C23', 'fr', 'Aspects anormaux');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C23', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C3');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.C24', 'fr', 'technique');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.C24', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C4');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS1', 'fr', 'ETF');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS1', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C14');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS2', 'fr', 'TDM');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS2', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C14');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS3', 'fr', 'IRM');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS3', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C14');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS4', 'fr', 'Autre');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS4', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C14');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS5', 'fr', '1 an');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS5', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C16');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS6', 'fr', '2 ans');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS6', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C16');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS7', 'fr', '5 ans');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS7', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C16');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS8', 'fr', '8 ans');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS8', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C16');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS9', 'fr', 'EEG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS9', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C17');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS10', 'fr', 'ECG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS10', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C17');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS11', 'fr', 'Terme corrigé');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS11', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS12', 'fr', 'Age en jours de vie');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS12', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS13', 'fr', 'Stabilité');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS13', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS14', 'fr', 'Respiration');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS14', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS15', 'fr', 'Digestif');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS15', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS16', 'fr', 'Neuro');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS16', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS17', 'fr', 'ETF');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS17', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS18', 'fr', 'IRM');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS18', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS19', 'fr', 'Traitement en cours');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS19', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS20', 'fr', 'Durée');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS20', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C18');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS21', 'fr', 'Nombre électrode EEG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS21', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C18');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS22', 'fr', 'Autres');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS22', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C18');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS23', 'fr', 'Qualité');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS23', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C18');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS24', 'fr', 'Format');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS24', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C18');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS25', 'fr', 'Filtres');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS25', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C18');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS26', 'fr', 'Conclusion');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS26', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C20');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS27', 'fr', 'Organisation globale');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS27', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C20');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS28', 'fr', 'Crises');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS28', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C20');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS29', 'fr', 'Alimentation');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS29', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C21');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS30', 'fr', 'Crise');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS30', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C21');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS31', 'fr', 'Soins');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS31', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C21');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS32', 'fr', 'EEG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS32', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C22');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS33', 'fr', 'ECG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS33', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C22');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CS34', 'fr', 'EEG');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CS34', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.C23');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS1', 'fr', 'Recueil');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS1', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS5');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS2', 'fr', 'Recueil');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS2', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS6');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS3', 'fr', 'Recueil');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS3', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS7');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS4', 'fr', 'Recueil');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS4', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS8');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS5', 'fr', 'Résultat');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS5', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS5');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS6', 'fr', 'Résultat');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS6', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS6');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS7', 'fr', 'Résultat');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS7', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS7');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS8', 'fr', 'Résultat');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS8', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS8');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS9', 'fr', 'Sédatif');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS9', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS10', 'fr', 'Anticonvulsivant');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS10', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS11', 'fr', 'Hypothermie');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS11', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS19');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS12', 'fr', 'Pointes');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS12', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS23');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS13', 'fr', 'Déchargent');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS13', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS23');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS14', 'fr', 'Crise');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS14', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS23');
INSERT INTO I18N_TABLE (I18N_KEY, LANGUAGE , LABEL) VALUES ('criteria.CSS15', 'fr', 'Figure modifiées');
INSERT INTO INDEX_CRITERIA (ID, I18N_KEY, PARENT_REF) VALUES (DOMAIN_SEQ.nextval, 'criteria.CSS15', SELECT ID FROM INDEX_CRITERIA WHERE I18N_KEY = 'criteria.CS23');