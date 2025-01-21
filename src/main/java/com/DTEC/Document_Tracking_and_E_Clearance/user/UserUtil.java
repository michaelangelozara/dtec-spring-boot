package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.Fingerprint;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserUtil {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        } else {
            return null;
        }
    }

    public static User getUserByRole(List<User> allOfficeInCharge, Role role, String nameOfOffice) {
        return allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(role)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the " + nameOfOffice + "'s account is not created yet"));
    }

    public static User getUserByRole(List<User> allOfficeInCharge, Role role) {
        return allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(role)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("There's no " + role.name() + " in the Organization."));
    }

    public static boolean checkESignature(User user){
        if(user.getESignature().isEmpty())
            return false;
        return true;
    }

    public static String getUserFullName(User user) {
        if (user == null) {
            return ""; // Return empty string if user is null
        }

        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastname() != null ? user.getLastname() : "";
        String middleInitial = (user.getMiddleName() != null && !user.getMiddleName().isEmpty())
                ? user.getMiddleName().charAt(0) + ". "
                : "";

        return firstName + " " + middleInitial + lastName;

    }

    public static Set<Role> getOfficeInChargeRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.DSA);
        roles.add(Role.PRESIDENT);
        roles.add(Role.OFFICE_HEAD);
        roles.add(Role.FINANCE);
        roles.add(Role.GUIDANCE);
        roles.add(Role.DEAN);
        roles.add(Role.CASHIER);
        roles.add(Role.LIBRARIAN);
        roles.add(Role.SCHOOL_NURSE);
        roles.add(Role.PROGRAM_HEAD);
        roles.add(Role.REGISTRAR);
        roles.add(Role.SCIENCE_LAB);
        roles.add(Role.COMPUTER_SCIENCE_LAB);
        roles.add(Role.ELECTRONICS_LAB);
        roles.add(Role.CRIM_LAB);
        roles.add(Role.HRM_LAB);
        roles.add(Role.NURSING_LAB);
        roles.add(Role.ACCOUNTING_CLERK);
        roles.add(Role.CUSTODIAN);
        roles.add(Role.VPAF);
        roles.add(Role.VPA);
        roles.add(Role.MULTIMEDIA);
        return roles;
    }

    public static Set<Role> getLabInChargeRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.SCIENCE_LAB);
        roles.add(Role.COMPUTER_SCIENCE_LAB);
        roles.add(Role.ELECTRONICS_LAB);
        roles.add(Role.CRIM_LAB);
        roles.add(Role.HRM_LAB);
        roles.add(Role.NURSING_LAB);
        return roles;
    }

    // dsa, office head, guidance, and school nurse
    public static Set<Role> getDOGSRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.DSA);
        roles.add(Role.OFFICE_HEAD);
        roles.add(Role.GUIDANCE);
        roles.add(Role.SCHOOL_NURSE);
        return roles;
    }

    public String getOfficeInChargeSignature(User oic) {
        var fingerprints = oic.getFingerprints();
        var eSignature = fingerprints
                .stream()
                .filter(f -> f.getESignature() != null && f.getESignature().getFingerprints() != null && !f.getESignature().getFingerprints().isEmpty())
                .findFirst()
                .map(Fingerprint::getESignature)
                .orElseThrow(() -> new ResourceNotFoundException("The " + oic.getRole().name() + " hasn't E-Signature yet"));
        return eSignature.getImage();
    }

    public static List<User> getAllUserExceptTo(List<User> users, Set<Role> roles) {
        return users
                .stream()
                .filter(u -> !roles.contains(u.getRole()))
                .toList();
    }

    public static boolean rolesNoMultipleAccount(Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.DSA);
        roles.add(Role.PRESIDENT);
        roles.add(Role.FINANCE);
        roles.add(Role.OFFICE_HEAD);
        roles.add(Role.GUIDANCE);
        roles.add(Role.LIBRARIAN);
        roles.add(Role.VPAF);
        roles.add(Role.VPA);
        roles.add(Role.MULTIMEDIA);
        roles.add(Role.SCIENCE_LAB);
        roles.add(Role.COMPUTER_SCIENCE_LAB);
        roles.add(Role.ELECTRONICS_LAB);
        roles.add(Role.CRIM_LAB);
        roles.add(Role.HRM_LAB);
        roles.add(Role.NURSING_LAB);
        return roles.contains(role);
    }

    public static String removeWhiteSpace(String str){
        return str.trim();
    }
}
