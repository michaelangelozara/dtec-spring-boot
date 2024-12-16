package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GenericLetterService {

    List<GenericResponse> getAllLetters(int s);

    void onClick(TypeOfLetter type, int id);

    void signLetter(TypeOfLetter type, String signature, int letterId);

    void rejectLetter(TypeOfLetter type, int id, String reasonOfRejection);
}
