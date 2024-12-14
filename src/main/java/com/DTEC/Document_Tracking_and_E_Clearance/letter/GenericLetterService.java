package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import java.util.List;

public interface GenericLetterService {

    List<GenericResponse> getAllLetters(LetterStatus status);
}
