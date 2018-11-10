package com.juliashouse.sweetpotatoes;

public class CsvParser {
    // FamilyID, start, end, payrollID1, payrollID2, payrollID3

    // Assume that this is what you received from the endpoint
    public void parseCSV(String csv) throws IllegalArgumentException {
        String[] split = csv.split(",");
        // TODO:
        // 1. Check whether it is in the right format
        // 2. put it into classes
        // 3. Send it to the database
        //      3.1. Add an entry into ScheduleEvent (straightforward)
        //      3.2. Add entries to ScheduleCarer for each payrollID

        if (split.length % 3 != 0) {
            throw new IllegalArgumentException("The CSV text " +
                    "input is not in the right format: Incorrect " +
                    "number of fields");
        }




    }
}
