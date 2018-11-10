package com.juliashouse.sweetpotatoes;

import com.juliashouse.sweetpotatoes.entity.Carer;
import com.juliashouse.sweetpotatoes.entity.Family;
import com.juliashouse.sweetpotatoes.entity.ScheduleCarer;
import com.juliashouse.sweetpotatoes.entity.ScheduleEvent;
import com.juliashouse.sweetpotatoes.repository.CarerRepository;
import com.juliashouse.sweetpotatoes.repository.FamilyRepository;
import com.juliashouse.sweetpotatoes.repository.ScheduleCarerRepository;
import com.juliashouse.sweetpotatoes.repository.ScheduleEventRepository;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.util.Optional;

public class ScheduleUpdater {
    // FamilyID, start, end, payrollID1, payrollID2, payrollID3

    public void parseScheduleCSV(String csv,
                                 ScheduleEventRepository ser,
                                 ScheduleCarerRepository scr,
                                 CarerRepository cr,
                                 FamilyRepository fr) throws IllegalArgumentException {

        // 1. Check whether it is in the right format
        // 2. put it into classes
        // 3. Save it to the database
        //      3.1. Add an entry into ScheduleEvent (straightforward)
        //      3.2. Add entries to ScheduleCarer for each payrollID

        String[] split = csv.split(",");

        if (split.length % 3 != 0) {
            throw new IllegalArgumentException("The CSV text " +
                    "input is not in the right format: Incorrect " +
                    "number of fields");
        }

        int line = 0;
        int columnCount = 6;
        int readingColumn = 0;

        while (readingColumn < split.length) {
            for (int i = 0; i < columnCount; i++) {
                Optional<Carer> carer1Op = cr.findById(Long.valueOf(split[3]));
                Optional<Carer> carer2Op = cr.findById(Long.valueOf(split[4]));
                Optional<Carer> carer3Op = cr.findById(Long.valueOf(split[5]));
                Optional<Family> familyOp = fr.findById(Long.valueOf(split[0]));

                if (carer1Op.isPresent()) {
                    if (carer2Op.isPresent()) {
                        if (carer3Op.isPresent()) {
                            if (familyOp.isPresent()) {
                                Carer carer1 = carer1Op.get();
                                Carer carer2 = carer2Op.get();
                                Carer carer3 = carer3Op.get();
                                Family family = familyOp.get();

                                ScheduleEvent se = new ScheduleEvent(Date.valueOf(split[1]),
                                        Date.valueOf(split[2]), family);
                                ScheduleCarer sc1 = new ScheduleCarer(se, carer1);
                                ScheduleCarer sc2 = new ScheduleCarer(se, carer2);
                                ScheduleCarer sc3 = new ScheduleCarer(se, carer3);


                                ser.save(se);
                                scr.save(sc1);
                                scr.save(sc2);
                                scr.save(sc3);
                            } else {
                                throw new InvalidParameterException("The family id entered on line " + line + " was " +
                                        "not found in the database, please check your entry.");
                            }
                        } else {
                            throw new InvalidParameterException("The carer id for the third carer entered on line " + line + " was " +
                                    "not found in the database, please check your entry.");
                        }
                    } else {
                        throw new InvalidParameterException("The carer id for the second carer entered on line " + line + " was " +
                                "not found in the database, please check your entry.");
                    }
                } else {
                    throw new InvalidParameterException("The carer id for the first carer entered on line " + line + " was " +
                            "not found in the database, please check your entry.");
                }

                readingColumn++;
            }
            line++;
        }
    }
}
