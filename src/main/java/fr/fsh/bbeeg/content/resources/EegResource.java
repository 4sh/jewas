package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.EegSettings;
import jewas.json.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author driccio
 */
public class EegResource {
    private static final String EEG_PREFIX = "eeg_";

    private final ContentDao contentDao;
    private final String contentPath;

    public EegResource(ContentDao _contentDao, String _contentPath) {
        contentDao = _contentDao;
        contentPath = _contentPath;
    }

    private String getFileName(Long eegId) {
        return EEG_PREFIX + eegId;
    }

    public void updateEegSettings(Long contentId, EegSettings eegSettings, String mode) {
        String eegSettingString = Json.instance().toJsonString(eegSettings, null);
        Path path;

        if (mode == null || !"tmp".equals(mode)) {
            path = Paths.get(contentPath, getFileName(contentId));
        } else {
            path = TempFiles.getPath(getFileName(contentId));
        }

        try {
            Files.newOutputStream(path).write(eegSettingString.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (mode == null || !"tmp".equals(mode)) {
            contentDao.updateContentOfContent(contentId, path.toString());
        }
    }

    public String getEegSettings(Long contentId) {
        Path path;

        if (TempFiles.tmpFileExists(getFileName(contentId))) {
            path = TempFiles.getPath(getFileName(contentId));
        } else {
            String url = contentDao.getContentUrl(contentId);

            if (url == null || "".equals(url)) {
                return null;
            }

            path = Paths.get(url);
        }

        byte[] bytes = null;

        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }

    public void cleanTmp(Long eegId) {
        TempFiles.removeFile(getFileName(eegId));

        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/clean/" + eegId);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
