package projectHS.DBminiPTL.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projectHS.DBminiPTL.Common.Session;
import projectHS.DBminiPTL.DAO.Acc_InfoDAO;
import projectHS.DBminiPTL.VO.Acc_InfoVO;

import java.util.List;

@Controller
@RequestMapping("/main")
public class Acc_InfoController {

    @Autowired
    private Acc_InfoDAO aiDAO;

    @GetMapping
    public String loginView(Model model) {
        model.addAttribute("aiVO", new Acc_InfoVO());
        return "thymeleaf/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("aiVO") Acc_InfoVO aiVO, Model model) {

        String userId = aiVO.getUserId();
        String userPw = aiVO.getUserPw();

        int authLevel = aiDAO.checkUserAuthLevel(userId, userPw);

        if (authLevel == 3) {
            // CUSTOMER 로그인 성공
            Session.loggedInUserId = userId; // Set user ID in session
            model.addAttribute("message", "CUSTOMER 로그인 성공!");
            return "redirect:/main/customer"; // Redirect to customer main page
        } else if (authLevel == 1) {
            // ADMIN 로그인 성공
            Session.loggedInUserId = userId; // Set user ID in session
            System.out.println(Session.loggedInUserId);
            Session.storeId = aiDAO.adminStore(Session.loggedInUserId);
            model.addAttribute("message", "ADMIN 로그인 성공!");
            return "redirect:/main/admin"; // Redirect to the admin main page
        } else if (authLevel == 2) {
            // HQ 로그인 성공
            Session.loggedInUserId = userId; // Set user ID in session
            model.addAttribute("message", "HQ 로그인 성공!");
            return "redirect:/main/hq"; // Redirect to the HQ main page
        } else {
            // 로그인 실패
            model.addAttribute("alertMessage", "아이디 또는 비밀번호를 확인해주세요."); // Add alert message
            return "thymeleaf/login"; // Redirect to login page with error
        }
    }

    @GetMapping("/customer")
    public String rdCustomerMain(Model model) {
        // 고객 메인 페이지에 필요한 데이터 추가
        return "thymeleaf/customerMain"; // 고객 메인 페이지 템플릿
    }

    @GetMapping("/admin")
    public String rdAdminMain(Model model) {
        // 관리자 메인 페이지에 필요한 데이터 추가
        return "thymeleaf/adminMain"; // 관리자 메인 페이지 템플릿
    }

    @GetMapping("/hq")
    public String rdHqMain(Model model) {
        // HQ 메인 페이지에 필요한 데이터 추가
        return "thymeleaf/hqMain"; // HQ 메인 페이지 템플릿
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("accountInfo", new Acc_InfoVO());
        return "thymeleaf/signup";
    }

    // 회원가입 처리 메소드
    @PostMapping("/signup")
    public String signup(@ModelAttribute Acc_InfoVO aiVO, Model model) {
        boolean isSuccess = aiDAO.Acc_InfoInsert(aiVO);
        model.addAttribute("isSuccess", isSuccess);
        return "thymeleaf/signupRst";
    }

    // 모든 회원 목록 조회
    @GetMapping("/select")
    public String selectAccInfo(Model model) {
        List<Acc_InfoVO> aiList = aiDAO.Acc_InfoSelect();
        model.addAttribute("accountInfo", aiList);
        return "thymeleaf/aiSelect";
    }

    // 로그인처리 후 CUSTOMER 메인 페이지로 이동
    @GetMapping("/customerMain")
    public String customerMain(@ModelAttribute Model model) {
        return "thymeleaf/customerMain";
    }

    // 로그인처리 후 ADMIN 메인 페이지로 이동
    @GetMapping("/adminMain")
    public String adminMain(@ModelAttribute Model model) {
        return "thymeleaf/adminMain";
    }

    // 로그인처리 후 HQ 메인 페이지로 이동
    @GetMapping("/hqMain")
    public String hqMain(@ModelAttribute Model model) {
        return "thymeleaf/hqMain";
    }

}
