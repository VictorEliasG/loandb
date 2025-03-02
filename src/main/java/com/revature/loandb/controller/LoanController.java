package com.revature.loandb.controller;

import com.revature.loandb.dto.LoanAuthRequestDTO;
import com.revature.loandb.model.Loan;
import com.revature.loandb.service.LoanService;

import io.javalin.http.Context;


public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService; 
    }

    public void createLoan(Context ctx) {
        LoanAuthRequestDTO req = ctx.bodyAsClass(LoanAuthRequestDTO.class);
        

        int userId = (int) ctx.sessionAttribute("userId");
        req.setUserId(userId);
        

        if (req.getUserId() <= 0 || req.getAmount() <= 0 || req.getType() == null || req.getType().isEmpty()) {
            ctx.status(400).json("{\"error\":\"Invalid loan data\"}");
            return;
        }

        Loan newLoan = new Loan(req.getUserId(), req.getAmount(), req.getType(), "pending");

        boolean success = loanService.createLoan(newLoan);

        if (success) {
            ctx.status(201).json("{\"message\":\"Loan created successfully\"}");
        } else {
                ctx.status(500).json("{\"error\":\"Failed to create loan\"}");
            }
    }

    public void getLoans(Context ctx) {
        ctx.json(loanService.getLoans());
    }

    public void getLoan(Context ctx) {
        int loanId;
        try {
            loanId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"error\":\"Invalid loan ID format\"}");
            return;
        }

        Loan loan = loanService.getLoanById(loanId);

        if (loan == null) {
            ctx.status(404).json("{\"error\":\"Loan not found\"}");
        } else {
            ctx.json(loan);
        }
    }


    public void updateLoan(Context ctx) {
        // Implementation for updating the loan
        String loanId = ctx.pathParam("id");
        LoanAuthRequestDTO req = ctx.bodyAsClass(LoanAuthRequestDTO.class);

        if (req.getUserId() <= 0 || req.getAmount() <= 0 || req.getType() == null || req.getType().isEmpty()) {
            ctx.status(400).json("{\"error\":\"Invalid loan data\"}");
            return;
        }

        System.out.println("Loan ID: " + loanId);
        System.out.println("User ID: " + req.getUserId());
        System.out.println("Amount: " + req.getAmount());
        System.out.println("Type: " + req.getType());
        System.out.println("Status: " + req.getStatus());

        Loan updatedLoan = new Loan(req.getUserId(), req.getAmount(), req.getType(), req.getStatus());
        boolean success = loanService.updateLoan(Integer.parseInt(loanId), updatedLoan);

        if (success) {
            ctx.status(200).json("{\"message\":\"Loan updated successfully\"}");
        } else {
            ctx.status(500).json("{\"error\":\"Failed to update loan\"}");
        }
    }


}
