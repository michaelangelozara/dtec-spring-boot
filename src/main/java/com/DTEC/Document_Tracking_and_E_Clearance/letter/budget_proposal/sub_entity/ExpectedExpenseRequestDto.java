package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity;

import java.math.BigDecimal;

public record ExpectedExpenseRequestDto(
        String name,
        BigDecimal amount
) {
}
