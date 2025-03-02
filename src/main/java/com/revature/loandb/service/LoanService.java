package com.revature.loandb.service;

import com.revature.loandb.dao.LoanDao;
import com.revature.loandb.model.Loan;

public class LoanService {
    private final LoanDao loanDao;

    public LoanService(LoanDao loanDao) {
        this.loanDao = loanDao;
    }

    public boolean createLoan(Loan loan) {
        return loanDao.createLoan(loan);
    }
}
