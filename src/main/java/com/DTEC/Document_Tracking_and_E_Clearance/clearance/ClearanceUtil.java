package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignOffStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;

import java.util.*;

public class ClearanceUtil {
    public static boolean isLibrarianOrMultimediaSigned(List<ClearanceSignoff> clearanceSignoffs) {
        for (var clearanceSignoff : clearanceSignoffs) {
            if ((clearanceSignoff.getRole().equals(Role.LIBRARIAN) ||
                    clearanceSignoff.getRole().equals(Role.MULTIMEDIA)) &&
                    clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isClearanceReadyForDSAOrDeanForPersonnelClearance(
            List<ClearanceSignoff> clearanceSignoffs,
            List<Role> signedRoles
    ) {
        for (var signedRole : signedRoles) {
            boolean isExist = false;
            for (var clearanceSignoff : clearanceSignoffs) {

                if (clearanceSignoff.getUser() != null && clearanceSignoff.getRole().equals(signedRole) ||
                        (clearanceSignoff.getRole().equals(Role.LIBRARIAN) || clearanceSignoff.getRole().equals(Role.MULTIMEDIA))) {
                    isExist = true;
                    break;
                }
            }

            if (!isExist) {
                return false;
            }
        }
        return true;
    }

    public static ClearanceSignoff getClearanceSignoff(User user, Clearance clearance) {
        if (user == null) return null;

        return ClearanceSignoff.builder()
                .status(ClearanceSignOffStatus.PENDING)
                .clearance(clearance)
                .role(user.getRole())
                .user(user)
                .build();
    }

    public static boolean areAllSignaturesSettled(List<ClearanceSignoff> clearanceSignoffs, List<Role> roles) {
        for (var role : roles) {
            for (var clearanceSignoff : clearanceSignoffs) {
                if (role.equals(clearanceSignoff.getRole()) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.PENDING) ||
                        role.equals(clearanceSignoff.getRole()) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.IN_PROGRESS))
                    return false;
            }
        }
        return true;
    }

    public static boolean isSectionSignedAlready(List<ClearanceSignoff> clearanceSignoffs, Role role) {
        for (var clearanceSignoff : clearanceSignoffs) {
            if (clearanceSignoff.getRole().equals(role) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOneOfLabInChargeSigned(List<ClearanceSignoff> clearanceSignoffs) {
        for (var clearanceSignoff : clearanceSignoffs) {
            if ((clearanceSignoff.getRole().equals(Role.SCIENCE_LAB) ||
                    clearanceSignoff.getRole().equals(Role.COMPUTER_SCIENCE_LAB) ||
                    clearanceSignoff.getRole().equals(Role.ELECTRONICS_LAB) ||
                    clearanceSignoff.getRole().equals(Role.CRIM_LAB) ||
                    clearanceSignoff.getRole().equals(Role.HRM_LAB) ||
                    clearanceSignoff.getRole().equals(Role.NURSING_LAB)) &&
                    clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMyRoleIncludedForSigning(List<ClearanceSignoff> clearanceSignoffs, Role role) {
        for (var clearanceSignoff : clearanceSignoffs) {
            if (clearanceSignoff.getRole().equals(role)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isLabInChargeRole(Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.SCIENCE_LAB);
        roles.add(Role.COMPUTER_SCIENCE_LAB);
        roles.add(Role.ELECTRONICS_LAB);
        roles.add(Role.CRIM_LAB);
        roles.add(Role.HRM_LAB);
        roles.add(Role.NURSING_LAB);

        return roles.contains(role);
    }

    public static User getLabInChargeBasedOnStudentCourse(List<User> users, String courseShortName) {
        Map<String, Role> map = new HashMap<>();
        map.put("BSCrim", Role.CRIM_LAB);
        map.put("BSN", Role.NURSING_LAB);
        map.put("BSCS", Role.COMPUTER_SCIENCE_LAB);
        map.put("BSCpE", Role.ELECTRONICS_LAB);
        map.put("BSHM", Role.HRM_LAB);

        return users.stream().filter(u -> u.getRole().equals(map.get(courseShortName))).findFirst()
                .orElse(null);
    }
}
