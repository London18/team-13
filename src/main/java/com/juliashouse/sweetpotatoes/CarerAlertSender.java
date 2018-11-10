package com.juliashouse.sweetpotatoes;

import com.juliashouse.sweetpotatoes.entity.Carer;
import com.juliashouse.sweetpotatoes.entity.ScheduleCarer;
import com.juliashouse.sweetpotatoes.entity.ScheduleEvent;
import com.juliashouse.sweetpotatoes.entity.VisitUpdate;

import java.util.Date;
import java.util.List;

public class CarerAlertSender {
    // Send an alert after this many minutes have passed after the
    // carer has left and hasn't arrived home.
    private final int minutesAfterLeftNotHome;
    // Send an alert after this many minutes have passed after the
    // session has ended but the carer hasn't notified that they have left.
    private final int minutesAfterSessionEnd;

    public CarerAlertSender() {
        // Default values
        minutesAfterLeftNotHome = 120;
        minutesAfterSessionEnd = 30;
    }

    public CarerAlertSender(int minutesAfterLeftNotHome, int minutesAfterSessionEnd) {
        this.minutesAfterLeftNotHome = minutesAfterLeftNotHome;
        this.minutesAfterSessionEnd = minutesAfterSessionEnd;
    }

    // TODO: Username and Password - I won't hardcode my details here!
    private EmailSender es = new EmailSender(System.getenv("EMAIL_USERNAME"), System.getenv("EMAIL_PASSWORD"), "Julia's House");

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
        Date endMinutesLater = new Date(se.getEnd().getTime() + minutesAfterSessionEnd * 60 * 1000);
        return now.after(endMinutesLater);
    }

    private boolean minutesPassedAfterLeavingPosted(ScheduleCarer session) {
        if (hasLeft(session)) {
            Date now = new Date();

            Date leaveTime = new Date();
            for (VisitUpdate update : session.getVisitUpdates()) {
                if (update.getAction().equals("left")) {
                    leaveTime = update.getTime();
                }
            }


            Date leftMinutesLater = new Date(leaveTime.getTime() + minutesAfterLeftNotHome * 60 * 1000);
            return now.after(leftMinutesLater);
        } else {
            return false;
        }
    }

    public void checkForCarerAlerts(List<Carer> allCarers) {
        for (Carer carer : allCarers) {
            for (ScheduleCarer session : carer.getScheduleCarers()) {
                System.out.println(carer + ": " + session + ": " + isHome(session));
                if (hasLeft(session) && !isHome(session) && minutesPassedAfterLeavingPosted(session)) {
                    System.out.println("Not home!!!");
                    // Case 1: Left the session with a family, and hasn't arrived
                    // home after 2 hours of saying they left.
                    es.sendEmail("", "Alert: Carer hasn't notified they have arrived home",
                            "Carer named " + carer.getFirstName() + carer.getLastName() + " has left 2 hours ago but hasn't arrived home yet. " +
                                    "You can reach them at this number: " + carer.getPhone());

                } else if (isScheduleEventEnded(session) && !hasLeft(session)
                        && minutesPassedAfterEventEnded(session.getScheduleEvent())) {
                    System.out.println("Session ended without report!!!");
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
