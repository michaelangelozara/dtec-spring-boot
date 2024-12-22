package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoffMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClearanceMapper {

    private final UserMapper userMapper;
    private final ClearanceSignoffMapper clearanceSignoffMapper;

    public ClearanceMapper(UserMapper userMapper, ClearanceSignoffMapper clearanceSignoffMapper) {
        this.userMapper = userMapper;
        this.clearanceSignoffMapper = clearanceSignoffMapper;
    }

    public ClearanceResponseDto toClearanceResponseDto(Clearance clearance){
        return new ClearanceResponseDto(
                clearance.getId(),
                clearance.getSchoolYear(),
                clearance.getCreatedAt(),
                clearance.getLastModified(),
                this.userMapper.toUserInfoResponseDto(clearance.getUser()),
                this.clearanceSignoffMapper.toClearanceSignoffResponseDtoList(clearance.getClearanceSignoffs()),
                clearance.getStatus(),
                clearance.getDateOfStudentSignature(),
                clearance.getStudentSignature(),
                clearance.getType(),
                clearance.isSubmitted()
        );
    }

    public List<ClearanceResponseDto> toClearanceResponseDtoList(List<Clearance> clearances){
        return clearances
                .stream()
                .map(this::toClearanceResponseDto)
                .toList();
    }
}
