package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.content.persistence.ContentDao;
import fr.fsh.bbeeg.content.pojos.EegSettings;
import jewas.json.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author driccio
 */
public class EegResource {
    private final ContentDao contentDao;
    private final String contentPath;

    public EegResource(ContentDao _contentDao, String _contentPath) {
        contentDao = _contentDao;
        contentPath = _contentPath;
    }

    public void updateEegSettings(Long contentId, EegSettings eegSettings) {
        String eegSettingString = Json.instance().toJsonString(eegSettings, null);
        Path path = Paths.get(contentPath, "eeg_"+contentId);

        try {
            Files.newOutputStream(path).write(eegSettingString.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        contentDao.updateContentOfContent(contentId, path.toString());
    }

    public String getEegSettings(Long contentId) {
        String url = contentDao.getContentUrl(contentId);

        if (url == null || "".equals(url)) {
            return null;
        }

        Path path = Paths.get(url);
        byte[] bytes = null;

        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }
}
