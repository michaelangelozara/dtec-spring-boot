package com.DTEC.Document_Tracking_and_E_Clearance.club;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
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
    public String assignClubOfficer(int clubId, int studentId) {
        var club = this.clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not Found"));
        var student = this.userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        // TODO this will be implemented
//        // check if there's no student officer yet
//        for(var memberRole : club.getMemberRoles()){
//            if(memberRole.getRole().equals(Role.STUDENT_OFFICER)){
//                memberRole.setRole(Role.STUDENT);
//                this.memberRoleRepository.save(memberRole);
//            }
//        }
//
//        for(var memberRole : club.getMemberRoles()){
//            if(memberRole.getUser().getId().equals(student.getId())){
//                memberRole.setRole(Role.STUDENT_OFFICER);
//                this.memberRoleRepository.save(memberRole);
//                break;
//            }
//        }

        return "";
    }
}
