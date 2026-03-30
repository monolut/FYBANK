package org.bank.accountservice.exception;

import org.apache.catalina.realm.JAASCallbackHandler;

public class SameAccountTransferException extends AccountException {
    public SameAccountTransferException(Long accountId)
    {
        super(String.format("Cannot transfer to same account with id: ", accountId));
    }
}
