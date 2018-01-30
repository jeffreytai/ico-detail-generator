package com.crypto;

import com.crypto.processor.SpreadsheetProcessor;

public class Application {

    public static void main(String[] args) {
        SpreadsheetProcessor processor = new SpreadsheetProcessor();
        processor.process("WePower");
    }
}