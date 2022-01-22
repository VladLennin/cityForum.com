package cityForum;

import cityForum.entity.Cities;
import cityForum.entity.User;
import cityForum.enums.Roles;
import cityForum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class StartupHousekeeper {

    @Autowired
    UserRepository userRepo;

    @EventListener(ContextRefreshedEvent.class)
    void contextRefreshedEvent() throws IOException {
        try {
                String inputCountries = new Scanner(new URL("https://api.hh.ru/areas/5").openStream(), "UTF-8").useDelimiter("\\A").next();

            PrintWriter writer = new PrintWriter("cities.txt", "UTF-8");
            writer.println(inputCountries);
            writer.close();


            Pattern pattern = Pattern.compile("\"name\":\"([А-я-() ]*)");
            Matcher mather = pattern.matcher(inputCountries);
            while (mather.find()) {
                Cities.cities.add(mather.group(1));
            }
            Cities.cities = (ArrayList<String>)  Cities.cities.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
        } catch (Exception e) {
            String fileName = "countries.txt";
            String inputCountries = Files.lines(Paths.get(fileName)).reduce("", String::concat);
            Pattern pattern = Pattern.compile("\"name\":\"([А-я-() ]*)");
            Matcher mather = pattern.matcher(inputCountries);
            while (mather.find()) {
                Cities.cities.add(mather.group(1));
            }
            Cities.cities = (ArrayList<String>)  Cities.cities.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
        }
        if (userRepo.findByLogin("Admin") != null) {
            User user = userRepo.findByLogin("Admin");
            user.setRole(Roles.ADMIN);
            userRepo.save(user);
        }


    }
}
