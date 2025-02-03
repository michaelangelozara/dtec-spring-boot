package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;

import java.util.List;

public class GenericLetterUtil {

    public enum MessageType {
        STUDENT,
        PERSONNEL
    }

    public static String generateMessageWhenLetterIsSubmittedOrMovesToTheNextOffice(String name, SharedFields sharedFields) {
        return "Hi " + name + ",\nYour " + sharedFields.getType().name() + " has been submitted and forwarded to " + sharedFields.getCurrentLocation().name() + ". You will be updated once it is completed and forwarded to the next office.";
    }

    public static String generateMessageForFinalDecisionOfLetter(String name, SharedFields sharedFields) {
        return "Hi " + name + ",\nYour " + sharedFields.getType().name() + " has been " + sharedFields.getStatus().name() + ".\nPlease log in to your DTEC account to view the details.";
    }

    public static String generateMessageForModerator(String name, SharedFields letter) {
        return "Hi " + name + ",\nYou have a pending " + letter.getType().name() + " that needs your evaluation. Please log in to your DTEC account to review the transaction.";
    }

    public static String generateMessageAfterClearanceReleased() {
        return "Your clearance has been released. Please log in to your DTEC account to process and submit your clearance for evaluation";
    }

    public static String generateMessageForOfficeInChargeWhenClearancesRelease(String name) {
        return "Hi " + name + ",\nThe clearance has been released. Please log in to your DTEC account to review and sign the pending clearance requests.";
    }

    public static String generateMessageForUserWhoSubmittedClearance(String name) {
        return "Hi " + name + ",\nYou have successfully submitted your clearance. Please check your DTEC account for the status.";
    }

    // the role should be student or personnel
    public static String generateMessageWhenTheClearanceIsCompleted(String name, MessageType type) {
        return type.equals(MessageType.STUDENT) ? "Hi " + name + ",\nYour clearance has been completed. Please present this message to the cashier to claim your Exam Permit." :
                "Hi " + name + ",\nYour clearance has been completed. Please present this message to the Business Office to process your salary.";
    }

    public static User getUserByRole(List<SignedPeople> signedPeople, Role role) {
        var signedPerson = signedPeople
                .stream()
                .filter(sp -> sp != null && sp.getUser().getRole().equals(role))
                .findFirst();
        return signedPerson.map(SignedPeople::getUser).orElse(null);
    }
}
