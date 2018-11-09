package com.angusbarnes.bills;

public class SecurityConstants {
    /**
     * Number of characters that makes up a session key
     */
    public static final int SESSION_KEY_LENGTH = 64;
    /**
     * How long a user remains logged in for by default, in seconds
     */
    public static final int SESSION_DURATION = 60 * 60;
    /**
     * How many times hash is iterated
     */
    public static final int HASH_TIME_COST = 15;
    /**
     * How much memory calculating one hash requires
     */
    public static final int HASH_MEMORY_COST = 65536;
    /**
     * How many threads of execution calculating one hash requires
     */
    public static final int HASH_PARALLELISM = 2;
    /**
     * The number of bytes in a generated hash
     */
    public static final int HASH_LENGTH = 16;
    /**
     * The size of a block used in AES in bytes
     */
    public static final int AES_BLOCK_SIZE = 16;
}
