package org.bank.common.exception;

public class SameAccountTransferException extends AccountException {
    public SameAccountTransferException(Long accountId)
    {
        super(String.format("Cannot transfer to same account with id: %s", accountId));
    }
}
