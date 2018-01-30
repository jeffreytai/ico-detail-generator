package com.crypto.writer;

import com.crypto.GoogleSheetsConstants;
import com.crypto.entity.ICOEntry;
import com.crypto.util.StringUtils;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpreadsheetWriter {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SpreadsheetWriter.class);

    /**
     * Date format for use in Sheet titles
     */
    private final String DATE_FORMAT = "MM-dd";

    /**
     * Slack username to post as
     */
    private final String SLACK_USERNAME = "investment-spreadsheet-bot";

    /**
     * Service for accessing Google Sheets
     */
    private Sheets googleSheetsService;

    /**
     * Map of column names from original spreadsheet
     */
    private Map<String, Integer> columnIndexMap;

    /**
     * ICO entries that already exist on the spreadsheet
     */
    private Map<String, ICOEntry> existingEntries;

    /**
     * Constructor
     */
    public SpreadsheetWriter(Sheets googleSheetsService, Map<String, Integer> columnIndexMap, Map<String, ICOEntry> existingEntries) {
        this.googleSheetsService = googleSheetsService;
        this.columnIndexMap = columnIndexMap;
        this.existingEntries = existingEntries;
    }

    public void processResults(ICOEntry entry) {
        // Get sheet id
        String sheetId = getSheetId();

        // Find index of row to insert in, account for header row
        Integer rowIndex = findAvailableRow(entry);

        // Post ICO details to sheet
        postResults(GoogleSheetsConstants.SHEET_TITLE, entry, rowIndex);

        // Send slack alert
//        sendSlackAlert(spreadsheetUrl, sheetId);
    }

    /**
     * Gets the sheet id from the sheet title
     * @return
     */
    private String getSheetId() {
        try {
            List<Sheet> sheets = this.googleSheetsService.spreadsheets().get(GoogleSheetsConstants.SPREADSHEET_ID).execute().getSheets();
            for (Sheet sheet : sheets) {
                SheetProperties properties = sheet.getProperties();
                if (properties.get("title").toString().equals(GoogleSheetsConstants.SHEET_TITLE)) {
                    return properties.get("sheetId").toString();
                }
            }
        } catch (IOException ex) {
            logger.error("Error in retrieving google sheets");
        }
        return null;
    }

    /**
     * If the entry already exists, return that index so it can be overwritten.
     * The index has to be incremented by 1 to account for the header row.
     *
     * If the entry doesn't exist, return the first available row.
     * The index has to be incremented by 2 to account for the header row and the newly inserted row.
     *
     * @param entry
     * @return
     */
    private Integer findAvailableRow(ICOEntry entry) {
        String tokenName = entry.getToken();

        int index=1;
        for (Map.Entry<String, ICOEntry> kv : this.existingEntries.entrySet()) {
            if (kv.getKey().equals(tokenName)) {
                return index + 1;
            }
            ++index;
        }

        return this.existingEntries.size() + 2;
    }

    /**
     * Send results from ICO Drop list to spreadsheet
     * @param sheetTitle
     * @param entry
     */
    private void postResults(String sheetTitle, ICOEntry entry, Integer rowIndex) {
        String range = sheetTitle + "!A" + rowIndex;
        List<List<Object>> sheetData = new ArrayList<>();

        List<Object> rowData = new ArrayList<>();

        // Find the matching column for each field in the ICOEntry through reflection
        Field[] icoEntryFields = entry.getClass().getDeclaredFields();
        for (String columnName : this.columnIndexMap.keySet()) {
            List<Field> matchedFields =
                    Arrays.stream(icoEntryFields)
                            .filter(f ->
                                    StringUtils.areStringsEqualIgnoreCase(f.getName(), StringUtils.sanitizeAlphabeticalStringValue(columnName)))
                            .collect(Collectors.toList());

            if (matchedFields.size() != 1) {
                logger.error("Missing field for {}", columnName);
                rowData.add(StringUtils.EMPTY_STRING);
                continue;
            }

            Field matchedField = matchedFields.get(0);
            try {
                Object fieldValue = new PropertyDescriptor(matchedField.getName(), entry.getClass()).getReadMethod().invoke(entry);

                if (fieldValue == null) {
                    rowData.add(StringUtils.EMPTY_STRING);
                }
                else {
                    rowData.add(fieldValue);
                }

            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException ex) {
                logger.error("Illegal access for ICO Entry {}", entry.getToken());
            }
        }

        sheetData.add(rowData);

        ValueRange valueRange = new ValueRange();
        valueRange.setRange(range);
        valueRange.setValues(sheetData);

        List<ValueRange> oList = new ArrayList<>();
        oList.add(valueRange);

        BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
        oRequest.setValueInputOption("RAW");
        oRequest.setData(oList);

        try {
            googleSheetsService.spreadsheets().values().batchUpdate(GoogleSheetsConstants.SPREADSHEET_ID, oRequest).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        logger.info("ICO drop results posted to spreadsheet");
    }
}
