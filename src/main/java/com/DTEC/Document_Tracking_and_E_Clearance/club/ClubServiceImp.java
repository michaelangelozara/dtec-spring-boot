package com.DTEC.Document_Tracking_and_E_Clearance.club;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubServiceImp implements ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;
    private final CourseRepository courseRepository;
    private final MemberRoleRepository memberRoleRepository;

    public ClubServiceImp(UserRepository userRepository, ClubRepository clubRepository, ClubMapper clubMapper, CourseRepository courseRepository, MemberRoleRepository memberRoleRepository) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.clubMapper = clubMapper;
        this.courseRepository = courseRepository;
        this.memberRoleRepository = memberRoleRepository;
    }

    @Override
    public ClubResponseDto getClubById(int id) {
        var club = this.clubRepository.findById(id).orElse(null);
        if (club == null)
            throw new ResourceNotFoundException("Club not Found");
        return this.clubMapper.toClubInformationResponseDto(club);
    }

    @Override
    public List<ClubResponseDto> getAllClubs(int s, int e) {
        Pageable pageable = PageRequest.of(s, e);
        Page<Club> clubs = this.clubRepository.findAll(pageable);
        if (clubs.isEmpty())
            throw new ResourceNotFoundException("No Club Added yet");

        return this.clubMapper.toClubInformationResponseDtoList(clubs.getContent());
    }

    @Override
    public void updateLogo(String image, int clubId) {
        var club = this.clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not Found"));

        club.setLogo(image);
        this.clubRepository.save(club);
    }
}
