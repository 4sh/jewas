package fr.fsh.bbeeg.content.persistence.mocks;


import fr.fsh.bbeeg.i18n.persistence.I18nDao;

public class I18nDaoMock extends I18nDao {

    public I18nDaoMock() {
        super(null);
    }

    public String translation(String key, String locale) {
        return "";
    }

}
