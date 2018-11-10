package com.angusbarnes.bills;

import com.angusbarnes.bills.entity.Carer;
import com.angusbarnes.bills.entity.Family;
import com.angusbarnes.bills.entity.ScheduleCarer;
import com.angusbarnes.bills.entity.ScheduleEvent;
import com.angusbarnes.bills.repository.CarerRepository;
import com.angusbarnes.bills.repository.FamilyRepository;
import com.angusbarnes.bills.repository.ScheduleCarerRepository;
import com.angusbarnes.bills.repository.ScheduleEventRepository;

import java.sql.Date;

public class ScheduleUpdater {
    // FamilyID, start, end, payrollID1, payrollID2, payrollID3

    public void parseScheduleCSV(String csv,
                         ScheduleEventRepository ser,
                         ScheduleCarerRepository scr,
                         CarerRepository cr,
                         FamilyRepository fr) throws IllegalArgumentException {
        String[] split = csv.split(",");

        // 1. Check whether it is in the right format
        // 2. put it into classes
        // 3. Send it to the database
        //      3.1. Add an entry into ScheduleEvent (straightforward)
        //      3.2. Add entries to ScheduleCarer for each payrollID

        Carer Carer1 = cr.findById(Long.valueOf(split[3])).get();
        Carer Carer2 = cr.findById(Long.valueOf(split[4])).get();
        Carer Carer3 = cr.findById(Long.valueOf(split[5])).get();

        Family family = fr.findById(Long.valueOf(split[0])).get();

        if (split.length % 3 != 0) {
            throw new IllegalArgumentException("The CSV text " +
                    "input is not in the right format: Incorrect " +
                    "number of fields");
        }

        ScheduleEvent se = new ScheduleEvent(Date.valueOf(split[1]),
                Date.valueOf(split[2]), family);
        ScheduleCarer sc1 = new ScheduleCarer(se, Carer1);
        ScheduleCarer sc2 = new ScheduleCarer(se, Carer2);
        ScheduleCarer sc3 = new ScheduleCarer(se, Carer3);

        ser.save(se);
        scr.save(sc1);
        scr.save(sc2);
        scr.save(sc3);
    }
}
