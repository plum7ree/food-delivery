package com.example.gateway.controller;

import com.example.gateway.data.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {


//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody Customer customer) {
//        Customer savedCustomer = null;
//        ResponseEntity response = null;
//        try {
//            String hashPwd = passwordEncoder.encode(customer.getPwd());
//            customer.setPwd(hashPwd);
//            savedCustomer = customerRepository.save(customer);
//            if (savedCustomer.getId() > 0) {
//                response = ResponseEntity
//                        .status(HttpStatus.CREATED)
//                        .body("Given user details are successfully registered");
//            }
//        } catch (Exception ex) {
//            response = ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An exception occured due to " + ex.getMessage());
//        }
//        return response;
//    }
    @GetMapping("/index")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
        }

        return "index";
    }
    @PostMapping("/call")
    public String callDriver(Model model) {

//        SELECT us.id, us.userId, us.status, us.createdAt
//        FROM user_status us
//        JOIN (
//            SELECT userId, MAX(createdAt) AS latestCreatedAt
//            FROM user_status
//            GROUP BY userId
//        ) latest ON us.userId = latest.userId AND us.createdAt = latest.latestCreatedAt
//        WHERE us.userId = 1;



        // 사용자 정보를 가져오는 로직 (예: 세션, 데이터베이스 등)
        User user = getUserFromSession();

        // 운전자 호출 로직 처리
        user.setSearchingDriver(true);
        // 호출 요청 처리 로직 추가 (예: 데이터베이스 업데이트, 외부 API 호출 등)

        // 모델에 사용자 정보 추가
        model.addAttribute("user", user);

        return "index";
    }

    @PostMapping("/cancel")
    public String cancelCall(Model model) {
        // 사용자 정보를 가져오는 로직 (예: 세션, 데이터베이스 등)
        User user = getUserFromSession();

        // 호출 취소 로직 처리
        user.setSearchingDriver(false);
        // 호출 취소 처리 로직 추가 (예: 데이터베이스 업데이트, 외부 API 호출 등)

        // 모델에 사용자 정보 추가
        model.addAttribute("user", user);

        return "index";
    }

    // 사용자 정보를 세션에서 가져오는 메소드 (예시)
    private User getUserFromSession() {
        // 실제 구현에서는 세션에서 사용자 정보를 가져오는 로직을 구현해야 합니다.
        // 예: HttpSession에서 사용자 정보 가져오기
        // 또는 Spring Security를 사용하는 경우 Authentication 객체에서 사용자 정보 가져오기
        return new User();
    }
}