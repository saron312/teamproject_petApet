
package com.teamproject.petapet.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class testController {

    @GetMapping("/")
    public String test(Principal principal, Model model){
        if(Principal.class.isInstance(principal)){
            String memberId  = principal.getName();
            log.info("MemberId={}",memberId );
            log.info("MemberId={}",memberId );
            log.info("MemberId={}",memberId );
            model.addAttribute("memberId", memberId);
            return "index";
        }
        return "index";}

}