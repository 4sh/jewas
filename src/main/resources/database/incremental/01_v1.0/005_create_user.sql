CREATE TABLE USER (
                ID INT(10) NOT NULL,
                LOGIN CHAR(10) NOT NULL,
                PASSWORD CHAR(10) NOT NULL,
                NAME CHAR(50) NOT NULL,
                SURNAME CHAR(50) NOT NULL,
                EMAIL CHAR(50) NOT NULL,
                ROLE_REF INT(10) NOT NULL,
                PRIMARY KEY (ID)
);