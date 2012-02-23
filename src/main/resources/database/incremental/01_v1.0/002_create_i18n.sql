CREATE TABLE I18N_TABLE (
                I18N_KEY CHAR(64) NOT NULL,
                LANGUAGE CHAR(2) NOT NULL,
                LABEL VARCHAR(200) NOT NULL,
                PRIMARY KEY (I18N_KEY, LANGUAGE)
);