package com.crypto.processor;

import com.crypto.authentication.Authentication;
import com.crypto.authentication.GoogleSheetsAuthentication;
import com.crypto.entity.Entry;
import com.crypto.enums.SourceType;
import com.crypto.reader.Reader;
import com.crypto.reader.SpreadsheetReader;
import com.crypto.writer.SpreadsheetWriter;
import com.google.api.services.sheets.v4.Sheets;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class SpreadsheetProcessor {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SpreadsheetProcessor.class);

    public SpreadsheetProcessor() {}

    // TODO: Fix Basic authentication

    /**
     * Connect to Google Sheets API
     * Pull data from ICO spreadsheet
     * Retrieve data from ICO Drops page
     * Store data in personal Google Sheets
     */
    public void process(String icoName, String url) {
        try {
            // Connect to Google Sheets API
            Sheets service = GoogleSheetsAuthentication.getSheetsService(Authentication.OAUTH);

            // Pull existing data from spreadsheet
            SpreadsheetReader spreadsheetReader = new SpreadsheetReader(service);
            Map<String, Entry> existingEntries = spreadsheetReader.extractEntries();
            Map<String, Integer> columnIndexMap = spreadsheetReader.getColumnIndexMap();

            // Create entity from relevant page
            Entry entry = createEntity(existingEntries, icoName, url);

            // Write the entry to the spreadsheet
            if (entry != null) {
                SpreadsheetWriter writer = new SpreadsheetWriter(service, columnIndexMap, existingEntries);
                writer.processResults(entry);
            }
        } catch (IOException ex) {
            logger.error("Error in processing ICO spreadsheet");
        }
    }

    /**
     * Create ICO entity attempting to read from ICO Drops first then if unfound, ICO Bench
     * @param existingEntries
     * @param icoName
     * @param url
     * @return
     */
    private Entry createEntity(Map<String, Entry> existingEntries, String icoName, String url) {
        // Retrieve ICO Drops data for each ICO entry
        Reader dataReader = new Reader(SourceType.ICODrop);

        // Attempt to create entity from ICO Drops
        Entry entry = createEntity(existingEntries, icoName, url, dataReader);

        // If entry is still not populated, attempt to read from ICOBench
        if (entry == null) {
            dataReader = new Reader(SourceType.ICOBench);
            entry = createEntity(existingEntries, icoName, url, dataReader);
        }

        return entry;
    }

    private Entry createEntity(Map<String, Entry> existingEntries, String icoName, String url, Reader dataReader) {
        Entry entry = null;

        // First attempt to read from ICODrop
        // If the existing entry is identical, don't make any changes to the sheet
        if (existingEntries.containsKey(icoName)) {
            Entry existingEntry = existingEntries.get(icoName);
            entry = dataReader.extractDetails(existingEntry.getUrl());

            if (existingEntry.equals(entry)) {
                return null;
            }
            else {
                entry.mergeEntry(existingEntry);
            }
        }
        else if (!Strings.isNullOrEmpty(url)) {
            entry = dataReader.extractDetails(url);
        }
        else {
            entry = dataReader.inferDetails(icoName);
        }

        return entry;
    }
}
