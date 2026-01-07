package com.alura.literalura.exception;

/**
 * Custom exception for the BookVerse application.
 * Used to handle application-specific errors with proper error messages and cause tracking.
 */
public class BookVerseException extends RuntimeException {
    
    private final String errorCode;
    private final ErrorCategory category;

    /**
     * Constructs a new BookVerseException with the specified detail message.
     * @param message The detail message
     */
    public BookVerseException(String message) {
        super(message);
        this.errorCode = "BV_ERROR";
        this.category = ErrorCategory.GENERAL;
    }

    /**
     * Constructs a new BookVerseException with the specified detail message and cause.
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public BookVerseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BV_ERROR";
        this.category = ErrorCategory.GENERAL;
    }

    /**
     * Constructs a new BookVerseException with the specified detail message, error code, and category.
     * @param message The detail message
     * @param errorCode The error code for identification
     * @param category The error category
     */
    public BookVerseException(String message, String errorCode, ErrorCategory category) {
        super(message);
        this.errorCode = errorCode;
        this.category = category;
    }

    /**
     * Constructs a new BookVerseException with the specified detail message, cause, error code, and category.
     * @param message The detail message
     * @param cause The cause of the exception
     * @param errorCode The error code for identification
     * @param category The error category
     */
    public BookVerseException(String message, Throwable cause, String errorCode, ErrorCategory category) {
        super(message, cause);
        this.errorCode = errorCode;
        this.category = category;
    }

    /**
     * Gets the error code associated with this exception.
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error category associated with this exception.
     * @return The error category
     */
    public ErrorCategory getCategory() {
        return category;
    }

    /**
     * Returns a formatted string representation of the exception.
     * @return Formatted exception details
     */
    @Override
    public String toString() {
        return String.format("BookVerseException[%s:%s] %s", 
                           category.name(), 
                           errorCode, 
                           getMessage());
    }

    /**
     * Enumeration of error categories for better error handling and classification.
     */
    public enum ErrorCategory {
        /**
         * General application errors.
         */
        GENERAL("GEN"),
        
        /**
         * Database-related errors.
         */
        DATABASE("DB"),
        
        /**
         * Network/API communication errors.
         */
        NETWORK("NET"),
        
        /**
         * Data validation errors.
         */
        VALIDATION("VAL"),
        
        /**
         * JSON parsing/serialization errors.
         */
        JSON_PROCESSING("JSON"),
        
        /**
         * External service integration errors.
         */
        EXTERNAL_SERVICE("EXT"),
        
        /**
         * Business logic errors.
         */
        BUSINESS("BIZ"),
        
        /**
         * Configuration errors.
         */
        CONFIGURATION("CFG");

        private final String prefix;

        ErrorCategory(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
