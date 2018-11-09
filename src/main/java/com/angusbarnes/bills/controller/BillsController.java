package com.angusbarnes.bills.controller;

import com.angusbarnes.bills.SecurityConstants;
import com.angusbarnes.bills.entity.CredentialSet;
import com.angusbarnes.bills.entity.DataEncryptionKey;
import com.angusbarnes.bills.entity.KeyEncryptionKeyData;
import com.angusbarnes.bills.entity.SessionInstance;
import com.angusbarnes.bills.entity.TransactionGroup;
import com.angusbarnes.bills.entity.User;
import com.angusbarnes.bills.repository.CredentialSetRepository;
import com.angusbarnes.bills.repository.DataEncryptionKeyRepository;
import com.angusbarnes.bills.repository.KeyEncryptionKeyDataRepository;
import com.angusbarnes.bills.repository.SessionInstanceRepository;
import com.angusbarnes.bills.repository.TransactionGroupRepository;
import com.angusbarnes.bills.repository.UserRepository;
import com.angusbarnes.bills.service.DateService;
import com.angusbarnes.bills.service.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class BillsController {
    private static final Logger LOG =
            LogManager.getLogger(BillsController.class);
    @SuppressWarnings("unused")
    @Autowired
    private UserRepository userRepository;
    @SuppressWarnings("unused")
    @Autowired
    private TransactionGroupRepository transactionGroupRepository;
    @SuppressWarnings("unused")
    @Autowired
    private SessionInstanceRepository sessionInstanceRepository;
    @SuppressWarnings("unused")
    @Autowired
    private CredentialSetRepository credentialSetRepository;
    @SuppressWarnings("unused")
    @Autowired
    private DataEncryptionKeyRepository dataEncryptionKeyRepository;
    @SuppressWarnings("unused")
    @Autowired
    private KeyEncryptionKeyDataRepository keyEncryptionKeyDataRepository;
    
    private static RuntimeException logAndThrow (String m) {
        return getLogAndThrower(m).get();
    }
    
    private static Supplier<RuntimeException> getLogAndThrower (String m) {
        return () -> {
            LOG.error(m);
            return new RuntimeException(m);
        };
    }
    
    // TODO - obviously don't fucking leave this in if this goes live
    private static <E> String dumpRepository (JpaRepository<E, Long> repo) {
        return repo.findAll().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
    
    /**
     * Generate a new session key with given duration in seconds for a given
     * user. Data is returned in database record format
     *
     * @param sessionOwner The user the session is for
     * @param duration     How long until the session key expires, in seconds
     *
     * @return A SessionInstance entity representing an entry in the
     * SessionInstance table
     */
    private SessionInstance getNewSessionKey (User sessionOwner, int duration) {
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
    private void commitSessionInstance (
            SessionInstance sessionInstance,
            HttpServletResponse response) {
        sessionInstanceRepository.save(sessionInstance);
        response.addCookie(
                new Cookie("sessionKey", sessionInstance.getSessionKey()));
    }
    
    /**
     * Assigns a new session to a user, and returns its key as a cookie.
     *
     * @param sessionOwner The user to generate a session for
     * @param response     The HTTP server response to use to return the session
     *                     key as a cookie
     *
     * @return The newly created SessionInstance object
     */
    private SessionInstance assignNewSession (
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
     * Sets the expiry date for all unexpired sessions of a user to the current
     * time. This effectively signs them out of all locations.
     *
     * @param user User to force sign out
     */
    private void invalidateSessions (User user) {
        sessionInstanceRepository
                .findByUserAndExpiryDateGreaterThan(
                        user,
                        DateService.getDate())
                .forEach(s -> {
                    s.setExpiryDate(DateService.getDate());
                    sessionInstanceRepository.save(s);
                });
    }
    
    /**
     * Saves a new user to the database.
     *
     * @param username New user's username
     * @param password New user's password
     *
     * @return The newly created User entity
     */
    private User registerNewUser (String username, String password) {
        byte[] salt = SecurityService.getSalt();
        byte[] hash = SecurityService.hashPassword(
                password.getBytes(),
                salt,
                SecurityConstants.HASH_TIME_COST,
                SecurityConstants.HASH_MEMORY_COST,
                SecurityConstants.HASH_PARALLELISM);
        
        User newUser = new User(username);
        CredentialSet newCredentialSet = new CredentialSet(
                newUser,
                hash,
                salt,
                SecurityConstants.HASH_TIME_COST,
                SecurityConstants.HASH_MEMORY_COST,
                SecurityConstants.HASH_PARALLELISM);
        userRepository.save(newUser);
        credentialSetRepository.save(newCredentialSet);
        LOG.info("New user added: " + newUser.toString());
        return newUser;
    }
    
    /**
     * Updates a user's password and hash iterations in the database.
     *
     * @param user        User to update
     * @param newPassword New password to set
     */
    private void changePassword (User user, String newPassword) {
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
     * Checks that the user's password is hashed the correct number of times,
     * and if not, updates it.
     *
     * @param user            User to check
     * @param correctPassword If the hash iterations is incorrect, this is
     *                        hashed the correct number of times.
     */
    private void ensureCorrectHashParameters (
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
    
    @PostMapping("register")
    public String attemptRegistration (
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String password1,
            @RequestParam(defaultValue = "") String password2,
            @CookieValue(value = "sessionKey",
                         defaultValue = "") String sessionKey,
            HttpServletResponse response) {
        if (!password1.equals(password2)) {
            return "passwords do not match";
        }
        if (userRepository.findByUsername(username).size() != 0) {
            return "user already exists";
        }
        if (findUserFromSessionKey(sessionKey).isPresent()) {
            return "already logged in";
        }
        // Create new user & sign them in
        User newUser = registerNewUser(username, password1);
        SessionInstance newSession = assignNewSession(newUser, response);
        // Serve content
        return "signed in: \n\n" + newUser + "\n" + newSession;
    }
    
    @PostMapping("login")
    public String attemptLogin (
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String passwordAttempt,
            @CookieValue(value = "sessionKey",
                         defaultValue = "") String sessionKey,
            HttpServletResponse response) {
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
                return "success";
            } else {
                System.out.println("Incorrect password: " + passwordAttempt);
                return "no";
            }
        }
    }
    
    /**
     * Attempts to find a single user by a session key
     *
     * @param sessionKey The session key
     *
     * @return An Optional filled with a User if present, or empty Optional
     * otherwise
     */
    private Optional<User> findUserFromSessionKey (String sessionKey) {
        List<SessionInstance> matchingSessions = sessionInstanceRepository
                .findBySessionKeyAndExpiryDateGreaterThan(
                        sessionKey,
                        DateService.getDate());
        if (matchingSessions.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(matchingSessions.get(0).getUser());
    }
    
    @PostMapping("test-session")
    public String getUsername (
            @CookieValue(value = "sessionKey",
                         defaultValue = "") String sessionKey) {
        Optional<User> maybeUser = findUserFromSessionKey(sessionKey);
        if (maybeUser.isPresent()) {
            return maybeUser.get().getUsername();
        } else {
            return "";
        }
    }
    
    private TransactionGroup initialiseTransactionGroup (
            User user,
            Date initialisationDate,
            Date expiryDate) {
        // Slightly neater shortcut for a new, empty byte array
        Supplier<byte[]> e = () -> new byte[0];
        // Create new transaction group
        TransactionGroup newGroupEntity = new TransactionGroup(
                user,
                initialisationDate,
                expiryDate,
                e.get(),
                e.get());
        // Create dummy keys
        DataEncryptionKey newDekEntity = new DataEncryptionKey(
                e.get(),
                e.get(),
                newGroupEntity);
        KeyEncryptionKeyData newKekEntity = new KeyEncryptionKeyData(
                e.get(),
                8,
                1,
                1,
                newDekEntity);
        // Initialise opposite direction connection
        newGroupEntity.setDataEncryptionKey(newDekEntity);
        newDekEntity.setKeyEncryptionKeyData(newKekEntity);
        // Return new transaction group
        // Don't need to save since we'll overwrite immediately anyway
        return newGroupEntity;
    }
    
    @PostMapping("set-transaction-group")
    public boolean setTransactionGroup (
            @RequestParam String unparsedDate,
            @RequestParam String password,
            @RequestParam String json,
            @CookieValue(value = "sessionKey",
                         defaultValue = "") String sessionKey) {
        System.out.println("Date: " + unparsedDate);
        // Find the user
        User user = findUserFromSessionKey(sessionKey)
                .orElseThrow(getLogAndThrower(
                        "set-transaction-group invalid session key: "
                                + sessionKey));
        // Parse the date
        Date date = DateService
                .parseDate(unparsedDate)
                .orElseThrow(getLogAndThrower(
                        "set-transaction-group invalid date: " + unparsedDate));
        // Ensure date rounds correctly
        if (!DateService.getEarliestMondayMidnight(date).equals(date)) {
            throw getLogAndThrower("set-transaction-group non-Monday date: "
                    + date).get();
        }
        // Retrieve group
        TransactionGroup group = transactionGroupRepository
                .findByUserAndStartDate(user, date)
                .stream()
                .findFirst()
                .orElseGet(() -> initialiseTransactionGroup(
                        user,
                        date,
                        DateService.getFutureDate(
                                DateService
                                        .DEFAULT_TRANSACTION_GROUP_DURATION)));
        // Generate new encryption information
        byte[] newKekSalt = SecurityService.getSalt();
        byte[] newDekIv = SecurityService
                .getSaltOfLength(SecurityConstants.AES_BLOCK_SIZE);
        byte[] newTransactionGroupIv = SecurityService
                .getSaltOfLength(SecurityConstants.AES_BLOCK_SIZE);
        int timeCost = SecurityConstants.HASH_TIME_COST;
        int memoryCost = SecurityConstants.HASH_MEMORY_COST;
        int parallelism = SecurityConstants.HASH_PARALLELISM;
        
        byte[] newKek = SecurityService.hashPassword(
                password.getBytes(),
                newKekSalt,
                timeCost,
                memoryCost,
                parallelism);
        byte[] newDek = SecurityService
                .getSaltOfLength(SecurityConstants.AES_BLOCK_SIZE);
        byte[] encryptedJson = SecurityService.aesEncrypt(
                json.getBytes(),
                newDek,
                newTransactionGroupIv);
        byte[] encryptedDek = SecurityService.aesEncrypt(
                newDek,
                newKek,
                newDekIv);
        
        // Save new encrypted data and encryption information to database
        group.setIv(newTransactionGroupIv);
        group.setRawData(encryptedJson);
        transactionGroupRepository.save(group);
        
        DataEncryptionKey dekEntity = group.getDataEncryptionKey();
        dekEntity.setIv(newDekIv);
        dekEntity.setEncryptedKey(encryptedDek);
        dataEncryptionKeyRepository.save(dekEntity);
        
        KeyEncryptionKeyData kekEntity = dekEntity.getKeyEncryptionKeyData();
        kekEntity.setSalt(newKekSalt);
        kekEntity.setTimeCost(timeCost);
        kekEntity.setMemoryCost(memoryCost);
        kekEntity.setParallelism(parallelism);
        keyEncryptionKeyDataRepository.save(kekEntity);
        
        // Signal success
        return true;
    }
    
    @PostMapping("get-transaction-group")
    public String getTransactionGroup (
            @RequestParam String unparsedDate,
            @RequestParam String password,
            @CookieValue(value = "sessionKey",
                         defaultValue = "") String sessionKey) {
        // Find the user
        User user = findUserFromSessionKey(sessionKey)
                .orElseThrow(getLogAndThrower(
                        "get-transaction-group invalid session key: "
                                + sessionKey));
        // Parse the date
        Date date = DateService.parseDate(unparsedDate)
                .orElseThrow(getLogAndThrower(
                        "get-transaction-group invalid date: "
                                + unparsedDate));
        // Find the relevant transaction group
        TransactionGroup group = transactionGroupRepository
                .findByUserAndStartDate(user, date)
                .stream()
                .findFirst()
                .orElseThrow(getLogAndThrower(
                        "get-transaction-group unable to locate group: "
                                + unparsedDate));
        // Generate the key-encryption-key, and decrypt the data-encryption-key
        byte[] passwordBytes = password.getBytes();
        DataEncryptionKey dekEntity = group.getDataEncryptionKey();
        KeyEncryptionKeyData kekEntity = dekEntity.getKeyEncryptionKeyData();
        byte[] generatedKek = SecurityService.hashPassword(
                passwordBytes,
                kekEntity.getSalt(),
                kekEntity.getTimeCost(),
                kekEntity.getMemoryCost(),
                kekEntity.getParallelism());
        byte[] decryptedDek = SecurityService.aesDecrypt(
                dekEntity.getEncryptedKey(),
                generatedKek,
                dekEntity.getIv());
        // Use the decrypted DEK to decrypt stored information
        byte[] decryptedJson = SecurityService.aesDecrypt(
                group.getRawData(),
                decryptedDek,
                group.getIv());
        // Return stored information as a string
        return new String(decryptedJson);
    }
    
    /**
     * Finds the {@link TransactionGroup} for a logged in user ({@code
     * sessionKey}) at {@code Date}. Stored in an optional.
     *
     * @param sessionKey Session key of logged in user for which to find a
     *                   transaction
     * @param date       Date to search for transactions on
     *
     * @return Empty optional if no transactions found at date, or invalid
     * session key. Full optional otherwise.
     */
    private Optional<TransactionGroup> findTransactionGroup (
            String sessionKey,
            Date date) {
        Optional<User> user = findUserFromSessionKey(sessionKey);
        if (user.isPresent()) {
            List<TransactionGroup> transactions = transactionGroupRepository
                    .findByUserAndStartDate(user.get(), date);
            if (transactions.size() == 0) {
                return Optional.empty();
            } else {
                if (transactions.size() > 1) {
                    LOG.warn("Multiple transaction groups for same week - "
                            + user.toString());
                }
                return Optional.of(transactions.get(0));
            }
        } else {
            return Optional.empty();
        }
    }
    
    @PostMapping("round-date")
    public String roundDate (
            @RequestParam(defaultValue = "") String unparsedDate) {
        Date date = DateService
                .parseDate(unparsedDate)
                .orElseThrow(getLogAndThrower("round-date failure to parse"));
        Date dateRounded = DateService.getEarliestMondayMidnight(date);
        return DateService.formatDate(dateRounded);
    }
    
    @PostMapping("get-transaction-group-headers")
    public String getTransactionDates (
            @CookieValue(value = "sessionKey",
                         defaultValue = "") String sessionKey) {
        // Check we're logged in
        Optional<User> maybeUser = findUserFromSessionKey(sessionKey);
        if (!maybeUser.isPresent()) {
            LOG.info("Attempt to get transaction headers with no session key");
            return "";
        }
        // Get transactions
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat formatter
                = new SimpleDateFormat(DateService.DATE_FORMAT);
        transactionGroupRepository
                .findByUser(maybeUser.get())
                .stream()
                .map(tg -> formatter.format(tg.getStartDate()))
                .forEachOrdered(jsonArray::put);
        return new JSONObject().put("dates", jsonArray).toString();
    }
    
    
    @PostMapping("logout")
    public String logout (
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
    
    @PostMapping("groups")
    public String getAllGroups () {
        return dumpRepository(transactionGroupRepository);
    }
    
    @GetMapping("users")
    public String getAllUsers () {
        return dumpRepository(userRepository);
    }
    
    @GetMapping("sessions")
    public String getAllSessions () {
        return dumpRepository(sessionInstanceRepository);
    }
}
