package projectHS.DBminiPTL.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectHS.DBminiPTL.DAO.Inv_OrderDAO;
import projectHS.DBminiPTL.VO.InvVO;
import projectHS.DBminiPTL.VO.Inv_OrderVO;
import java.util.List;

@Controller
@RequestMapping("/main/hq")

public class Inv_OrderController {

    // Inv_OrderDAO 주입
    @Autowired
    private Inv_OrderDAO ioDAO;

    public Inv_OrderController(Inv_OrderDAO ioDAO) {
        this.ioDAO = ioDAO;
    }


    // 전체 메뉴 출력
    @GetMapping("/hqInvMngr")
    public String Inv_OrderSelect(Model model) {
        List<Inv_OrderVO> ioVO = ioDAO.Inv_OrderSelect();
        model.addAttribute("invMenuList", ioVO);
        return "thymeleaf/hqInvMngr";
    }

    // 수정
    @PostMapping("/hqInvMngr/update")
    public String Inv_OrderUpdate(Inv_OrderVO ioVO) {
        ioDAO.Inv_OrderUpdate(ioVO);
        return "redirect:/main/hq/hqInvMngr";
    }

    // 수정버튼 클릭시 editMode
    @PostMapping("/hqInvMngr/editMode")
    public String editMode(@RequestParam String menuName, Model model) {
        List<Inv_OrderVO> ioVO = ioDAO.Inv_OrderSelect();
        // 클릭한 메뉴 항목의 editMode를 true로 설정
        for (Inv_OrderVO item : ioVO) {
            if (item.getMenuName().equals(menuName)) {
                item.setEditMode(true);
            } else {
                item.setEditMode(false);
            }
        }
        model.addAttribute("invMenuList", ioVO);
        return "thymeleaf/hqInvMngr"; // 수정할 페이지 이름
    }

    // 삭제
    @PostMapping("/hqInvMngr/delete")
    public String Inv_OrderDelete(@RequestParam String menuName) {
        ioDAO.Inv_OrderDelete(menuName);
        return "redirect:/main/hq/hqInvMngr";
    }

    // 추가 버튼 클릭시 addMode
    @PostMapping("/hqInvMngr/addMode")
    public String addMode(@ModelAttribute Inv_OrderVO ioVO) {
        ioDAO.Inv_OrderInsert(ioVO);
        return "redirect:/main/hq/hqInvMngr"; // 수정할 페이지 이름
    }

}