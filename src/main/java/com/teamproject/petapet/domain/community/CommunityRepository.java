package com.teamproject.petapet.domain.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.List;

/**
 * 박채원 22.10.01 작성
 */
public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Modifying
    @Transactional
    @Query("update Community c set c.communityReport = c.communityReport + 1 where c.communityId =:communityId")
    void addCommunityReport(Long communityId);

    @Query(value = "select count(*) from Community c where date_format(modifiedDate,'%Y-%m-%d') = curdate()", nativeQuery = true)
    Long countTodayCommunity();

    @Query(value = "select count(*) from Community c where date_format(modifiedDate,'%Y-%m-%d') = curdate() " +
            "and communityCategory =:communityCategory", nativeQuery = true)
    Long countTodayCommunity(String communityCategory);

    @Transactional
    @Modifying
    @Query("update Community c set c.viewCount=c.viewCount+1 where c.communityId=:communityId")
    void viewCountPlus(@Param("communityId") Long communityId);

    Page<Community> findAllByCommunityCategory(String communityCategory, Pageable pageable);

    @Query(value = "select c from Community c where c.communityCategory not in ('공지사항')")
    Page<Community> getCommunityList(Pageable pageable);

    Page<Community> findAllByMemberMemberId(String memberId, Pageable pageable);

    Optional<Community> findByCommunityIdAndMemberMemberId(Long communityId, String memberId);

    @Query("select c from Community c where c.communityCategory = '공지사항'")
    List<Community> getNotice();
    @Modifying
    @Transactional
    @Query("update Community c set c.communityTitle =:title, c.communityContent =:content where c.communityId =:noticeId")
    void updateNotice(String title, String content, Long noticeId);

    @Query("select c from Community c where c.communityId=:searchContent and c.communityCategory not in ('공지사항')")
    Page<Community> searchCommunityIdList(Long searchContent,Pageable pageable);

    @Query("select c from Community c where c.communityTitle like %:searchContent% and c.communityCategory not in ('공지사항')")
    Page<Community> searchCommunityTitle(String searchContent,Pageable pageable);

    @Query("select c from Community c where (c.communityTitle like %:searchContent% or c.communityContent like %:searchContent%) and c.communityCategory not in ('공지사항')")
    Page<Community> searchCommunityTitleContentList(String searchContent,Pageable pageable);

    @Query("select c from Community c where c.member.memberId like %:searchContent% and c.communityCategory not in ('공지사항')" )
    Page<Community> searchMemberIdList(String searchContent,Pageable pageable);

    @Query("select c from Community c where c.communityId in (select distinct c1.community.communityId from Comment c1 where c1.member.memberId =:memberId)")
    Page<Community> getCommentWritingCommunityList(String memberId,Pageable pageable);

}
