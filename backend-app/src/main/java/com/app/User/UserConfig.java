// package com.app.User;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class UserConfig {

//     @Bean
//     CommandLineRunner commandLineRunner(UserRepository userRepository) {
//         return args -> {
//             // Insert 10 dummy users
//             for (int i = 1; i <= 10; i++) {
//                 User user = new User();
//                 user.setEmail("user" + i + "@example.com");
//                 user.setPassword("password" + i);
//                 user.setFirstName("FirstName" + i);
//                 user.setLastName("LastName" + i);
//                 user.setRole("Role" + i);
//                 userRepository.save(user);
//             }
//         };
//     }
// }
