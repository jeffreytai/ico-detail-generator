package com.crypto;

import com.crypto.exception.InvalidArgumentException;
import com.crypto.processor.SpreadsheetProcessor;
import com.crypto.util.StringUtils;

import java.util.Arrays;

public class Application {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            throw new InvalidArgumentException("No arguments provided");
        }

        SpreadsheetProcessor processor = new SpreadsheetProcessor();

        // If the URL is provided, extract the name
        String coin = StringUtils.EMPTY_STRING;
        String url = StringUtils.EMPTY_STRING;

        String lastArg = args[args.length - 1];
        if (StringUtils.isUrlFormat(lastArg)) {
            String[] subArray = Arrays.copyOfRange(args, 0, args.length - 1);
            coin = String.join(" ", subArray);
            url = lastArg;
        }
        else {
            coin = String.join(" ", args);
        }

        // Process the current arguments
        processor.process(coin, url);
    }
}