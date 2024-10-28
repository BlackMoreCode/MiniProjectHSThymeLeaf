package projectHS.DBminiPTL.Controller;

import org.springframework.jdbc.core.JdbcTemplate;
import projectHS.DBminiPTL.DAO.Acc_InfoDAO;
import projectHS.DBminiPTL.VO.Acc_InfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/acc_info")
public class Acc_InfoController {

    private final Acc_InfoDAO accInfoDAO;

    public Acc_InfoController(Acc_InfoDAO accInfoDAO) {
        this.accInfoDAO = accInfoDAO;
    }
    // 로그인 페이지 요청 처리
    @GetMapping
    public String loginView(Model model) {
        model.addAttribute("aiVO", new Acc_InfoVO());
        return "thymeleaf/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute("aiVO") Acc_InfoVO aiVO, Model model) {

        String userId = aiVO.getUserId();
        String userPw = aiVO.getUserPw();

        int authLevel = accInfoDAO.checkUserAuthLevel(userId, userPw);

        if (authLevel == 3) {
            // CUSTOMER 로그인 성공
            model.addAttribute("message", "CUSTOMER 로그인 성공!");
            return "thymeleaf/customer/customerMain"; // 고객 메인 페이지로 리디렉션
        } else if (authLevel == 1) {
            // ADMIN 로그인 성공
            model.addAttribute("message", "ADMIN 로그인 성공!");
            return "thymeleaf/admin/adminMain"; // 관리자 메인 페이지로 리디렉션
        } else if (authLevel == 2) {
            // HQ 로그인 성공
            model.addAttribute("message", "HQ 로그인 성공!");
            return "thymeleaf/hq/hqMain"; // HQ 메인 페이지로 리디렉션
        } else {
            // 로그인 실패
            model.addAttribute("error", "아이디 또는 비밀번호를 확인해주세요.");
            return "thymeleaf/login"; // 로그인 페이지로 돌아감
        }
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/thymeleaf/signup")
    public String signupView(Model model) {
        model.addAttribute("accountInfo", new Acc_InfoVO());
        return "thymeleaf/signup";
    }

    // 회원가입 처리 메소드
    @PostMapping("/signup")
    public String signup(@ModelAttribute Acc_InfoVO aiVO) {

        // 회원가입 메소드 호출
        accInfoDAO.Acc_InfoInsert(aiVO);
        return "thymeleaf/signupSuccess";
    }

    // 모든 회원 목록 조회
    @GetMapping("/select")
    public String selectAccInfo(Model model) {
        List<Acc_InfoVO> aiList = accInfoDAO.Acc_InfoSelect();
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





    @GetMapping("/select2")
    public String selectAccInfo2(Model model) {
        List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect();
        model.addAttribute("accInfoList", accInfoList);
        return "thymeleaf/accInfoSelect";
    }

    @GetMapping("/insert")
    public String insertViewAccInfo(Model model) {
        model.addAttribute("accInfoVO", new Acc_InfoVO());
        return "thymeleaf/accInfoInsert";
    }

    @PostMapping("/insert")
    public String insertDBAccInfo(@ModelAttribute("accInfoVO") Acc_InfoVO accInfoVO) {
        accInfoDAO.Acc_InfoInsert(accInfoVO);
        return "thymeleaf/signupSuccess";
    }
}













/*
package projectHS.DBminiPTL.Controller;

import projectHS.DBminiPTL.DAO.Acc_InfoDAO;
import projectHS.DBminiPTL.VO.Acc_InfoVO;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/acc_info")
public class Acc_InfoController {


    @GetMapping("/select")
    public String selectAccInfo(Model model) {
        List<Acc_InfoVO> acivo = Acc_InfoDAO.Acc_InfoSelect();
        model.addAttribute("userId", acivo);
        return "thymeleaf/accInfoSelect";
    }


    @GetMapping("/insert")
    public String insertViewAccInfo(Model model){
        model.addAttribute("userId", new Acc_InfoVO());
        return "thymeleaf/accInfoInsert";
    }

    @PostMapping("/insert")
    public String insertDBAccInfo(@ModelAttribute("userId") Acc_InfoVO accInfoVO) {
        Acc_InfoDAO.Acc_InfoInsert(accInfoVO);
        return "thymeleaf/accInfoResult";
    }

}


*/
