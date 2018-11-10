package com.juliashouse.sweetpotatoes.service;

import com.juliashouse.sweetpotatoes.SecurityConstants;
import com.kosprov.jargon2.api.Jargon2.Hasher;
import com.kosprov.jargon2.api.Jargon2.Type;
import com.kosprov.jargon2.api.Jargon2.Verifier;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Supplier;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

/**
 * Handles password cryptography, as well as containing a number of important,
 * security-related program constants.
 * <p>
 * The {@code generateSessionKey} method will utilise {@link SecureRandom} to
 * generate a string of lowercase characters.
 * <p>
 * <p>The {@code getPasswordHash} method provides the resulting hash of a
 * password after combining plaintext with a salt and iterating over a number of
 * passes. Similarly, {@code verifyPassword} will combine with a salt to test a
 * plaintext password against a stored series of bytes representing a user's
 * saved, hashed password.
 */
public class SecurityService {
    
    /**
     * A random generator used throughout the class to provide cryptographically
     * secure random numbers
     */
    private static final Random RANDOM_GENERATOR = new SecureRandom();
    
    /**
     * Gets a salt a certain number of bytes long
     *
     * @param byteCount The number of bytes long to make the salt
     *
     * @return A salt a certain number of bytes long
     */
    public static byte[] getSaltOfLength (int byteCount) {
        byte[] bytes = new byte[byteCount];
        RANDOM_GENERATOR.nextBytes(bytes);
        return bytes;
    }
    
    /**
     * Gets a salt the default number of bytes long
     *
     * @return A salt, with length set by
     *
     * @see SecurityConstants#HASH_LENGTH
     */
    public static byte[] getSalt () {
        return getSaltOfLength(SecurityConstants.HASH_LENGTH);
    }
    
    /**
     * Generates a random session key of specified length. Session keys are
     * lowercase alphabetical characters.
     *
     * @param length The length of the provided session key in characters
     *
     * @return A randomly generated alphabetical session key
     */
    public static String generateSessionKey (int length) {
        Supplier<Character> randomHex =
                () -> (char) (RANDOM_GENERATOR.nextInt('z' - 'a') + 'a');
        
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            key.append(randomHex.get());
        }
        
        return key.toString();
    }
    
    public static void main (String[] args) {
        String password = "hello";
        String data64
                = "27zY7/FLF/Ii1A4vvYJBf731tJYdkrvHTxhD+9AI2GKbWZoW6TpZL+3+CslBDvBx";
        String dataIv64 = "S7dPCkZIiwdaKFeF6Is7PQ==";
        String dek64 = "j3L8F50Y3t5YoltVIQnFD60+cp3QiJm2ZxuGdXPnhf4=";
        String dekIv64 = "rXXAh8aklbtS9zeBuPxNMA==";
        String kekSalt64 = "W+aRDhhrGQ0fv/ACd/qEOA==";
        
        byte[] kek = hashPassword(
                password.getBytes(),
                Base64Service.fromBase64(kekSalt64),
                SecurityConstants.HASH_TIME_COST,
                SecurityConstants.HASH_MEMORY_COST,
                SecurityConstants.HASH_PARALLELISM);
        
        byte[] dek = aesDecrypt(Base64Service.fromBase64(dek64), kek, Base64Service.fromBase64(dekIv64));
        
        byte[] data = aesDecrypt(Base64Service.fromBase64(data64), dek, Base64Service.fromBase64(dataIv64));
    
        System.out.println(new String(data));
        
        
    }
    
    /**
     * Calculate an argon2id hash, using given parameters.
     *
     * @param password    Password bytes to hash
     * @param salt        Salt bytes to add when hashing
     * @param timeCost    The number of passes to apply
     * @param memoryCost  The amount of memory required. Minimum 8.
     * @param parallelism The amount of parallelism required.
     *
     * @return The hashed result
     */
    public static byte[] hashPassword (final byte[] password,
                                       final byte[] salt,
                                       final int timeCost,
                                       final int memoryCost,
                                       final int parallelism) {
        Hasher hasher = jargon2Hasher()
                .type(Type.ARGON2id)
                .timeCost(timeCost)
                .memoryCost(memoryCost)
                .parallelism(parallelism)
                .hashLength(SecurityConstants.HASH_LENGTH);
        
        return hasher.password(password).salt(salt).rawHash();
        
    }
    
    /**
     * Verify if the given hash correctly matches one calculated with the given
     * parameters.
     *
     * @param password    Password bytes to hash
     * @param salt        Salt bytes to add when hashing
     * @param hash        The pre-calculated hash to verify
     * @param timeCost    The number of passes to apply
     * @param memoryCost  The amount of memory required. Minimum 8.
     * @param parallelism The amount of parallelism required.
     *
     * @return True if the provided hash is valid. False otherwise.
     */
    public static boolean verifyPassword (final byte[] password,
                                          final byte[] salt,
                                          final byte[] hash,
                                          final int timeCost,
                                          final int memoryCost,
                                          final int parallelism) {
        Verifier verifier = jargon2Verifier()
                .type(Type.ARGON2id)
                .timeCost(timeCost)
                .memoryCost(memoryCost)
                .parallelism(parallelism)
                .salt(salt);
        return verifier.hash(hash).password(password).verifyRaw();
    }
    
    /**
     * Performs an AES cipher operation in CBC mode using Java's inbuilt Cipher
     * class.
     * <p>
     * As CBC mode is used, an initialisation vector must be provided.
     * <p>
     * See {@see Cipher#init} for mode information
     *
     * @param aesMode              The mode to apply.
     * @param data                 The data to encrypt or decrypt
     * @param key                  The key to use when encrypting or decrypting
     * @param initialisationVector The IV to use when
     *
     * @return The result of the cipher operation
     */
    private static byte[] manageAes (int aesMode,
                                     final byte[] data,
                                     final byte[] key,
                                     final byte[] initialisationVector
    ) {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(
                    aesMode,
                    new SecretKeySpec(key, "AES"),
                    new IvParameterSpec(initialisationVector));
            return c.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Encrypts given plaintext using CBC AES, with the given key and
     * initialisation vector.
     *
     * @param plaintext            Plaintext to encrypt
     * @param key                  Key to use when encrypting
     * @param initialisationVector Initialisation vector to use (since CBC mode
     *                             is used)
     *
     * @return The encrypted plaintext
     */
    public static byte[] aesEncrypt (final byte[] plaintext,
                                     final byte[] key,
                                     final byte[] initialisationVector) {
        return manageAes(
                Cipher.ENCRYPT_MODE,
                plaintext,
                key,
                initialisationVector);
    }
    
    /**
     * Encrypts given plaintext using CBC AES, with the given key and
     * initialisation vector.
     *
     * @param cipherText           Plaintext to encrypt
     * @param key                  Key to use when encrypting
     * @param initialisationVector Initialisation vector to use (since CBC mode
     *                             is used)
     *
     * @return The encrypted plaintext
     */
    public static byte[] aesDecrypt (final byte[] cipherText,
                                     final byte[] key,
                                     final byte[] initialisationVector) {
        return manageAes(
                Cipher.DECRYPT_MODE,
                cipherText,
                key,
                initialisationVector);
    }
}