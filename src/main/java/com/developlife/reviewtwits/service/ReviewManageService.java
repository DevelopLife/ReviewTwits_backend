package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.review.CannotHandleReviewException;
import com.developlife.reviewtwits.exception.review.ReviewNotFoundException;
import com.developlife.reviewtwits.mapper.ReviewMapper;
import com.developlife.reviewtwits.message.request.review.ReviewApproveRequest;
import com.developlife.reviewtwits.message.response.review.DetailShoppingMallReviewResponse;
import com.developlife.reviewtwits.message.response.review.ReviewApproveResponse;
import com.developlife.reviewtwits.repository.review.ReviewRepository;
import com.developlife.reviewtwits.type.review.ReviewStatus;
import com.developlife.reviewtwits.type.review.SortDirectionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-05-19
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewManageService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewApproveResponse reviewAuthorizeProcess(User user, ReviewApproveRequest request) {
        Review review = reviewRepository.findById(request.reviewId())
                .orElseThrow(()-> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));

        if(!review.getProject().getUser().equals(user)){
            throw new CannotHandleReviewException("해당 리뷰를 허가 및 분류할 권한이 없습니다.");
        }

        review.setStatus(ReviewStatus.valueOf(request.approveType()));
        reviewRepository.save(review);
        return ReviewApproveResponse.builder()
                .reviewId(review.getReviewId())
                .status(review.getStatus().name())
                .build();
    }
    @Transactional(readOnly = true)
    public List<DetailShoppingMallReviewResponse> searchReviewByInfo(User user, Long reviewId, int size, String status, String sort, String startDate, String endDate, String keyword) {
        Pageable pageable = PageRequest.of(0,size, SortDirectionFilter.getSortFromDirection(sort,"reviewId"));
        return reviewRepository.findReviewsBySearchInfo(user, reviewId, status, startDate, endDate, keyword, pageable);
    }
}