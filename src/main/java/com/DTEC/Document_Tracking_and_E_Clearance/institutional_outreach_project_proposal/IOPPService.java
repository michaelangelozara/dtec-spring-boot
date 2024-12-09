package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IOPPService {

    private final IOPPRepository ioppRepository;
    private final IOPPMapper ioppMapper;
    private final UserUtil userUtil;
    private final CAOORepository caooRepository;

    public IOPPService(IOPPRepository ioppRepository, IOPPMapper ioppMapper, UserUtil userUtil, CAOORepository caooRepository) {
        this.ioppRepository = ioppRepository;
        this.ioppMapper = ioppMapper;
        this.userUtil = userUtil;
        this.caooRepository = caooRepository;
    }

    @Transactional
    public String requestLetter(IOPPRequestDto dto) {
        var mayor = this.userUtil.getCurrentUser();
        if (mayor == null)
            throw new ResourceNotFoundException("Mayor not Found");

        var iopp = this.ioppMapper.toIOPP(dto);
        var savedIoop = this.ioppRepository.save(iopp);
        List<CAOO> caoos = new ArrayList<>();
        for (var caooDto : dto.caoos()) {
            var caoo = CAOO.builder()
                    .activities(caooDto.activities())
                    .objectives(caooDto.objectives())
                    .expectedOutput(caooDto.expectedOutput())
                    .committeeInCharges(caooDto.committeeInCharges())
                    .iopp(savedIoop)
                    .build();
            caoos.add(caoo);
        }
        this.caooRepository.saveAll(caoos);
        return "Institutional Outreach Project Proposal Submitted";
    }
}
