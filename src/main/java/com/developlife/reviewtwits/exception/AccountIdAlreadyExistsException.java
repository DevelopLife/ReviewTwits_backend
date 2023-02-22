package com.developlife.reviewtwits.exception;

/**
 * @author ghdic
 * @since 2023/02/19
 */
public class AccountIdAlreadyExistsException extends RuntimeException {
    public AccountIdAlreadyExistsException(String message) {
        super(message);
    }
}
