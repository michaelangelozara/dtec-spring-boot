package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;

import java.time.LocalDateTime;
import java.util.List;

public interface SharedFields {

    Integer getId();

    LocalDateTime getCreatedAt();

    TypeOfLetter getType();

    String getNameOfTransaction();

    Club getClub();

    LetterStatus getStatus();

    LocalDateTime getLastModified();

    List<SignedPeople> getSignedPeople();

    CurrentLocation getCurrentLocation();

    String getReasonOfRejection();
}
