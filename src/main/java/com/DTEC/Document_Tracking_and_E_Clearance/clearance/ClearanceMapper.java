package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.user.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClearanceMapper {

    private final UserMapper userMapper;

    public ClearanceMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Clearance toClearance(String schoolYear){
        return Clearance.builder()
                .schoolYear(schoolYear)
                .build();
    }

    public ClearanceResponseDto toClearanceResponseDto(Clearance clearance){
        return new ClearanceResponseDto(
                clearance.getId(),
                clearance.getSchoolYear(),
                clearance.getClearanceSignoffs(),
                clearance.getCreatedAt(),
                clearance.getLastModified(),
                clearance.getStudent() != null ? this.userMapper.toUserInfoResponseDto(clearance.getStudent()) : null
        );
    }

    public List<ClearanceResponseDto> toClearanceResponseDtoList(List<Clearance> clearances){
        return clearances
                .stream()
                .map(this::toClearanceResponseDto)
                .toList();
    }
}
