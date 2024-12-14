package com.DTEC.Document_Tracking_and_E_Clearance.club;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ClubConfiguration {

    private final ClubRepository clubRepository;

    public ClubConfiguration(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Bean(name = "club-initializer")
    public ApplicationRunner applicationRunner() {
        return args -> {
            int count = this.clubRepository.countRow();
            if (count == 0) {
                List<String[]> clubNames = new ArrayList<>();
                clubNames.add(new String[]{"Computer Science Club", "CSC", "DEPARTMENT"});
                clubNames.add(new String[]{"Mechanical Engineering Society", "MES", "DEPARTMENT"});
                clubNames.add(new String[]{"Art and Design Collective", "ADC", "SOCIAL"});
                clubNames.add(new String[]{"Literature Enthusiasts", "LE", "SOCIAL"});
                clubNames.add(new String[]{"Electrical Engineering Club", "EEC", "DEPARTMENT"});
                clubNames.add(new String[]{"Biology Research Group", "BRG", "DEPARTMENT"});
                clubNames.add(new String[]{"Photography Club", "PC", "SOCIAL"});
                clubNames.add(new String[]{"Physics Society", "PS", "DEPARTMENT"});
                clubNames.add(new String[]{"Chemistry Club", "CC", "DEPARTMENT"});
                clubNames.add(new String[]{"Drama Club", "DC", "SOCIAL"});
                clubNames.add(new String[]{"Business Club", "BC", "SOCIAL"});
                clubNames.add(new String[]{"Mathematics Club", "MC", "DEPARTMENT"});
                clubNames.add(new String[]{"Cultural Exchange Society", "CES", "SOCIAL"});
                clubNames.add(new String[]{"Music Club", "MC", "SOCIAL"});
                clubNames.add(new String[]{"Engineering Society", "ES", "DEPARTMENT"});
                clubNames.add(new String[]{"Environmental Awareness Group", "EAG", "SOCIAL"});
                clubNames.add(new String[]{"Law and Debate Society", "LDS", "SOCIAL"});
                clubNames.add(new String[]{"Human Resources Club", "HRC", "DEPARTMENT"});
                clubNames.add(new String[]{"Finance Society", "FS", "SOCIAL"});
                clubNames.add(new String[]{"History and Politics Forum", "HPF", "SOCIAL"});


                List<Club> clubs = new ArrayList<>();
                for(var name : clubNames){
                    if(name[2].equals("DEPARTMENT")){
                        clubs.add(toClub(name[0], name[1], Type.DEPARTMENT));
                    }else{
                        clubs.add(toClub(name[0], name[1], Type.SOCIAL));
                    }
                }

                this.clubRepository.saveAll(clubs);
            }
        };
    }

    private Club toClub(String name, String shortName, Type type) {
        return Club.builder()
                .name(name)
                .shortName(shortName)
                .type(type)
                .build();
    }
}
