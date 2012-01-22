INSERT INTO USER (ID, LOGIN, PASSWORD, NAME, SURNAME, EMAIL, ROLE_REF) VALUES (USER_SEQ.nextval, 'bbeeg-etu', 'test', 'Dubois', 'Pascal', 'pascal.dubois@yahoo.fr', 1000);
INSERT INTO USER (ID, LOGIN, PASSWORD, NAME, SURNAME, EMAIL, ROLE_REF) VALUES (USER_SEQ.nextval, 'bbeeg-ens', 'test', 'Martin', 'Robert', 'robert.Martin@yahoo.fr', 1001);
INSERT INTO USER (ID, LOGIN, PASSWORD, NAME, SURNAME, EMAIL, ROLE_REF) VALUES (USER_SEQ.nextval, 'bbeeg-mod', 'test', 'Dupont', 'Jeanne', 'jeanne.dupont@yahoo.fr', 1002);
COMMIT;