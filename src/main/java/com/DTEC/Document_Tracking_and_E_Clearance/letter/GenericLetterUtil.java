package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;

import java.util.List;

public class GenericLetterUtil {

    public static String generateMessage(String name, SharedFields sharedFields) {
        return "Hi " + name + ",\nYour " + sharedFields.getType() + "\nTransaction status is " + sharedFields.getStatus().name() + ".";
    }

    public static User getUserByRole(List<SignedPeople> signedPeople, Role role) {
        var signedPerson = signedPeople
                .stream()
                .filter(sp -> sp != null && sp.getUser().getRole().equals(role))
                .findFirst();
        return signedPerson.map(SignedPeople::getUser).orElse(null);
    }
}
