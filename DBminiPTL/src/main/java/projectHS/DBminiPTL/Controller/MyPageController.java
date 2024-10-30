package projectHS.DBminiPTL.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projectHS.DBminiPTL.Common.Session;
import projectHS.DBminiPTL.DAO.Acc_InfoDAO;
import projectHS.DBminiPTL.DAO.MyPageDAO;
import projectHS.DBminiPTL.VO.Acc_InfoVO;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/main/customer/myPage")
public class MyPageController {

    @Autowired
    private MyPageDAO myPageDAO;

    @Autowired
    private Acc_InfoDAO accInfoDAO;

    // My Page View
    @GetMapping
    public String mypageView() {
        return "thymeleaf/myPage"; // Main template for My Page
    }

    @GetMapping("/accInfoUpdate")
    public String showUpdateForm(Model model) {
        String userId = Session.loggedInUserId; // 세션으로 부터 로그인 ID 값 받아서 설정
        if (userId == null) {
            model.addAttribute("alertMessage", "아이디나 비밀번호가 맞지 않습니다. 메인페이지로 이동합니다.");
            return "redirect:/main"; // 만일 존재하지 않으면 main으로 되돌려보내기
        }

        List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect(); // Retrieve all user info
        Acc_InfoVO accInfo = accInfoList.stream()
                .filter(info -> userId.equals(info.getUserId()))
                .findFirst()
                .orElse(null); // Find specific user by ID

        if (accInfo == null) {
            model.addAttribute("error", "User not found.");
            return "error"; // Return an error page if the user is not found
        }

        model.addAttribute("accInfoVO", accInfo); // Bind user info to the model
        return "thymeleaf/accInfoUpdate"; // Return the update form view
    }


    @PostMapping("/accInfoUpdate")
    public String updateUserInfo(@ModelAttribute("accInfoVO") Acc_InfoVO accInfoVO, Model model) {
        boolean isSuccess = false; // false 를 초기값으로 설정
        try {
            List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect();
            String sessionUserId = Session.loggedInUserId;

            // 회원 정보 업데이트 해줄 메서드 호출
            isSuccess = myPageDAO.membUpdate(accInfoVO, sessionUserId, accInfoList);
        } catch (IllegalArgumentException e) {
            // 특정 validation exeception 호출
            model.addAttribute("error", e.getMessage());
            isSuccess = false; // 에러 발생시 false로 설정
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            isSuccess = false; // 에러 발생시 false로 설정
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            isSuccess = false; // 에러 발생시 false로 설정
        }

        // Add success or failure to the model
        model.addAttribute("isSuccess", isSuccess);
        return "thymeleaf/accUpdateResult"; // Ensure this points to the correct Thymeleaf template
    }

    @GetMapping("/accInfoDelete")
    public String showDeleteForm(Model model) {
        String userId = Session.loggedInUserId; // 세션으로 부터 로그인 ID 값 받아서 설정
        if (userId == null) {
            model.addAttribute("alertMessage", "아이디나 비밀번호가 맞지 않습니다. 메인페이지로 이동합니다.");
            return "redirect:/main"; // 만일 존재하지 않으면 main으로 되돌려보내기
        }

        List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect(); // Retrieve all user info
        Acc_InfoVO accInfo = accInfoList.stream()
                .filter(info -> userId.equals(info.getUserId()))
                .findFirst()
                .orElse(null); // Find specific user by ID

        if (accInfo == null) {
            model.addAttribute("error", "User not found.");
            return "error"; // Return an error page if the user is not found
        }

        model.addAttribute("accInfoVO", accInfo); // Bind user info to the model
        return "thymeleaf/accDelete"; // Return the update form view
    }

    @PostMapping("/accInfoDelete")
    public String deleteUserInfo(@RequestParam("userId") String userId, Model model) {
        Acc_InfoVO accInfoVO = new Acc_InfoVO();
        accInfoVO.setUserId(userId); // Set the user ID from the request

        boolean isSuccess = false; // false to initialize
        try {
            List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect();
            String sessionUserId = Session.loggedInUserId;

            // Call the method to delete the user information
            isSuccess = myPageDAO.membDelete(accInfoVO, sessionUserId, accInfoList);
        } catch (Exception e) {
            System.err.println("Error during deletion: " + e.getMessage());
        }

        // Add success or failure to the model
        model.addAttribute("isSuccess", isSuccess);
        return "thymeleaf/accDeleteRst"; // Ensure this points to the correct Thymeleaf template
    }
}
