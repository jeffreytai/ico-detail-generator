package com.crypto.entity;

import com.crypto.enums.SourceType;
import com.crypto.util.StringUtils;
import com.google.common.base.Strings;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Entry {

    private static final Logger logger = LoggerFactory.getLogger(Entry.class);

    /**************
     * Fields
     *************/

    /**
     * Coin name
     */
    private String token;

    /**
     * Coin ticker
     */
    private String ticker;

    /**
     * Type, utility, or purpose of coin
     * Will be manually inserted
     */
    private String type;

    /**
     * Total number of tokens minted
     */
    private String totalTokens;

    /**
     * ICO fundraising goal in USD
     */
    private String fundraisingGoal;

    /**
     * Amount of tokens available for token sale as a percentage
     */
    private String availableForTokenSale;

    /**
     * ICO price in USD
     */
    private String icoTokenPrice;

    /**
     * Summary of project
     */
    private String projectSummary;

    /**
     * Notable team members
     * Will be manually inserted
     */
    private String team;

    /**
     * Notable advisors
     * Will be manually inserted
     */
    private String advisors;

    /**
     * Optional bonus details
     */
    private String bonusForTheFirst;

    /**
     * Details about presale
     * Will be manually inserted
     */
    private String presaleInformation;

    /**
     * Start of public sale
     */
    private String icoStartDate;

    /**
     * Issuance details
     */
    private String tokenIssue;

    /**
     * Type of token (e.g ERC20 or NEP5)
     */
    private String tokenType;

    /**
     * Details on token amount sold for presale
     */
    private String soldOnPresale;

    /**
     * Details on whitelist availability and dates
     */
    private String whitelist;

    /**
     * Individual max/min contribution
     */
    private String minMaxPersonalCap;

    /**
     * Predicted hype rate
     */
    private String hypeRate;

    /**
     * Predicted risk rate
     */
    private String riskRate;

    /**
     * Predicted return on investment rate
     */
    private String roiRate;

    /**
     * Overall score (e.g. Very high interest)
     */
    private String overallScore;

    /**
     * ICODrops URL
     */
    private String url;

    /**
     * Status of registering to the whitelist
     */
    private String registrationStatus;

    /**
     * Which account registration is under
     */
    private String registeredAs;

    /**
     * Status of whitelist
     */
    private String whitelistApproved;

    /**
     * Status of KYC
     */
    private String kycApproved;

    /**
     * If ICO has been purchased
     */
    private String purchased;


    /***************
     * Constructors
     **************/
    public Entry(String token, String ticker, String type, String totalTokens, String fundraisingGoal, String availableForTokenSale, String icoTokenPrice,
                    String projectSummary, String team, String advisors, String bonusForTheFirst, String presaleInformation, String icoStartDate,
                    String tokenIssue, String tokenType, String soldOnPresale, String whitelist, String minMaxPersonalCap, String hypeRate, String riskRate,
                    String roiRate, String overallScore, String url, String registrationStatus, String registeredAs, String whitelistApproved, String kycApproved,
                    String purchased) {
        this.token = token;
        this.ticker = ticker;
        this.type = type;
        this.totalTokens = totalTokens;
        this.fundraisingGoal = fundraisingGoal;
        this.availableForTokenSale = availableForTokenSale;
        this.icoTokenPrice = icoTokenPrice;
        this.projectSummary = projectSummary;
        this.team = team;
        this.advisors = advisors;
        this.bonusForTheFirst = bonusForTheFirst;
        this.presaleInformation = presaleInformation;
        this.icoStartDate = icoStartDate;
        this.tokenIssue = tokenIssue;
        this.tokenType = tokenType;
        this.soldOnPresale = soldOnPresale;
        this.whitelist = whitelist;
        this.minMaxPersonalCap = minMaxPersonalCap;
        this.hypeRate = hypeRate;
        this.riskRate = riskRate;
        this.roiRate = roiRate;
        this.overallScore = overallScore;
        this.url = url;
        this.registrationStatus = registrationStatus;
        this.registeredAs = registeredAs;
        this.whitelistApproved = whitelistApproved;
        this.kycApproved = kycApproved;
        this.purchased = purchased;
    }

    /**
     * Creates an ICOEntry entity with a JSoup HTML document retrieved from the ICODrops page
     * @param document
     */
    public Entry(SourceType sourceType, Document document) {
        String url = document.location();

        String coinName = StringUtils.EMPTY_STRING,
                hypeRate = StringUtils.EMPTY_STRING,
                riskRate = StringUtils.EMPTY_STRING,
                roiRate = StringUtils.EMPTY_STRING,
                overallScore = StringUtils.EMPTY_STRING,
                description = StringUtils.EMPTY_STRING,
                icoStartDate = StringUtils.EMPTY_STRING,
                ticker = StringUtils.EMPTY_STRING,
                tokenType = StringUtils.EMPTY_STRING,
                icoTokenPrice = StringUtils.EMPTY_STRING,
                fundraisingGoal = StringUtils.EMPTY_STRING,
                soldOnPresale = StringUtils.EMPTY_STRING,
                totalTokens = StringUtils.EMPTY_STRING,
                availableForTokenSale = StringUtils.EMPTY_STRING,
                whitelist = StringUtils.EMPTY_STRING,
                bonusForTheFirst = StringUtils.EMPTY_STRING,
                minMaxPersonalCap = StringUtils.EMPTY_STRING,
                tokenIssue = StringUtils.EMPTY_STRING;

        if (sourceType == SourceType.ICODrop) {
            coinName = document.select(".ico-desk .ico-main-info h3").text();
            hypeRate = document.select(".rating-field .rating-items .rating-item:nth-child(1) p.rate").text();
            riskRate = document.select(".rating-field .rating-items .rating-item:nth-child(2) p.rate").text();
            roiRate = document.select(".rating-field .rating-items .rating-item:nth-child(3) p.rate").text();
            overallScore = document.select(".rating-result .rating-box p.ico-rate").text();
            description = document.select(".ico-description").text();
            icoStartDate = document.select(".sale-date").text();

            // Create a map for each row value
            Map<String, String> tokenSaleInformation = new LinkedHashMap<>();
            Elements tokenSaleDetails = document.select(".white-desk.ico-desk .row.list li");
            for (Element detail : tokenSaleDetails) {
                String detailText = detail.text();
                String delimiter = ": ";

                if (detailText.contains(delimiter)) {
                    String key = detailText.substring(0, detailText.indexOf(delimiter));
                    String value = detailText.substring(detailText.indexOf(delimiter) + delimiter.length(), detailText.length());

                    tokenSaleInformation.put(key, value);
                }
            }

            ticker = StringUtils.extractValueFromMap("Ticker", tokenSaleInformation);
            tokenType = StringUtils.extractValueFromMap("Token type", tokenSaleInformation);
            icoTokenPrice = StringUtils.extractValueFromMap("ICO Token Price", tokenSaleInformation);
            fundraisingGoal = StringUtils.extractValueFromMap("Fundraising Goal", tokenSaleInformation);
            soldOnPresale = StringUtils.extractValueFromMap("Sold on pre-sale", tokenSaleInformation);
            totalTokens = StringUtils.extractValueFromMap("Total Tokens", tokenSaleInformation);
            availableForTokenSale = StringUtils.extractValueFromMap("Available for Token Sale", tokenSaleInformation);
            whitelist = StringUtils.extractValueFromMap("Whitelist", tokenSaleInformation);
            bonusForTheFirst = StringUtils.extractValueFromMap("Bonus for the First", tokenSaleInformation);
            minMaxPersonalCap = StringUtils.extractValueFromMap("Min/Max Personal Cap", tokenSaleInformation);
            tokenIssue = StringUtils.extractValueFromMap("Token Issue", tokenSaleInformation);
        }
        else if (sourceType == SourceType.ICOBench) {
            coinName = document.select(".name h1").text();
            hypeRate = StringUtils.EMPTY_STRING;
            riskRate = StringUtils.EMPTY_STRING;
            roiRate = StringUtils.EMPTY_STRING;
            overallScore = document.select(".fixed_data .rate").text() + "/5";
            description = document.select(".ico_information p").text();
            icoStartDate = document.select(".financial_data .col_2 small").text();

            ticker = document.select(".financial_data div:eq(2) b").text();
            tokenType = document.select("#financial .box_left > div:contains(Type) .value").text();
            icoTokenPrice = document.select("#financial .box_left > div:contains(Price in ICO) .value").text();
            fundraisingGoal = StringUtils.EMPTY_STRING;
            soldOnPresale = StringUtils.EMPTY_STRING;
            totalTokens = StringUtils.EMPTY_STRING;
            availableForTokenSale = StringUtils.EMPTY_STRING;
            whitelist = StringUtils.EMPTY_STRING;
            bonusForTheFirst = StringUtils.EMPTY_STRING;
            minMaxPersonalCap = StringUtils.EMPTY_STRING;
            tokenIssue = StringUtils.EMPTY_STRING;
        }


        this.token = coinName;
        this.ticker = ticker;
        this.type = StringUtils.EMPTY_STRING; // will be manually updated
        this.totalTokens = totalTokens;
        this.fundraisingGoal = fundraisingGoal;
        this.availableForTokenSale = availableForTokenSale;
        this.icoTokenPrice = icoTokenPrice;
        this.projectSummary = description;
        this.team = StringUtils.EMPTY_STRING; // will be manually updated
        this.advisors = StringUtils.EMPTY_STRING; // will be manually updated
        this.bonusForTheFirst = bonusForTheFirst;
        this.presaleInformation = StringUtils.EMPTY_STRING; // will be manually updated
        this.icoStartDate = icoStartDate;
        this.tokenIssue = tokenIssue;
        this.tokenType = tokenType;
        this.soldOnPresale = soldOnPresale;
        this.whitelist = whitelist;
        this.minMaxPersonalCap = minMaxPersonalCap;
        this.hypeRate = hypeRate;
        this.riskRate = riskRate;
        this.roiRate = roiRate;
        this.overallScore = overallScore;
        this.url = url;
        this.registrationStatus = StringUtils.EMPTY_STRING;
        this.registeredAs = StringUtils.EMPTY_STRING;
        this.whitelistApproved = StringUtils.EMPTY_STRING;
        this.kycApproved = StringUtils.EMPTY_STRING;
        this.purchased = StringUtils.EMPTY_STRING;
    }

    /**
     * Retrieve values dynamically in case the order of the columns change
     * Use Java reflection to get the list of names
     * Exclude static fields to ignore variables like Logger
     * @param rowEntry
     * @param columnIndexMap
     */
    public Entry(List<Object> rowEntry, Map<String, Integer> columnIndexMap) {
        Integer nameIndex = columnIndexMap.get("Token").intValue();
        logger.info("{} - creating entity", rowEntry.get(nameIndex).toString());

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                continue;
            }

            String fieldName = field.getName();

            List<Map.Entry<String, Integer>> matchedFields =
                    columnIndexMap.entrySet()
                            .stream()
                            .filter(c -> StringUtils.areStringsEqualIgnoreCase(
                                    StringUtils.sanitizeAlphanumericStringValue(c.getKey()), fieldName))
                            .collect(Collectors.toList());

            if (matchedFields.size() != 1) {
                logger.error("No valid header found for {}", fieldName);
                continue;
            }

            Map.Entry<String, Integer> matchedField = matchedFields.get(0);

            try {
                Integer indexOfValue = matchedField.getValue();
                Class<?> dynamicClassType = Class.forName(field.getType().getName());

                // Parse string for Integer equivalent, account for nullable fields
                if (field.getType().isAssignableFrom(Integer.class)) {
                    String stringValue = rowEntry.get(indexOfValue).toString();
                    Integer value = Strings.isNullOrEmpty(stringValue)
                            ? null
                            : Integer.parseInt(stringValue);

                    field.set(this, value);
                }
                // Parse string for Double equivalent, account for nullable fields
                else if (field.getType().isAssignableFrom(Double.class)) {
                    String doubleValue = rowEntry.get(indexOfValue).toString();
                    Double value = Strings.isNullOrEmpty(doubleValue)
                            ? null
                            : Double.parseDouble(doubleValue);

                    field.set(this, value);
                }
                else {
                    field.set(this, dynamicClassType.cast(rowEntry.get(indexOfValue)));
                }
            } catch (IllegalAccessException ex) {
                logger.error("Unable to access {} modifier", fieldName);
            } catch (ClassNotFoundException ex) {
                logger.error("Unable to infer class type for {}", fieldName);
            }
        }
    }

    /********************
     * Getters and setters
     *******************/

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(String totalTokens) {
        this.totalTokens = totalTokens;
    }

    public String getFundraisingGoal() {
        return fundraisingGoal;
    }

    public void setFundraisingGoal(String fundraisingGoal) {
        this.fundraisingGoal = fundraisingGoal;
    }

    public String getAvailableForTokenSale() {
        return availableForTokenSale;
    }

    public void setAvailableForTokenSale(String availableForTokenSale) {
        this.availableForTokenSale = availableForTokenSale;
    }

    public String getIcoTokenPrice() {
        return icoTokenPrice;
    }

    public void setIcoTokenPrice(String icoTokenPrice) {
        this.icoTokenPrice = icoTokenPrice;
    }

    public String getProjectSummary() {
        return projectSummary;
    }

    public void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getAdvisors() {
        return advisors;
    }

    public void setAdvisors(String advisors) {
        this.advisors = advisors;
    }

    public String getBonusForTheFirst() {
        return bonusForTheFirst;
    }

    public void setBonusForTheFirst(String bonusForTheFirst) {
        this.bonusForTheFirst = bonusForTheFirst;
    }

    public String getPresaleInformation() {
        return presaleInformation;
    }

    public void setPresaleInformation(String presaleInformation) {
        this.presaleInformation = presaleInformation;
    }

    public String getIcoStartDate() {
        return icoStartDate;
    }

    public void setIcoStartDate(String icoStartDate) {
        this.icoStartDate = icoStartDate;
    }

    public String getTokenIssue() {
        return tokenIssue;
    }

    public void setTokenIssue(String tokenIssue) {
        this.tokenIssue = tokenIssue;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getSoldOnPresale() {
        return soldOnPresale;
    }

    public void setSoldOnPresale(String soldOnPresale) {
        this.soldOnPresale = soldOnPresale;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    public String getMinMaxPersonalCap() {
        return minMaxPersonalCap;
    }

    public void setMinMaxPersonalCap(String minMaxPersonalCap) {
        this.minMaxPersonalCap = minMaxPersonalCap;
    }

    public String getHypeRate() {
        return hypeRate;
    }

    public void setHypeRate(String hypeRate) {
        this.hypeRate = hypeRate;
    }

    public String getRiskRate() {
        return riskRate;
    }

    public void setRiskRate(String riskRate) {
        this.riskRate = riskRate;
    }

    public String getRoiRate() {
        return roiRate;
    }

    public void setRoiRate(String roiRate) {
        this.roiRate = roiRate;
    }

    public String getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(String overallScore) {
        this.overallScore = overallScore;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRegisteredAs() {
        return registeredAs;
    }

    public void setRegisteredAs(String registeredAs) {
        this.registeredAs = registeredAs;
    }

    public String getWhitelistApproved() {
        return whitelistApproved;
    }

    public void setWhitelistApproved(String whitelistApproved) {
        this.whitelistApproved = whitelistApproved;
    }

    public String getKycApproved() {
        return kycApproved;
    }

    public void setKycApproved(String kycApproved) {
        this.kycApproved = kycApproved;
    }

    public String getPurchased() {
        return purchased;
    }

    public void setPurchased(String purchased) {
        this.purchased = purchased;
    }

    /**
     * Assign the values that are user-populated, used when we're overwriting details on the spreadsheet
     * @param source
     */
    public void assignDefaultEmptyFields(Entry source) {
        this.type = source.getType();
        this.team = source.getTeam();
        this.advisors = source.getAdvisors();
        this.presaleInformation = source.getPresaleInformation();
        this.registrationStatus = source.getRegisteredAs();
        this.registeredAs = source.getRegisteredAs();
        this.whitelistApproved = source.getWhitelistApproved();
        this.kycApproved = source.getKycApproved();
        this.purchased = source.getPurchased();
    }


    /**
     * Merge the entity with an existing source.
     * If the current entity already has a value in the field, prefer to use that.
     * If the current entity doesn't have a value but the source does, use the source's value.
     * @param source
     */
    public void mergeEntry(Entry source) {
        this.token = !Strings.isNullOrEmpty(source.getToken()) && Strings.isNullOrEmpty(this.token)
                ? source.getToken() : this.token;
        this.ticker = !Strings.isNullOrEmpty(source.getTicker()) && Strings.isNullOrEmpty(this.ticker)
                ? source.getTicker() : this.ticker;
        this.totalTokens = !Strings.isNullOrEmpty(source.getTotalTokens()) && Strings.isNullOrEmpty(this.totalTokens)
                ? source.getTotalTokens() : this.totalTokens;
        this.fundraisingGoal = !Strings.isNullOrEmpty(source.getFundraisingGoal()) && Strings.isNullOrEmpty(this.fundraisingGoal)
                ? source.getFundraisingGoal() : this.fundraisingGoal;
        this.availableForTokenSale = !Strings.isNullOrEmpty(source.getAvailableForTokenSale()) && Strings.isNullOrEmpty(this.availableForTokenSale)
                ? source.getAvailableForTokenSale() : this.availableForTokenSale;
        this.icoTokenPrice = !Strings.isNullOrEmpty(source.getIcoTokenPrice()) && Strings.isNullOrEmpty(this.icoTokenPrice)
                ? source.getIcoTokenPrice() : this.icoTokenPrice;
        this.projectSummary = !Strings.isNullOrEmpty(source.getProjectSummary()) && Strings.isNullOrEmpty(this.projectSummary)
                ? source.getProjectSummary() : this.projectSummary;
        this.bonusForTheFirst = !Strings.isNullOrEmpty(source.getBonusForTheFirst()) && Strings.isNullOrEmpty(this.bonusForTheFirst)
                ? source.getBonusForTheFirst() : this.bonusForTheFirst;
        this.icoStartDate = !Strings.isNullOrEmpty(source.getIcoStartDate()) && Strings.isNullOrEmpty(this.icoStartDate)
                ? source.getIcoStartDate() : this.icoStartDate;
        this.tokenIssue = !Strings.isNullOrEmpty(source.getTokenIssue()) && Strings.isNullOrEmpty(this.tokenIssue)
                ? source.getTokenIssue() : this.tokenIssue;
        this.tokenType = !Strings.isNullOrEmpty(source.getTokenType()) && Strings.isNullOrEmpty(this.tokenType)
                ? source.getTokenType() : this.tokenType;
        this.soldOnPresale = !Strings.isNullOrEmpty(source.getSoldOnPresale()) && Strings.isNullOrEmpty(this.soldOnPresale)
                ? source.getSoldOnPresale() : this.soldOnPresale;
        this.whitelist = !Strings.isNullOrEmpty(source.getWhitelist()) && Strings.isNullOrEmpty(this.whitelist)
                ? source.getWhitelist() : this.whitelist;
        this.minMaxPersonalCap = !Strings.isNullOrEmpty(source.getMinMaxPersonalCap()) && Strings.isNullOrEmpty(this.minMaxPersonalCap)
                ? source.getMinMaxPersonalCap() : this.minMaxPersonalCap;
        this.hypeRate = !Strings.isNullOrEmpty(source.getHypeRate()) && Strings.isNullOrEmpty(this.hypeRate)
                ? source.getHypeRate() : this.hypeRate;
        this.riskRate = !Strings.isNullOrEmpty(source.getRiskRate()) && Strings.isNullOrEmpty(this.riskRate)
                ? source.getRiskRate() : this.riskRate;
        this.roiRate = !Strings.isNullOrEmpty(source.getRoiRate()) && Strings.isNullOrEmpty(this.roiRate)
                ? source.getRoiRate() : this.roiRate;
        this.overallScore = !Strings.isNullOrEmpty(source.getOverallScore()) && Strings.isNullOrEmpty(this.overallScore)
                ? source.getOverallScore() : this.overallScore;
        this.url = !Strings.isNullOrEmpty(source.getUrl()) && Strings.isNullOrEmpty(this.url)
                ? source.getUrl() : this.url;


        assignDefaultEmptyFields(source);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token: ").append(this.token).append("\n")
                .append("Ticker: ").append(this.ticker).append("\n")
                .append("Type: ").append(this.type).append("\n")
                .append("Total tokens: ").append(this.totalTokens).append("\n")
                .append("Fundraising goal: ").append(this.fundraisingGoal).append("\n")
                .append("Available for token sale: ").append(this.availableForTokenSale).append("\n")
                .append("ICO token price: ").append(this.icoTokenPrice).append("\n")
                .append("Project summary: ").append(this.projectSummary).append("\n")
                .append("Team: ").append(this.team).append("\n")
                .append("Advisors: ").append(this.advisors).append("\n")
                .append("Bonus for the first: ").append(this.bonusForTheFirst).append("\n")
                .append("Presale information: ").append(this.presaleInformation).append("\n")
                .append("ICO start date: ").append(this.icoStartDate).append("\n")
                .append("Token issuance: ").append(this.tokenIssue).append("\n")
                .append("Token type: ").append(this.tokenType).append("\n")
                .append("Sold on presale: ").append(this.soldOnPresale).append("\n")
                .append("Whitelist: ").append(this.whitelist).append("\n")
                .append("Min/Max personal cap: ").append(this.minMaxPersonalCap).append("\n")
                .append("Hype rate: ").append(this.hypeRate).append("\n")
                .append("Risk rate: ").append(this.riskRate).append("\n")
                .append("ROI rate: ").append(this.roiRate).append("\n")
                .append("Overall score: ").append(this.overallScore).append("\n")
                .append("ICODrops url: ").append(this.url).append("\n")
                .append("Registration status: ").append(this.registrationStatus).append("\n")
                .append("Registered as: ").append(this.registeredAs).append("\n")
                .append("Whitelist approved: ").append(this.whitelistApproved).append("\n")
                .append("KYC approved: ").append(this.kycApproved).append("\n")
                .append("Purchased: ").append(this.purchased).append("\n");

        return sb.toString();
    }

    /**
     * Intentionally omitted the columns that are user-inputted
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        Entry comp = (Entry) obj;

        return this.token.equals(comp.getToken()) &&
                this.ticker.equals(comp.getTicker()) &&
                this.totalTokens.equals(comp.getTotalTokens()) &&
                this.fundraisingGoal.equals(comp.getFundraisingGoal()) &&
                this.availableForTokenSale.equals(comp.getAvailableForTokenSale()) &&
                this.icoTokenPrice.equals(comp.getIcoTokenPrice()) &&
                this.projectSummary.equals(comp.getProjectSummary()) &&
                this.bonusForTheFirst.equals(comp.getBonusForTheFirst()) &&
                this.icoStartDate.equals(comp.getIcoStartDate()) &&
                this.tokenIssue.equals(comp.getTokenIssue()) &&
                this.tokenType.equals(comp.getTokenType()) &&
                this.soldOnPresale.equals(comp.getSoldOnPresale()) &&
                this.whitelist.equals(comp.getWhitelist()) &&
                this.minMaxPersonalCap.equals(comp.getMinMaxPersonalCap()) &&
                this.riskRate.equals(comp.getRiskRate()) &&
                this.roiRate.equals(comp.getRoiRate()) &&
                this.overallScore.equals(comp.getOverallScore()) &&
                this.url.equals(comp.getUrl());
    }
}
