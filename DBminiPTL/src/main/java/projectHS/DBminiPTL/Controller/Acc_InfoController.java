package projectHS.DBminiPTL.Controller;

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

    @Autowired
    public Acc_InfoController(Acc_InfoDAO accInfoDAO) {
        this.accInfoDAO = accInfoDAO;
    }

    @GetMapping("/select")
    public String selectAccInfo(Model model) {
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
        return "thymeleaf/accInfoResult";
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
