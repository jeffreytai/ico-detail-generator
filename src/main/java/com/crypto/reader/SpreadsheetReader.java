package com.crypto.reader;

import com.crypto.GoogleSheetsConstants;
import com.crypto.entity.ICOEntry;
import com.crypto.util.StringUtils;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpreadsheetReader {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SpreadsheetReader.class);

    /**
     * Service for accessing Google Sheets
     */
    private Sheets googleSheetsService;

    /**
     * Map of column names from spreadsheet
     */
    private Map<String, Integer> columnIndexMap;

    public SpreadsheetReader(Sheets googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
        this.columnIndexMap = new HashMap<>();
    }

    /**
     * Extract data from the ICO spreadsheet
     */
    public Map<String, ICOEntry> extractEntries() {
        Map<String, ICOEntry> entries = new LinkedHashMap<>();

        try {
            // Shorthand notation for retrieving all cells on a sheet
            List<String> ranges = new ArrayList<>();
            ranges.add(GoogleSheetsConstants.SHEET_TITLE + "!1:65536");

            // Extract each entry (row value) from the spreadsheet
            BatchGetValuesResponse response =
                    googleSheetsService.spreadsheets()
                            .values()
                            .batchGet(GoogleSheetsConstants.SPREADSHEET_ID)
                            .setRanges(ranges)
                            .execute();

            if (response != null) {
                List<List<Object>> values =
                        response.getValueRanges()
                                .stream()
                                .flatMap(valueRange -> valueRange.getValues().stream())
                                .collect(Collectors.toList());

                // Force the row to match the number of columns in the header
                if (values.size() > 1) {
                    int headerColumnCount = values.get(0).size();

                    for (int rowIndex = 1; rowIndex<values.size(); rowIndex++) {
                        int missingColumnCount = headerColumnCount - values.get(rowIndex).size();

                        while (missingColumnCount > 0) {
                            values.get(rowIndex).add(StringUtils.EMPTY_STRING);
                            --missingColumnCount;
                        }
                    }
                }

                // Create a map of the column name to its index in case the spreadsheet order changes
                boolean headerRow = true;
                for (List<Object> entry : values) {
                    if (headerRow) {
                        this.columnIndexMap = generateColumnIndexMap(entry);
                        headerRow = false;
                        logger.info("Created map of column name to index");
                    } else {
                        ICOEntry detailedIco = new ICOEntry(entry, this.columnIndexMap);
                        entries.put(detailedIco.getToken(), detailedIco);
                    }

                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return Collections.unmodifiableMap(entries);
    }

    /**
     * Create a mapping for each valid column index to name
     * Preserve the order with a LinkedHashMap
     * @param header
     * @return
     */
    private Map<String, Integer> generateColumnIndexMap(List<Object> header) {
        Map<String, Integer> map =
                IntStream.range(0, header.size())
                        .boxed()
                        .collect(Collectors.toMap(kv -> header.get(kv).toString(), kv -> kv,
                                (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }

    /***********************
     * Getters and setters
     ***********************/

    public Map<String, Integer> getColumnIndexMap() {
        return columnIndexMap;
    }
}
