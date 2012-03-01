package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.ScriptRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInitializator {

    public static void initDB(String jdbcURL, String username, String password) throws SQLException, IOException {
        Connection dbInitializationConnection = DriverManager.getConnection(jdbcURL, username, password);
        ScriptRunner sr = new ScriptRunner(dbInitializationConnection, true, true);
        String scriptsLocation = "/database/incremental/01_v1.0/";

            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "001_create_content_status.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "002_create_i18n.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "003_create_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "004_create_user.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "005_create_role.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "006_create_content.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "007_create_tags.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "008_create_content_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "009_create_content_comment.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "010_insert_i18n.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "011_insert_role.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "012_insert_user.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "013_insert_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "014_insert_content_status.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "015_alter_user.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "016_alter_content.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "017_alter_content_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "018_alter_content_comment.sql")));
            // V1.1
    }

    public static void cleanupDB(String jdbcURL, String username, String password) throws SQLException, IOException {
        Connection dbInitializationConnection = DriverManager.getConnection(jdbcURL, username, password);
        ScriptRunner sr = new ScriptRunner(dbInitializationConnection, true, true);
        String scriptsLocation = "/scripts/";

        sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "cleanUp.sql")));
    }
    
}
