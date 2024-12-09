package com.DTEC.Document_Tracking_and_E_Clearance.club;

import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.NoContentException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubServiceImp implements ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;
    private final CourseRepository courseRepository;

    public ClubServiceImp(UserRepository userRepository, ClubRepository clubRepository, ClubMapper clubMapper, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.clubMapper = clubMapper;
        this.courseRepository = courseRepository;
    }

    @Override
    public ClubResponseDto getClubById(int id) {
        var club = this.clubRepository.findById(id).orElse(null);
        if (club == null)
            throw new ResourceNotFoundException("Club not Found");
        return this.clubMapper.toClubResponseDto(club);
    }

    @Transactional
    @Override
    public String addClubForStudent(int clubId, int studentId) {
        var student = this.userRepository.findById(studentId).orElse(null);
        if (student == null)
            throw new ResourceNotFoundException("Student not Found");

        var club = this.clubRepository.findById(clubId).orElse(null);
        if (club == null)
            throw new ResourceNotFoundException("Club not Found");

        if (student.getSocialClub() != null)
            throw new ForbiddenException(student.getLastname() + ", " + student.getFirstName() + " has 2 Clubs already");

        student.setSocialClub(club);
        this.userRepository.save(student);
        return club.getName() + " is Added to " + student.getLastname() + ", " + student.getFirstName();
    }

    @Override
    public List<ClubResponseDto> getAllClubs() {
        var clubs = this.clubRepository.findAll();
        if (clubs.isEmpty())
            throw new NoContentException("No Club Added yet");

        return this.clubMapper.clubResponseDtoList(clubs);
    }

    @Override
    public String addClub(AddClubRequestDto dto) {
        if (dto.name().isEmpty())
            throw new BadRequestException("Club Name cannot be Blank");

        var club = this.clubMapper.toClub(dto);
        this.clubRepository.save(club);
        return "Club is Successfully Added";
    }
}
