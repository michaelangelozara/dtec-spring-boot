package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import com.DTEC.Document_Tracking_and_E_Clearance.e_signature.ESignature;
import com.DTEC.Document_Tracking_and_E_Clearance.e_signature.ESignatureRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.e_signature.ESignatureResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FingerprintServiceImp implements FingerprintService {

    private final FingerprintRepository fingerprintRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserUtil userUtil;
    private final FingerprintMapper fingerprintMapper;
    private final ESignatureRepository eSignatureRepository;

    @Value("${websocket.code}")
    private String webCode;


    public FingerprintServiceImp(FingerprintRepository fingerprintRepository, UserRepository userRepository, SimpMessagingTemplate simpMessagingTemplate, UserUtil userUtil, FingerprintMapper fingerprintMapper, ESignatureRepository eSignatureRepository) {
        this.fingerprintRepository = fingerprintRepository;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userUtil = userUtil;
        this.fingerprintMapper = fingerprintMapper;
        this.eSignatureRepository = eSignatureRepository;
    }

    @Override
    public void enroll(String username, String fingerprint) {
        var user = this.userRepository.findByUsername(username)
                .orElse(null);
        Map<String, String> data = new HashMap<>();
        if (user == null) {
            data.put("message", "Invalid Username");
            this.simpMessagingTemplate.convertAndSend("/topic/enrollment.failed", data);
            return;
        }

        if (user.getFingerprints().size() > 2) {
            data.put("message", "The User reached the maximum of 3 registered fingerprint");
            this.simpMessagingTemplate.convertAndSend("/topic/enrollment.failed", data);
            return;
        }

        var fingerprintInstance = Fingerprint.builder()
                .fingerprint(fingerprint)
                .user(user)
                .build();

        this.fingerprintRepository.save(fingerprintInstance);
        data.put("message", "Fingerprint is Successfully Enrolled");
        this.simpMessagingTemplate.convertAndSend("/topic/enrollment.success", data);

    }

    @Override
    public List<FingerprintResponseDto> getFingerprints() {
        var user = this.userUtil.getCurrentUser();
        if (user == null)
            throw new UnauthorizedException("Session Expired");

        var fingerprints = this.fingerprintRepository.findAllByUserId(user.getId());
        return this.fingerprintMapper.toFingerprintResponseDtoList(fingerprints);
    }

    @Transactional
    @Override
    public void addESignature(Map<String, String> data) {
        try {
            Integer userId = Integer.parseInt(data.get("user_id"));
            String image = data.get("image");

            if (image == null || image.isEmpty())
                throw new BadRequestException("Invalid Image");
            var eSignature = ESignature.builder()
                    .image(image)
                    .build();
            var savedESignature = this.eSignatureRepository.save(eSignature);

            var user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

            if(user.getRole().equals(Role.STUDENT_OFFICER) || user.getRole().equals(Role.MODERATOR)){
                user.setESignature(image);
                this.userRepository.save(user);
                return;
            }

            var fingerprints = user.getFingerprints();
            if (fingerprints.isEmpty()) throw new ForbiddenException("Please Register Fingerprint First");

            for (var fingerprint : fingerprints) {
                fingerprint.setESignature(savedESignature);
            }
            this.fingerprintRepository.saveAll(fingerprints);

        } catch (NumberFormatException e) {
            throw new ForbiddenException("Invalid User id");
        }
    }

    @Transactional
    @Override
    public ESignatureResponseDto getMyESignature() {
        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new ResourceNotFoundException("User not Found");

        var fingerprints = this.fingerprintRepository.findAllByUserId(user.getId());

        var eSignature = fingerprints.get(0).getESignature();
        if (eSignature == null) throw new ResourceNotFoundException("E-Signature not Found");
        return new ESignatureResponseDto(
                eSignature.getId(),
                eSignature.getImage()
        );
    }

    @Override
    public Map<String, List<String>> getAllFingerprints(String code) {
        if (!code.equals(webCode)) {
            throw new BadRequestException("Code is incorrect");
        }
        List<String> encodedFingerprints = new ArrayList<>();
        var fingerprints = this.fingerprintRepository.findAll();
        for(var fingerprint : fingerprints){
            encodedFingerprints.add(fingerprint.getFingerprint());
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put("fingerprints", encodedFingerprints);
        return map;
    }
}
