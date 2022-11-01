package com.teamproject.petapet.web.cart;

import com.teamproject.petapet.domain.cart.Cart;
import com.teamproject.petapet.domain.member.Member;
import com.teamproject.petapet.web.cart.dto.CartVO;
import com.teamproject.petapet.web.cart.service.CartService;
import com.teamproject.petapet.web.member.service.MemberService;
import com.teamproject.petapet.web.product.service.ProductService;
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
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;
    private final ProductService productService;

    /**
     * 회원 장바구니
     * @param principal
     * @param request
     * @param httpSession
     * @param model
     * @return
     * 추가할것 - 로그인한 회원 일치 검증 에러처리
     */
    @GetMapping()
    public String mycart(Principal principal,
                         HttpServletRequest request,
                         HttpSession httpSession,
                         Model model){

        if(Principal.class.isInstance(principal)) {
            String loginMember = checkMember(principal, request, httpSession);

            // 세션 유지되면 로그인으로 이동
            List<Cart> carts = cartService.findAll(loginMember);
            model.addAttribute("carts", carts);
            return "mypage/cart";
        }

        return "login";

    }

    // 상품 페이지 -> 장바구니 담기
    @ResponseBody
    @RequestMapping(value = "/add", method = { RequestMethod.POST }, produces = "application/json")
    public void productToCart(@RequestBody CartVO vo, Principal principal, HttpServletRequest request, HttpSession httpSession){

        String loginMember = checkMember(principal, request, httpSession);
        Long product = vo.getProduct();
        Long quantity = vo.getQuantity();
        Cart cart = new Cart(
                memberService.findOne(loginMember),
                productService.findOne(product),
                quantity);

        cartService.addCart(cart);

    }


    private String checkMember(Principal principal, HttpServletRequest request, HttpSession httpSession) {
        httpSession.setAttribute("loginMember", memberService.findOne(principal.getName()));
        HttpSession session = request.getSession(false);
        Member loginMemberSession = (Member) session.getAttribute("loginMember");
        String loginMember = loginMemberSession.getMemberId();
        return loginMember;
    }


}