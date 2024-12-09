package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IOPPMapper {

    public IOPP toIOPP(IOPPRequestDto dto){
        return IOPP.builder()
                .title(dto.title())
                .rationale(dto.rationale())
                .targetGroup(dto.targetGroup())
                .dateAndPlace(dto.dateAndPlace())
                .programOrFlowOfActivity(dto.programOrFlowOfActivity())
                .build();
    }
}
