package com.teamproject.petapet.web.admin;

import com.teamproject.petapet.domain.report.Report;
import com.teamproject.petapet.web.Inquired.dto.InquiredFAQDTO;
import com.teamproject.petapet.web.Inquired.service.InquiredService;
import com.teamproject.petapet.web.community.service.CommunityService;
import com.teamproject.petapet.web.member.service.MemberService;
import com.teamproject.petapet.web.product.service.ProductService;
import com.teamproject.petapet.web.report.dto.ReportProductDTO;
import com.teamproject.petapet.web.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 박채원 22.10.09 작성
 */

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final InquiredService inquiredService;
    private final ProductService productService;
    private final CommunityService communityService;
    private final MemberService memberService;
    private final ReportService reportService;

    @GetMapping("/adminPage")
    public String adminPage(Model model){

        model.addAttribute("FAQ", inquiredService.getFAQ());
        model.addAttribute("otherInquiry", inquiredService.getOtherInquiries());
        model.addAttribute("communityReport", reportService.getReportCommunityList());
        model.addAttribute("memberReport", reportService.getReportMemberList());
        model.addAttribute("productReport", reportService.getReportProductList());
        model.addAttribute("product", productService.getProductList());
        model.addAttribute("community", communityService.getProductList());
        model.addAttribute("member", memberService.getMemberList());


        return "/admin/adminMain";
    }

    @GetMapping("/registerFAQ")
    public String registerFAQForm(){
        return "/admin/registerFAQ";
    }

    @PostMapping("/registerFAQ")
    public String registerFAQ(InquiredFAQDTO inquiredFAQDTO){
        inquiredService.registerFAQ(inquiredFAQDTO);
        return "redirect:/admin/adminPage";
    }

    @GetMapping("/updateFAQForm/{FAQId}")
    public String getOneFAQ(@PathVariable("FAQId") Long FAQId, Model model){
        model.addAttribute("FAQInfo",inquiredService.getOneFAQ(FAQId));
        return "/admin/updateFAQ";
    }

    @PostMapping("/updateFAQ")
    public String updateFAQ(InquiredFAQDTO inquiredFAQDTO){
        inquiredService.updateFAQ(inquiredFAQDTO);
        return "redirect:/admin/adminPage";
    }

    @GetMapping("/deleteFAQ/{FAQId}")
    public String deleteFAQ(@PathVariable("FAQId") Long FAQId){
        inquiredService.deleteFAQ(FAQId);
        return "redirect:/admin/adminPage";
    }

    @GetMapping("/registerProduct")
    public String registerProductForm(){
        return "/admin/registerProduct";
    }

    @PostMapping("/registerProduct")
    public String registerProduct(){
        return "redirect:/admin/adminPage";
    }

    @ResponseBody
    @RequestMapping(value = "/updateProductStatus/{productId}", method = RequestMethod.GET)
    public void updateProductStatus(@RequestParam Map<String, Object> map, @PathVariable("productId") Long productId){
        productService.updateProductStatus((String)map.get("status"), Long.valueOf((String) map.get("stock")), productId);
    }

    @GetMapping("/deleteCommunity/{communityId}")
    public void deleteCommunity(@PathVariable("communityId") Long communityId){
        communityService.deleteCommunity(communityId);
    }

    @ResponseBody
    @GetMapping("/disabledMember/{memberId}")
    public void disabledMember(@PathVariable("memberId") String memberId){
        memberService.updateMemberStopDate(memberId);
    }

    @GetMapping("/deleteMember/{memberId}")
    public void deleteMember(@PathVariable("memberId") String memberId){
        memberService.deleteMember(memberId);
    }

    @ResponseBody
    @GetMapping("/acceptCommunityReport/{reportId}")
    public void acceptCommunityReport(@PathVariable("reportId") Long reportId, @RequestParam("communityId") Long communityId){
        communityService.addCommunityReport(communityId);
        reportService.setResponseStatusCommunity(reportId);
    }

    @ResponseBody
    @GetMapping("/acceptMemberReport/{reportId}")
    public void acceptMemberReport(@PathVariable("reportId") Long reportId, @RequestParam("memberId") String memberId){
        memberService.addMemberReport(memberId);
        reportService.setResponseStatusCommunity(reportId);
    }

    @ResponseBody
    @GetMapping("/acceptProductReport/{reportId}")
    public void acceptProductReport(@PathVariable("reportId") Long reportId, @RequestParam("productId") Long productId){
        productService.addProductReport(productId);
        reportService.setResponseStatusCommunity(reportId);
    }

    @ResponseBody
    @GetMapping("/getGenderList")
    public int[] getGenderList(){
        return memberService.getGenderList();
    }

    @ResponseBody
    @GetMapping("/getAgeList")
    public List<Integer> getAgeList(){
        return memberService.getAgeList();
    }

    @ResponseBody
    @GetMapping("/setOutOfStock")
    public void setOutOfStock(@RequestParam(value="productIdList[]") List<String> productIdList){
        productService.updateProductStatusOutOfStock(productIdList);
    }

    @ResponseBody
    @GetMapping("/getReportReason/{id}/{type}")
    public HashMap<String, ReportProductDTO> getReportReason(@PathVariable("id") Long id, @PathVariable("type") String type){
        HashMap<String, ReportProductDTO> reportProduct = new HashMap<String, ReportProductDTO>();
        reportProduct.put("report",reportService.getOneReportProduct(id, type));
        return reportProduct;
    }

    @ResponseBody
    @PostMapping("/refuseReport/{reportId}")
    public void refuseReport(@PathVariable("reportId") Long reportId){
        reportService.refuseReport(reportId);
    }
}