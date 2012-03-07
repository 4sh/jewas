INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.accueil.bbeeg', 0);

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.nouveau.ne.normal', 1);
UPDATE DOMAIN SET PARENT_REF = 1 WHERE I18N_KEY = 'domain.nouveau.ne.normal';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.eeg.nouveau.ne', 1);
UPDATE DOMAIN SET PARENT_REF = 1 WHERE I18N_KEY = 'domain.eeg.nouveau.ne';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.atteinte.neurologique.nouveau.ne', 1);
UPDATE DOMAIN SET PARENT_REF = 1 WHERE I18N_KEY = 'domain.atteinte.neurologique.nouveau.ne';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.anatomie.cerebrale', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.anatomie.cerebrale';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.physiologie.cerebrale', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.physiologie.cerebrale';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.construction.cerveau', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.construction.cerveau';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.adaptation.vie.extra.uterine', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.adaptation.vie.extra.uterine';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.examen.neurologique.nouveau.ne', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.examen.neurologique.nouveau.ne';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.examens.complementaires.neurologiques', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.examens.complementaires.neurologiques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.developpement', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.suivi.developpement';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.morpholgie', 3);
UPDATE DOMAIN SET PARENT_REF = 5 WHERE I18N_KEY = 'domain.morpholgie';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.vascularisation', 3);
UPDATE DOMAIN SET PARENT_REF = 5 WHERE I18N_KEY = 'domain.vascularisation';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.cellules', 3);
UPDATE DOMAIN SET PARENT_REF = 5 WHERE I18N_KEY = 'domain.cellules';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.premier.trimestre', 3);
UPDATE DOMAIN SET PARENT_REF = 7 WHERE I18N_KEY = 'domain.premier.trimestre';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.deuxieme.trimestre', 3);
UPDATE DOMAIN SET PARENT_REF = 7 WHERE I18N_KEY = 'domain.deuxieme.trimestre';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.troisieme.triemestre', 3);
UPDATE DOMAIN SET PARENT_REF = 7 WHERE I18N_KEY = 'domain.troisieme.triemestre';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.maturation.post.natale', 3);
UPDATE DOMAIN SET PARENT_REF = 7 WHERE I18N_KEY = 'domain.maturation.post.natale';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.scores', 3);
UPDATE DOMAIN SET PARENT_REF = 9 WHERE I18N_KEY = 'domain.scores';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.examen.cliniques', 3);
UPDATE DOMAIN SET PARENT_REF = 9 WHERE I18N_KEY = 'domain.examen.cliniques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.motricite.liberee', 3);
UPDATE DOMAIN SET PARENT_REF = 9 WHERE I18N_KEY = 'domain.motricite.liberee';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.analyse.mouvement.spontanes', 3);
UPDATE DOMAIN SET PARENT_REF = 9 WHERE I18N_KEY = 'domain.analyse.mouvement.spontanes';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.developpement.normal', 3);
UPDATE DOMAIN SET PARENT_REF = 11 WHERE I18N_KEY = 'domain.developpement.normal';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.theories', 4);
UPDATE DOMAIN SET PARENT_REF = 23 WHERE I18N_KEY = 'domain.theories';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.axes', 4);
UPDATE DOMAIN SET PARENT_REF = 23 WHERE I18N_KEY = 'domain.axes';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.etapes', 4);
UPDATE DOMAIN SET PARENT_REF = 23 WHERE I18N_KEY = 'domain.etapes';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.deroulement.scolarite', 3);
UPDATE DOMAIN SET PARENT_REF = 11 WHERE I18N_KEY = 'domain.deroulement.scolarite';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.scolarite.milieu.ordinaire', 4);
UPDATE DOMAIN SET PARENT_REF = 27 WHERE I18N_KEY = 'domain.scolarite.milieu.ordinaire';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.scolarite.adaptee', 4);
UPDATE DOMAIN SET PARENT_REF = 27 WHERE I18N_KEY = 'domain.scolarite.adaptee';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.scolarite.milieu.medical', 4);
UPDATE DOMAIN SET PARENT_REF = 27 WHERE I18N_KEY = 'domain.scolarite.milieu.medical';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.prise.charge.handicap', 3);
UPDATE DOMAIN SET PARENT_REF = 11 WHERE I18N_KEY = 'domain.prise.charge.handicap';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.definitions', 4);
UPDATE DOMAIN SET PARENT_REF = 31 WHERE I18N_KEY = 'domain.definitions';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.classifications', 4);
UPDATE DOMAIN SET PARENT_REF = 31 WHERE I18N_KEY = 'domain.classifications';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.echelles', 4);
UPDATE DOMAIN SET PARENT_REF = 31 WHERE I18N_KEY = 'domain.echelles';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.structures', 4);
UPDATE DOMAIN SET PARENT_REF = 31 WHERE I18N_KEY = 'domain.structures';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.2.ans', 3);
UPDATE DOMAIN SET PARENT_REF = 11 WHERE I18N_KEY = 'domain.suivi.2.ans';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.2.ans.examen.standardise', 4);
UPDATE DOMAIN SET PARENT_REF = 36 WHERE I18N_KEY = 'domain.suivi.2.ans.examen.standardise';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.2.ans.tests.developpement', 4);
UPDATE DOMAIN SET PARENT_REF = 36 WHERE I18N_KEY = 'domain.suivi.2.ans.tests.developpement';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.2.ans.questionnaires', 4);
UPDATE DOMAIN SET PARENT_REF = 36 WHERE I18N_KEY = 'domain.suivi.2.ans.questionnaires';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.5.ans', 3);
UPDATE DOMAIN SET PARENT_REF = 11 WHERE I18N_KEY = 'domain.suivi.5.ans';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.5.ans.examen.standardise', 4);
UPDATE DOMAIN SET PARENT_REF = 40 WHERE I18N_KEY = 'domain.suivi.5.ans.examen.standardise';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.5.ans.tests.cogintifs', 4);
UPDATE DOMAIN SET PARENT_REF = 40 WHERE I18N_KEY = 'domain.suivi.5.ans.tests.cogintifs';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.5.ans.questionnaires', 4);
UPDATE DOMAIN SET PARENT_REF = 40 WHERE I18N_KEY = 'domain.suivi.5.ans.questionnaires';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.age.scolaire', 3);
UPDATE DOMAIN SET PARENT_REF = 11 WHERE I18N_KEY = 'domain.suivi.age.scolaire';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.age.scolaire.examen.standardise', 4);
UPDATE DOMAIN SET PARENT_REF = 44 WHERE I18N_KEY = 'domain.suivi.age.scolaire.examen.standardise';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.age.scolaire.tests.cogintifs', 4);
UPDATE DOMAIN SET PARENT_REF = 44 WHERE I18N_KEY = 'domain.suivi.age.scolaire.tests.cogintifs';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.suivi.age.scolaire.questionnaires', 4);
UPDATE DOMAIN SET PARENT_REF = 44 WHERE I18N_KEY = 'domain.suivi.age.scolaire.questionnaires';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.principes.electrophysiologiques', 2);
UPDATE DOMAIN SET PARENT_REF = 1 WHERE I18N_KEY = 'domain.principes.electrophysiologiques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.enregistrement.eeg.polygraphique', 2);
UPDATE DOMAIN SET PARENT_REF = 3 WHERE I18N_KEY = 'domain.enregistrement.eeg.polygraphique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.materiel', 3);
UPDATE DOMAIN SET PARENT_REF = 48 WHERE I18N_KEY = 'domain.materiel';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.technique.enregistrement', 3);
UPDATE DOMAIN SET PARENT_REF = 48 WHERE I18N_KEY = 'domain.technique.enregistrement';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.technique.analyse.signal', 3);
UPDATE DOMAIN SET PARENT_REF = 48 WHERE I18N_KEY = 'domain.technique.analyse.signal';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.bases.interpretation', 2);
UPDATE DOMAIN SET PARENT_REF = 3 WHERE I18N_KEY = 'domain.bases.interpretation';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.artefacts', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.artefacts';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.compte.rendu', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.compte.rendu';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.demarche.interpretation', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.demarche.interpretation';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.activite.fond', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.activite.fond';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.figures.physiologiques', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.figures.physiologiques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.figures.anormales', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.figures.anormales';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.decharges.critiques', 3);
UPDATE DOMAIN SET PARENT_REF = 53 WHERE I18N_KEY = 'domain.decharges.critiques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.eeg.nouveau.ne.terme', 2);
UPDATE DOMAIN SET PARENT_REF = 3 WHERE I18N_KEY = 'domain.eeg.nouveau.ne.terme';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.stades.vigilance', 3);
UPDATE DOMAIN SET PARENT_REF = 61 WHERE I18N_KEY = 'domain.stades.vigilance';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.mouvements', 3);
UPDATE DOMAIN SET PARENT_REF = 61 WHERE I18N_KEY = 'domain.mouvements';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.organisation.spatiale', 3);
UPDATE DOMAIN SET PARENT_REF = 61 WHERE I18N_KEY = 'domain.organisation.spatiale';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.organisation.temporelle', 3);
UPDATE DOMAIN SET PARENT_REF = 61 WHERE I18N_KEY = 'domain.organisation.temporelle';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.terme.figures.physiologiques', 3);
UPDATE DOMAIN SET PARENT_REF = 61 WHERE I18N_KEY = 'domain.terme.figures.physiologiques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.maturation.eeg.nouveau.ne.premature', 2);
UPDATE DOMAIN SET PARENT_REF = 2 WHERE I18N_KEY = 'domain.maturation.eeg.nouveau.ne.premature';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.eeg.lesions.neurologiques.prematurite', 3);
UPDATE DOMAIN SET PARENT_REF = 67 WHERE I18N_KEY = 'domain.eeg.lesions.neurologiques.prematurite';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.classification.traces', 4);
UPDATE DOMAIN SET PARENT_REF = 68 WHERE I18N_KEY = 'domain.classification.traces';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.lesion.figures.anormales', 4);
UPDATE DOMAIN SET PARENT_REF = 68 WHERE I18N_KEY = 'domain.lesion.figures.anormales';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.rappel.clinique', 4);
UPDATE DOMAIN SET PARENT_REF = 68 WHERE I18N_KEY = 'domain.rappel.clinique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.criteres.analyse', 3);
UPDATE DOMAIN SET PARENT_REF = 67 WHERE I18N_KEY = 'domain.criteres.analyse';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.chronologie', 3);
UPDATE DOMAIN SET PARENT_REF = 67 WHERE I18N_KEY = 'domain.chronologie';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.prematurite', 2);
UPDATE DOMAIN SET PARENT_REF = 4 WHERE I18N_KEY = 'domain.prematurite';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.epidemiologie', 3);
UPDATE DOMAIN SET PARENT_REF = 74 WHERE I18N_KEY = 'domain.epidemiologie';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.consequence.immaturite', 3);
UPDATE DOMAIN SET PARENT_REF = 74 WHERE I18N_KEY = 'domain.consequence.immaturite';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.lesion.neurologique', 3);
UPDATE DOMAIN SET PARENT_REF = 74 WHERE I18N_KEY = 'domain.lesion.neurologique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.surveillance.neurologique', 3);
UPDATE DOMAIN SET PARENT_REF = 74 WHERE I18N_KEY = 'domain.surveillance.neurologique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.devenir.neurologique', 3);
UPDATE DOMAIN SET PARENT_REF = 74 WHERE I18N_KEY = 'domain.devenir.neurologique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.atteintes.neurologiques.aigues', 2);
UPDATE DOMAIN SET PARENT_REF = 4 WHERE I18N_KEY = 'domain.atteintes.neurologiques.aigues';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.accident.vasculaire.cerebral', 3);
UPDATE DOMAIN SET PARENT_REF = 80 WHERE I18N_KEY = 'domain.accident.vasculaire.cerebral';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.ancephalopathie.anoxo.ischemique', 3);
UPDATE DOMAIN SET PARENT_REF = 80 WHERE I18N_KEY = 'domain.ancephalopathie.anoxo.ischemique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.infections.snc', 3);
UPDATE DOMAIN SET PARENT_REF = 80 WHERE I18N_KEY = 'domain.infections.snc';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.traumatismes.cranien.obstetricaux', 3);
UPDATE DOMAIN SET PARENT_REF = 80 WHERE I18N_KEY = 'domain.traumatismes.cranien.obstetricaux';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.troubles.metaboliques.accidentels', 3);
UPDATE DOMAIN SET PARENT_REF = 80 WHERE I18N_KEY = 'domain.troubles.metaboliques.accidentels';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.maladies.neurologiques.revelation.neonatale', 2);
UPDATE DOMAIN SET PARENT_REF = 4 WHERE I18N_KEY = 'domain.maladies.neurologiques.revelation.neonatale';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.syndrome.epiletiques.nouveau.ne', 3);
UPDATE DOMAIN SET PARENT_REF = 86 WHERE I18N_KEY = 'domain.syndrome.epiletiques.nouveau.ne';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.malformations.cerebrales', 3);
UPDATE DOMAIN SET PARENT_REF = 86 WHERE I18N_KEY = 'domain.malformations.cerebrales';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.maladies.metaboliques', 3);
UPDATE DOMAIN SET PARENT_REF = 86 WHERE I18N_KEY = 'domain.maladies.metaboliques';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.lesions.antenatales.accidentelles', 3);
UPDATE DOMAIN SET PARENT_REF = 86 WHERE I18N_KEY = 'domain.lesions.antenatales.accidentelles';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.convulsions.neonatales', 2);
UPDATE DOMAIN SET PARENT_REF = 4 WHERE I18N_KEY = 'domain.convulsions.neonatales';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.physiopathologie', 3);
UPDATE DOMAIN SET PARENT_REF = 91 WHERE I18N_KEY = 'domain.physiopathologie';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.convulsions.epidemiologie', 3);
UPDATE DOMAIN SET PARENT_REF = 91 WHERE I18N_KEY = 'domain.convulsions.epidemiologie';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.semiologie.clinique', 3);
UPDATE DOMAIN SET PARENT_REF = 91 WHERE I18N_KEY = 'domain.semiologie.clinique';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.diagnostic', 3);
UPDATE DOMAIN SET PARENT_REF = 91 WHERE I18N_KEY = 'domain.diagnostic';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.traitement', 3);
UPDATE DOMAIN SET PARENT_REF = 91 WHERE I18N_KEY = 'domain.traitement';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.pronostic', 3);
UPDATE DOMAIN SET PARENT_REF = 91 WHERE I18N_KEY = 'domain.pronostic';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.EEG', 3);
UPDATE DOMAIN SET PARENT_REF = 10 WHERE I18N_KEY = 'domain.EEG';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.ETF', 3);
UPDATE DOMAIN SET PARENT_REF = 10 WHERE I18N_KEY = 'domain.ETF';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.IRM', 3);
UPDATE DOMAIN SET PARENT_REF = 10 WHERE I18N_KEY = 'domain.IRM';

INSERT INTO DOMAIN (I18N_KEY, LEVEL) VALUES ('domain.potentiels.evoques', 3);
UPDATE DOMAIN SET PARENT_REF = 10 WHERE I18N_KEY = 'domain.potentiels.evoques';

COMMIT;



