package com.crypto.reader;

import com.crypto.entity.ICOEntry;
import com.crypto.util.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ICODropReader {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ICODropReader.class);

    /**
     * Base url for retrieving data
     */
    private final String BASE_URL = "https://icodrops.com/";

    /**
     * Dash character used for different combinations of coin URL
     */
    private final String DASH_CHARACTER = "-";

    /**
     * User agent for web requests
     */
    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

    /**
     * Constructor
     */
    public ICODropReader() {}

    public ICOEntry extractDetails(String url) {
        try {
            Document document = Jsoup.connect(url).userAgent(this.USER_AGENT).get();

            if (document != null) {
                logger.info("Creating row entity from {}", url);
                ICOEntry entry = new ICOEntry(document);

                return entry;
            }
        } catch (IOException ex) {
            logger.error("Unable to retrieve ICO Drops page at {}", url);
        }
        return null;
    }

    /**
     * Attempt to find the JSoup HTML document given the ico name based on different combinations
     * @param icoName
     * @return
     */
    public ICOEntry inferDetails(String icoName) {
        Document document = retrieveJsoupDocument(icoName);

        if (document != null) {
            logger.info("Creating row entity for {}", icoName);
            ICOEntry entry = new ICOEntry(document);

            return entry;
        }

        return null;
    }

    /**
     * To handle coins that have spaces or camelcase in the name,
     * try different combinations to find valid URL
     * @param icoName
     * @return
     */
    private Document retrieveJsoupDocument(String icoName) {
        String requestUrl = StringUtils.EMPTY_STRING;
        String sanitizedIcoName = StringUtils.EMPTY_STRING;
        Document doc = null;

        // If it doesn't, try to find the name
        sanitizedIcoName = StringUtils.sanitizeAlphabeticalStringValue(icoName);
        requestUrl = this.BASE_URL + sanitizedIcoName + "/";

        // Try the base ico name itself
        try {
            try {
                doc = Jsoup.connect(requestUrl).userAgent(this.USER_AGENT).get();
            } catch (HttpStatusException ex) {
                // Try a dash in between each character in the name
                for (int i=1; i<sanitizedIcoName.length(); i++) {
                    String modifiedIcoName = sanitizedIcoName.substring(0, i) + this.DASH_CHARACTER + sanitizedIcoName.substring(i, sanitizedIcoName.length());
                    requestUrl = this.BASE_URL + modifiedIcoName + "/";
                    try {
                        doc = Jsoup.connect(requestUrl).userAgent(this.USER_AGENT).get();
                        break;
                    } catch (HttpStatusException hex) {
                        continue;
                    }
                }
            }
        } catch (IOException ex) {
        } finally {
            if (doc == null) {
                logger.error("Unable to retrieve ICO Drops page for {}", icoName);
            }
            return doc;
        }
    }
}
