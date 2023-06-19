package com.developlife.reviewtwits.repository.review;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewMappingRepository {
    List<Review> findReviewsByProductUrl(String productURL);
    List<Review> findReviewsByProductUrlAndProjectIsNotNull(String productURL);
    Page<Review> findByReviewIdLessThan(long reviewId, Pageable pageable);

    List<Review> findReviewsByUser(User user);
    Page<Review> findReviewsByUser(User user, Pageable pageable);
    Page<Review> findByReviewIdLessThanAndUser(long reviewId, User user, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.status != 'DELETED' AND (r.productName LIKE %:searchKey% OR r.content LIKE %:searchKey%) ORDER BY r.reviewId DESC")
    List<Review> findByProductNameLikeOrContentLike(@Param("searchKey")String searchKey, Pageable pageable);
}

