package com.angusbarnes.bills;

import com.angusbarnes.bills.entity.Carer;
import com.angusbarnes.bills.entity.ScheduleCarer;
import com.angusbarnes.bills.entity.VisitUpdate;

import java.util.Date;
import java.util.List;

public class NotificationSender {

    private boolean hasLeft(ScheduleCarer session) {
        for (VisitUpdate update : session.getVisitUpdates()) {
            if (update.getAction().equals("left")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasArrived(ScheduleCarer session) {
        for (VisitUpdate update : session.getVisitUpdates()) {
            if (update.getAction().equals("arrived")) {
                return true;
            }
        }
        return false;
    }

    private boolean isScheduleEventEnded(ScheduleCarer session) {
        Date now = new Date();

        if (session.getScheduleEvent().getEnd().after(now....));
    }

    private boolean isHome(ScheduleCarer session) {
        for (VisitUpdate update : session.getVisitUpdates()) {
            if (update.getAction().equals("home")) {
                return true;
            }
        }
        return false;
    }

    private boolean xHoursPassedAfterLeaving(float x) {

    }

    private boolean xHoursPassedAfterScheduleEventEnded(float x) {
    }

    // Carer: (CarerID), Name, Address, Email, Phone
    // ScheduleEvent: (ScheduleID), Date, Start, End, FID
    // VisitUpdate: (VisitID), ScheduleCarerID action, time, comment
    // ScheduleCarer: (ScheduleCarerID), ScheduleID, CarerID
    public void sendNotifications(List<Carer> allCarers) {
        for (Carer carer : allCarers) {
            for (ScheduleCarer session : carer.getScheduleCarers()) {
                if (hasLeft(session) && !isHome(session) && xHoursPassedAfterLeaving(2.1f)) {

                } else if (isScheduleEventEnded() && !hasLeft(session) && xHoursPassedAfterScheduleEventEnded()) {

                } else {
                    // We're fine!
                }
            }
        }

        // Need these info:
        // Carer's name
        // Carer's phone (to call)
        // All Visits (in the last 3 days just in case):
        //  - Actions (action and action time)
        //  - Associated Schedule event's end time
    }

}
