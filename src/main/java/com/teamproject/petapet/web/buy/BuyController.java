package com.teamproject.petapet.web.buy;


import com.teamproject.petapet.domain.buy.Buy;
import com.teamproject.petapet.domain.cart.Cart;
import com.teamproject.petapet.domain.member.Member;
import com.teamproject.petapet.web.buy.dto.BuyDTO;
import com.teamproject.petapet.web.buy.service.Buyservice;
import com.teamproject.petapet.web.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/buy")
@RequiredArgsConstructor
public class BuyController {

    private final Buyservice buyService;
    private final MemberService memberService;

    @GetMapping()
    public String myBuy(Principal principal,
                         HttpServletRequest request,
                         HttpSession httpSession,
                         Model model){

        if(Principal.class.isInstance(principal)) {
            String memberId = principal.getName();
            httpSession.setAttribute("loginMember", memberService.findOne(memberId).get());
            HttpSession session = request.getSession(false);
            Member loginMemberSession = (Member) session.getAttribute("loginMember");
            String loginMember = loginMemberSession.getMemberId();

            List<Buy> buyList = buyService.findAll(loginMember);
            model.addAttribute("buyList", buyList);
            return "mypage/buy";
        }

        return "login";
    }
}
