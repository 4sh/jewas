package fr.fsh.bbeeg.content.resources;

import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.content.persistence.ContentDao;

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

    public void updateEegDocumentUri(Long contentId, String path) {
       contentDao.updateContentOfContent(contentId, path.toString());
    }

    public void cleanTmp(Long eegId) {
        TempFiles.removeFile(getFileName(eegId));

        StringBuilder content = new StringBuilder();

        /*try {
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
        }*/
    }
}
