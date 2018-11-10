package com.angusbarnes.bills;

import com.angusbarnes.bills.entity.Carer;
import com.angusbarnes.bills.entity.ScheduleCarer;
import com.angusbarnes.bills.entity.ScheduleEvent;
import com.angusbarnes.bills.entity.VisitUpdate;
import org.simplejavamail.email.Email;

import java.util.Date;
import java.util.List;

public class NotificationSender {

    // Username and Password - I won't hardcode my details here!
    private EmailSender es = new EmailSender("", "", "Julia's House");


    private boolean hasLeft(ScheduleCarer session) {
        for (VisitUpdate update : session.getVisitUpdates()) {
            if (update.getAction().equals("left")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasArrived(ScheduleCarer session) {
        for (VisitUpdate update : session.getVisitUpdates()) {
            if (update.getAction().equals("arrived")) {
                return true;
            }
        }
        return false;
    }

    private boolean isHome(ScheduleCarer session) {
        for (VisitUpdate update : session.getVisitUpdates()) {
            if (update.getAction().equals("home")) {
                return true;
            }
        }
        return false;
    }

    private boolean isScheduleEventEnded(ScheduleCarer session) {
        Date now = new Date();
        return session.getScheduleEvent().getEnd().after(now);
    }

    private boolean minutesPassedAfterEventEnded(ScheduleEvent se) {
        Date now = new Date();
        int x = 30;
        Date endMinutesLater = new Date(se.getEnd().getTime() + x * 60 * 1000);
        return now.after(endMinutesLater);
    }

    private boolean minutesPassedAfterLeavingPosted(ScheduleCarer session) {
        if (hasLeft(session)) {
            Date now = new Date();
            int x = 120;

            Date leaveTime = new Date();
            for (VisitUpdate update : session.getVisitUpdates()) {
                if (update.getAction().equals("left")) {
                    leaveTime = update.getTime();
                }
            }


            Date leftMinutesLater = new Date(leaveTime.getTime() + x * 60 * 1000);
            return now.after(leftMinutesLater);
        } else {
            return false;
        }
    }

    public void sendNotifications(List<Carer> allCarers) {
        for (Carer carer : allCarers) {
            for (ScheduleCarer session : carer.getScheduleCarers()) {

                if (hasLeft(session) && !isHome(session) && minutesPassedAfterLeavingPosted(session)) {
                    // Case 1: Left the session with a family, and hasn't arrived
                    // home after 2 hours of saying they left.
                    es.sendEmail("", "Alert: Carer hasn't notified they have arrived home",
                            "Carer named " + carer.getFirstName() + carer.getLastName() + " has left 2 hours ago but hasn't arrived home yet. " +
                                    "You can reach them at this number: " + carer.getPhone());

                } else if (isScheduleEventEnded(session) && !hasLeft(session)
                        && minutesPassedAfterEventEnded(session.getScheduleEvent())) {
                    // Case 2: The session with family has ended, and after 30
                    // minutes the carer hasn't said "I've left" yet.

                    es.sendEmail("", "Alert: Carer hasn't notified they have left a session",
                            "Carer named " + carer.getFirstName() + carer.getLastName() +
                                    " hasn't notified us that they have left a session which ended 30 minutes ago. " +
                                    "You can reach them at this number: " + carer.getPhone());
                }
            }
        }
    }
}
