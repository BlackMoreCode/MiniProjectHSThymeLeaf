package projectHS.DBminiPTL.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectHS.DBminiPTL.DAO.Acc_InfoDAO;
import projectHS.DBminiPTL.DAO.MyPageDAO;
import projectHS.DBminiPTL.VO.Acc_InfoVO;

import java.security.Principal;
import java.util.List;

@Controller
public class MyPageController {

    @Autowired
    private MyPageDAO myPageDAO;

    @Autowired
    private Acc_InfoDAO accInfoDAO; // Acc_InfoDAO를 주입처리

    @GetMapping("/updateUserForm")
    public String showUpdateForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Redirect to login if user is not authenticated
        }

        String userId = principal.getName(); // Get the logged-in user's ID
        List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect(); // Retrieve user info from DAO
        model.addAttribute("accInfoVO", accInfoList); // Bind user info to the model
        return "membUpdateForm"; // Return the update form view
    }

    @PostMapping("/updateUser")
    public String updateUserInfo(@ModelAttribute("accInfoVO") Acc_InfoVO accInfoVO, Model model) {
        // Acc_InfoDAO로서 부터 기존 계정 정보들 받기
        List<Acc_InfoVO> accInfoList = accInfoDAO.Acc_InfoSelect(); // 여기서 계정 정보들 받아오기

        try {
            myPageDAO.membUpdate(accInfoVO, accInfoList);
            model.addAttribute("message", "회원 정보 업데이트 성공.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // 에러메세지 보이기?
        }
        return "redirect:/loginPage"; // 임시처리. 로그인 페이지로 리다이렉트 처리해야할듯?
    }

/*    @GetMapping("/delteUserForm")
    public String showDeleteForm(Model model) {

    }*/

}
