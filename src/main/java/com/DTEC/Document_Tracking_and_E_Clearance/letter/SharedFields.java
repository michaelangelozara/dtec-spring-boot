package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;

import java.time.LocalDateTime;

public interface SharedFields {

    Integer getId();

    LocalDateTime getCreatedAt();

    TypeOfLetter getType();

    String getNameOfTransaction();

    Club getClub();

    LetterStatus getStatus();

    LocalDateTime getLastModified();
}
