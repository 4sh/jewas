package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author carmarolli
 */
public class EegResource {

    /**
     * CLass logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(EegResource.class);

    private final ContentDao contentDao;
    private final String contentPath;

    public EegResource(ContentDao _contentDao, String _contentPath) {
        contentDao = _contentDao;
        contentPath = _contentPath;
    }

    public void updateEegDocumentUri(Long contentId, String path) {
       contentDao.updateContentOfContent(contentId, path.toString());
    }

    /**
     * Returns the eeg settings file content as JSON string.
     * @param eegId the eeg document identifier
     * @return a JSON string.
     */
    public String getEegSettings(Long eegId) {
        URL url = null;
        StringBuilder jsonResponse = new StringBuilder();
        try {
            url = new URL(BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/visio/eeg/settings/" + eegId);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonResponse.append(inputLine);
            }
            in.close();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            logger.error("Cannot build URL to access to visio application. ", e);
        } catch (IOException e) {
            logger.error("Cannot perform GET to url: " + url.toString(), e);
        }
        return jsonResponse.toString();
    }

    /**
     * In edition mode, copy the content of a saved eeg document into tmp directory for edition work.
     * @param eegId the eeg document id (coming from database)
     */
    public void copyContentToTmp(Long eegId) {
        URL url = null;
        try {
            url = new URL(BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/visio/copyTmp/" + eegId);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                //  content.append(inputLine);
            }
            in.close();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            logger.error("Cannot build URL to access to visio application", e.getMessage());
        } catch (IOException e) {
            logger.error("Cannot perform POST to url:" + url.toString(), e.getMessage());
        }
    }
}
