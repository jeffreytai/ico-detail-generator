package com.crypto.processor;

import com.crypto.authentication.Authentication;
import com.crypto.authentication.GoogleSheetsAuthentication;
import com.crypto.entity.ICOEntry;
import com.crypto.reader.ICODropReader;
import com.crypto.reader.SpreadsheetReader;
import com.crypto.writer.SpreadsheetWriter;
import com.google.api.services.sheets.v4.Sheets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    public void process(String icoName) {
        try {
            // Connect to Google Sheets API
            Sheets service = GoogleSheetsAuthentication.getSheetsService(Authentication.OAUTH);

            // Pull existing data from spreadsheet
            SpreadsheetReader reader = new SpreadsheetReader(service);
            Map<String, ICOEntry> existingEntries = reader.extractEntries();
            Map<String, Integer> columnIndexMap = reader.getColumnIndexMap();

            // Retrieve ICO Drops data for each ICO entry
            ICODropReader icoDropReader = new ICODropReader();
            ICOEntry entry = null;

            // If the existing entry is identical, don't make any changes to the sheet
            if (existingEntries.containsKey(icoName)) {
                ICOEntry existingEntry = existingEntries.get(icoName);
                entry = icoDropReader.extractDetails(existingEntry.getUrl());

                if (existingEntry.equals(entry)) {
                    return;
                }
                else {
                    entry.assignDefaultEmptyFields(existingEntry);
                }
            }
            else {
                entry = icoDropReader.inferDetails(icoName);
            }

            // Write the entry to the spreadsheet
            SpreadsheetWriter writer = new SpreadsheetWriter(service, columnIndexMap, existingEntries);
            writer.processResults(entry);
        } catch (IOException ex) {
            logger.error("Error in processing ICO spreadsheet");
        }
    }
}
