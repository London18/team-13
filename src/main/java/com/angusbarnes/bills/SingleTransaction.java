package com.angusbarnes.bills;

import com.angusbarnes.bills.service.DateService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.util.Date;

public class SingleTransaction {
    private final Date date;
    private final double amount;
    private final String category;
    
    public SingleTransaction (String unparsedDate,
                              double amount,
                              String category) {
        this.date = DateService.parseDate(unparsedDate).orElseThrow(() ->
                new IllegalArgumentException("Invalid date format: "
                        + unparsedDate));
        this.amount = amount;
        this.category = category;
    }
    
    public static SingleTransaction buildFromJson (String json) {
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        String date = data.get("date").getAsString();
        double amount = data.get("amount").getAsDouble();
        String category = data.get("category").getAsString();
        
        return new SingleTransaction(date, amount, category);
    }
    
    public Date getDate () {
        return date;
    }
    
    public double getAmount () {
        return amount;
    }
    
    public String getCategory () {
        return category;
    }
    
    private String getJson () {
        JSONObject self = new JSONObject();
        self.put("date", DateService.formatDate(date));
        self.put("amount", amount);
        self.put("category", category);
        return self.toString();
    }
}
