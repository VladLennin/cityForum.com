package cityForum.services;

import cityForum.entity.User;
import cityForum.enums.Roles;
import cityForum.enums.Sex;
import cityForum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    public String registryNewUser(String login, String password1, String password2, String email, String city, String sex, String birthdayStr) {

        if (userRepo.findByLogin(login) == null) {
            Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])[a-zA-Z0-9]{8,}");
            Matcher matcher = pattern.matcher(password1);
            if (matcher.find() && password1.equals(password2)) {
                if (userRepo.findByEmail(email) == null) {
                    User user = new User();
                    user.setEmail(email);
                    user.setLogin(login);
                    user.setPassword(password1);
                    if (sex.equals("0")) {
                        user.setSex(Sex.MALE);
                    } else if (sex.equals("1")) {
                        user.setSex(Sex.FEMALE);
                    }
                    if (!birthdayStr.isEmpty()) {
                        String[] temp = new String[0];
                        LocalDate birthday = LocalDate.of(1, 1, 1);
                        try {
                            temp = birthdayStr.split("-");
                            birthday = LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            return e.getMessage();
                        }

                        user.setCity(city);
                        if (login.equals("Admin")) {
                            user.setRole(Roles.ADMIN);
                        } else {
                            user.setRole(Roles.USER);
                        }

                        user.setImage("Default");
                        user.setBirthday(birthday);
                        user.setName("none");
                        user.setSurname("none");
                        userRepo.save(user);
                        return "Registration successful!";

                    } else {
                        return "User must have age!";
                    }
                } else {
                    return "User with this email already exist!";
                }
            } else {
                return "Incorrectly password!";
            }

        } else {
            return "User with this login already exist!";
        }
    }

    public String editUser(MultipartFile file, String newLogin,
                           String newPassword1, String newPassword2,
                           String newEmail, String newName,
                           String newSurname,
                           String newBirthday, String newSex,
                           String newCity,
                           User user,
                           Roles role
    ) throws IOException {
        String errors = "";

        if (file != null && file.getSize() != 0) {
            user.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        } else if (file == null) {
            user.setImage("Default");
        }

        if (!newPassword1.equals(newPassword2)) {
            errors += "Passwords aren`t equals!\n";
        }

        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])[a-zA-Z0-9]{8,}");
        Matcher matcher = pattern.matcher(newPassword1);
        if (!matcher.find()) {
            errors += "Your password does not match\n";
        }

        if (!userRepo.findByLogin(newLogin).equals(user) && userRepo.findByLogin(newLogin) != null) {
            errors += "User with this login already exist!\n";
        }
        if (newLogin.isEmpty()) {
            errors += "You must fill field login!";
        }

        if (!userRepo.findByEmail(newEmail).equals(user) && userRepo.findByEmail(newEmail) != null) {
            errors += "User with this email already exist!\n";
        }
        if (newLogin.isEmpty()) {
            errors += "You must fill field email!";
        }

        if (!newBirthday.equals("")) {
            String[] temp = newBirthday.split("-");
            try {
                user.setBirthday(LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                errors += e.getMessage() + "\n";
            }
        }

        if (newSex.equals("0")) {
            user.setSex(Sex.MALE);
        } else if (newSex.equals("1")) {
            user.setSex(Sex.FEMALE);
        }

        if (errors.equals("")) {
            user.setName(newName);
            user.setSurname(newSurname);
            user.setLogin(newLogin);
            user.setPassword(newPassword1);
            user.setEmail(newEmail);
            user.setCity(newCity);
            user.setRole(role);
            userRepo.save(user);
            return "Data successful updated";
        } else {
            return errors;
        }
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepo.findByLogin(login);
    }
}