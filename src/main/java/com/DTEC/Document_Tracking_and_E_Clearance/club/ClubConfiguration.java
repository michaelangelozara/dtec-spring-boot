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
                clubNames.add(new String[]{"PSITS", "PSITS", "DEPARTMENT"});
                clubNames.add(new String[]{"Finance", "Finance", "DEPARTMENT"});
                clubNames.add(new String[]{"JSWAP", "JSWAP", "DEPARTMENT"});
                clubNames.add(new String[]{"ICPEP.SE", "ICPEP.SE", "DEPARTMENT"});
                clubNames.add(new String[]{"PNSA", "PNSA", "DEPARTMENT"});
                clubNames.add(new String[]{"JHARA", "JHARA", "DEPARTMENT"});
                clubNames.add(new String[]{"JPMA", "JPMA", "DEPARTMENT"});
                clubNames.add(new String[]{"JPIA", "JPIA", "DEPARTMENT"});
                clubNames.add(new String[]{"UPSCISTS", "UPSCISTS", "DEPARTMENT"});
                clubNames.add(new String[]{"JMC", "JMC", "DEPARTMENT"});
                clubNames.add(new String[]{"PMC", "PMC", "DEPARTMENT"});

                clubNames.add(new String[]{"JEM", "JEM", "SOCIAL"});
                clubNames.add(new String[]{"CES", "CES", "SOCIAL"});
                clubNames.add(new String[]{"ASPA", "ASPA", "SOCIAL"});
                clubNames.add(new String[]{"PCC", "PCC", "SOCIAL"});
                clubNames.add(new String[]{"MSO", "MSO", "SOCIAL"});
                clubNames.add(new String[]{"YSLC", "YSLC", "SOCIAL"});
                clubNames.add(new String[]{"BLC", "BLC", "SOCIAL"});
                clubNames.add(new String[]{"KKB", "KKB", "SOCIAL"});
                clubNames.add(new String[]{"KARATE-DO", "KARATE-DO", "SOCIAL"});

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
