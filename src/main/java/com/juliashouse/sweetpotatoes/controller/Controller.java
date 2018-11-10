package com.juliashouse.sweetpotatoes.controller;

import com.juliashouse.sweetpotatoes.CarerAlertSender;
import com.juliashouse.sweetpotatoes.SecurityConstants;
import com.juliashouse.sweetpotatoes.entity.CredentialSet;
import com.juliashouse.sweetpotatoes.entity.ScheduleCarer;
import com.juliashouse.sweetpotatoes.entity.ScheduleEvent;
import com.juliashouse.sweetpotatoes.entity.SessionInstance;
import com.juliashouse.sweetpotatoes.entity.User;
import com.juliashouse.sweetpotatoes.entity.VisitUpdate;
import com.juliashouse.sweetpotatoes.repository.CarerRepository;
import com.juliashouse.sweetpotatoes.repository.CredentialSetRepository;
import com.juliashouse.sweetpotatoes.repository.ScheduleCarerRepository;
import com.juliashouse.sweetpotatoes.repository.SessionInstanceRepository;
import com.juliashouse.sweetpotatoes.repository.UserRepository;
import com.juliashouse.sweetpotatoes.repository.VisitUpdateRepository;
import com.juliashouse.sweetpotatoes.service.DateService;
import com.juliashouse.sweetpotatoes.service.SecurityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class Controller {

    @SuppressWarnings("unused")
    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("unused")
    @Autowired
    private CredentialSetRepository credentialSetRepository;

    @SuppressWarnings("unused")
    @Autowired
    private SessionInstanceRepository sessionInstanceRepository;

    @SuppressWarnings("unused")
    @Autowired
    private ScheduleCarerRepository scheduleCarerRepository;

    @SuppressWarnings("unused")
    @Autowired
    private VisitUpdateRepository visitUpdateRepository;

    @SuppressWarnings("unused")
    @Autowired
    private CarerRepository carerRepository;

    private CarerAlertSender carerAlertSender = new CarerAlertSender();

    @Scheduled(fixedRate = 1000)
    public void checkUnconfirmed() {
        System.out.println("Does it work");
        carerAlertSender.checkForCarerAlerts(carerRepository.findAll());
    }

    @PostMapping("/index")
    public String attemptLogin(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String passwordAttempt,
            @CookieValue(value = "sessionKey",
                    defaultValue = "") String sessionKey,
            HttpServletResponse response) {

        JSONObject jsonObj = new JSONObject();

        List<User> matchingUsers = userRepository.findByUsername(username);
        System.out.println(matchingUsers);

        // Find already existing sessions
        Optional<User> loggedIn = findUserFromSessionKey(sessionKey);
        if (loggedIn.isPresent()) {
            String newKey = assignNewSession(
                    loggedIn.get(),
                    response).getSessionKey();
            return "Already logged in as " + loggedIn.get().toString()
                    + " with session " + newKey;
        }

        if (matchingUsers.size() == 0) {
            return "wrong username";
        } else if (matchingUsers.size() > 1) {
            return "ambiguous username";
        } else {
            User matchingUser = matchingUsers.get(0);
            CredentialSet matchingCredentialSet
                    = matchingUser.getCredentialSet();

            byte[] trueHash = matchingCredentialSet.getPasswordHash();
            byte[] salt = matchingCredentialSet.getPasswordSalt();
            int timeCost = matchingCredentialSet.getTimeCost();
            int memoryCost = matchingCredentialSet.getMemoryCost();
            int parallelism = matchingCredentialSet.getParallelism();

            boolean loginAttempt = SecurityService.verifyPassword(
                    passwordAttempt.getBytes(),
                    salt,
                    trueHash,
                    timeCost,
                    memoryCost,
                    parallelism);

            if (loginAttempt) {
                ensureCorrectHashParameters(matchingUser, passwordAttempt);
                assignNewSession(matchingUser, response);
                jsonObj.put("result", true);
            } else {
                System.out.println("Incorrect password: " + passwordAttempt);
                jsonObj.put("result", false);
            }
        }
        return jsonObj.toString();
    }

    /**
     * Checks that the user's password is hashed the correct number of times,
     * and if not, updates it.
     *
     * @param user            User to check
     * @param correctPassword If the hash iterations is incorrect, this is
     *                        hashed the correct number of times.
     */
    private void ensureCorrectHashParameters(
            User user,
            String correctPassword) {
        CredentialSet userCredentialSet = user.getCredentialSet();
        boolean timeCostMatch = userCredentialSet.getTimeCost()
                == SecurityConstants.HASH_TIME_COST;
        boolean memoryCostMatch = userCredentialSet.getMemoryCost()
                == SecurityConstants.HASH_MEMORY_COST;
        boolean parallelismMatch = userCredentialSet.getParallelism()
                == SecurityConstants.HASH_PARALLELISM;
        if (!timeCostMatch || !memoryCostMatch || !parallelismMatch) {
            changePassword(user, correctPassword);
        }
    }

    /**
     * Updates a user's password and hash iterations in the database.
     *
     * @param user        User to update
     * @param newPassword New password to set
     */
    private void changePassword(User user, String newPassword) {
        // TODO - update saved transaction groups' encryption
        byte[] newSalt = SecurityService.getSalt();
        byte[] newHash = SecurityService.hashPassword(
                newPassword.getBytes(),
                newSalt,
                SecurityConstants.HASH_TIME_COST,
                SecurityConstants.HASH_MEMORY_COST,
                SecurityConstants.HASH_PARALLELISM);

        CredentialSet userCredentialSet = user.getCredentialSet();
        userCredentialSet.setPasswordHash(newHash);
        userCredentialSet.setPasswordSalt(newSalt);
        userCredentialSet.setTimeCost(SecurityConstants.HASH_TIME_COST);
        userCredentialSet.setMemoryCost(SecurityConstants.HASH_MEMORY_COST);
        userCredentialSet.setParallelism(SecurityConstants.HASH_PARALLELISM);

        credentialSetRepository.save(userCredentialSet);
    }

    /**
     * Attempts to find a single user by a session key
     *
     * @param sessionKey The session key
     * @return An Optional filled with a User if present, or empty Optional
     * otherwise
     */
    private Optional<User> findUserFromSessionKey(String sessionKey) {
        List<SessionInstance> matchingSessions = sessionInstanceRepository
                .findBySessionKeyAndExpiryDateGreaterThan(
                        sessionKey,
                        DateService.getDate());
        if (matchingSessions.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(matchingSessions.get(0).getUser());
    }

    /**
     * Assigns a new session to a user, and returns its key as a cookie.
     *
     * @param sessionOwner The user to generate a session for
     * @param response     The HTTP server response to use to return the session
     *                     key as a cookie
     * @return The newly created SessionInstance object
     */
    private SessionInstance assignNewSession(
            User sessionOwner,
            HttpServletResponse response) {
        SessionInstance newSessionInstance =
                getNewSessionKey(
                        sessionOwner,
                        SecurityConstants.SESSION_DURATION);
        commitSessionInstance(newSessionInstance, response);
        return newSessionInstance;
    }

    /**
     * Generate a new session key with given duration in seconds for a given
     * user. Data is returned in database record format
     *
     * @param sessionOwner The user the session is for
     * @param duration     How long until the session key expires, in seconds
     * @return A SessionInstance entity representing an entry in the
     * SessionInstance table
     */
    private SessionInstance getNewSessionKey(User sessionOwner, int duration) {
        String key = SecurityService.generateSessionKey(
                SecurityConstants.SESSION_KEY_LENGTH);
        Date expiryDate = DateService.getFutureDate(duration);
        return new SessionInstance(sessionOwner, key, expiryDate);
    }

    /**
     * Saves a given instance of a session to the database, and returns its key
     * as a token in a cookie.
     *
     * @param sessionInstance The SessionInstance record to save to the
     *                        database
     * @param response        The HTTP server response to use to return the
     *                        session key as a cookie
     */
    private void commitSessionInstance(
            SessionInstance sessionInstance,
            HttpServletResponse response) {
        sessionInstanceRepository.save(sessionInstance);
        response.addCookie(
                new Cookie("sessionKey", sessionInstance.getSessionKey()));
    }

    /**
     * Sets the expiry date for all unexpired sessions of a user to the current
     * time. This effectively signs them out of all locations.
     *
     * @param user User to force sign out
     */
    private void invalidateSessions(User user) {
        sessionInstanceRepository
                .findByUserAndExpiryDateGreaterThan(
                        user,
                        DateService.getDate())
                .forEach(s -> {
                    s.setExpiryDate(DateService.getDate());
                    sessionInstanceRepository.save(s);
                });
    }

    // need to get schedule IDS of user
        /*
        schedule: [
            {sid: bjsjnck,
             start: date,
             end: date,
             fname: sdlkaf,
             status: arrived, left, home (most recent status)
             }
         ]
    */
    @GetMapping("home")
    public String getSchedule(
            @RequestParam String date,
            @CookieValue(value = "sessionKey",
                    defaultValue = "") String sessionKey) {

        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Optional<User> user = findUserFromSessionKey(sessionKey);
        if (user.isPresent()) {

            List<ScheduleEvent> scheduleEvents = user.get().getCarer()
                    .getScheduleCarers()
                    .stream().map
                            (ScheduleCarer::getScheduleEvent)
                    .collect(Collectors.toList());

            for (ScheduleEvent scheduleEvent : scheduleEvents) {
                Long sid = scheduleEvent.getId();
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String start = formatter.format(scheduleEvent.getStart()
                        .toString());
                String end = formatter.format(scheduleEvent.getEnd()
                        .toString());
                String fname = scheduleEvent.getFamily().getName();

                //TODO: status query
                String status = "arrived";

                jsonObj.put("sid", sid);
                jsonObj.put("start", start);
                jsonObj.put("end", end);
                jsonObj.put("fname", fname);
                jsonObj.put("status", status);
            }

            jsonArray.put(jsonObj);
        } else {
            return "error";
        }

        JSONObject finalObj = new JSONObject();
        return finalObj.put("schedule", jsonArray).toString();
    }

    @PostMapping("postAction")
    public String postAction(
            @RequestParam String actionStr,
            @RequestParam String comment,
            @CookieValue(value = "sessionKey",
                    defaultValue = "") String sessionKey,
            @RequestParam int SCID) throws ParseException {

        ScheduleCarer scheduleCarer = scheduleCarerRepository
                .findById(SCID).orElseThrow(() -> new RuntimeException("SCID error"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(formatter.format(new Date()));

        VisitUpdate visitUpdate = new VisitUpdate(scheduleCarer, actionStr,
                date, comment);
        visitUpdateRepository.save(visitUpdate);
        return "";
    }

    @PostMapping("logout")
    public String logout(
            @CookieValue(value = "sessionKey",
                    defaultValue = "") String sessionKey) {
        Optional<User> user = findUserFromSessionKey(sessionKey);
        if (user.isPresent()) {
            invalidateSessions(user.get());
            return "logged out";
        } else {
            return "already not logged in";
        }
    }

    @PostMapping("loggedin")
    public String isLoggedIn(@CookieValue(value = "sessionKey",
            defaultValue = "") String sessionKey) {
        Optional<User> maybeUser = findUserFromSessionKey(sessionKey);
        if (maybeUser.isPresent()) {
            return maybeUser.get().getUsername();
        } else {
            return "";
        }
    }
}
