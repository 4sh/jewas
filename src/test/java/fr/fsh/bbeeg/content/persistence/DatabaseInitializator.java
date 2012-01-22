package fr.fsh.bbeeg.content.persistence;

import fr.fsh.bbeeg.ScriptRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInitializator {

    public static void initDB(String jdbcURL) throws SQLException, IOException {
        Connection dbInitializationConnection = DriverManager.getConnection(jdbcURL, "sa", "sa");
        ScriptRunner sr = new ScriptRunner(dbInitializationConnection, true, true);
        String scriptsLocation = "/database/incremental/01_v1.0/";

            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "001_create_sequences.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "002_create_i18n.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "003_create_index_criteria.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "004_create_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "005_create_user.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "006_create_role.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "007_create_content.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "008_create_tags.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "009_create_content_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "010_create_content_index_criteria.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "011_create_content_comment.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "012_alter_index_criteria.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "013_alter_user.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "014_alter_content.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "015_alter_content_domain.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "016_alter_content_index_criteria.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "017_alter_content_comment.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "018_index_tags.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "019_insert_i18n.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "020_insert_role.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "021_insert_user.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "022_insert_domain.sql")));
            // V1.1
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "022_v1.1/" + "001_alter_content_add_version.sql")));
            sr.runScript(new InputStreamReader(DatabaseInitializator.class.getResourceAsStream(scriptsLocation + "022_v1.1/" + "002_alter_content_delete_ancestor_constraint.sql")));

    }

}
