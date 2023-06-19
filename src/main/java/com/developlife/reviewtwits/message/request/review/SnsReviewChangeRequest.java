package com.developlife.reviewtwits.message.request.review;

import com.developlife.reviewtwits.message.annotation.file.ImageFiles;
import com.developlife.reviewtwits.message.annotation.review.DeleteFileName;
import com.developlife.reviewtwits.message.annotation.review.MultipartInteger;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-04-03
 */
public record SnsReviewChangeRequest(
        @Nullable
        @Size(message = "리뷰내용은 10자 이상이어야 합니다.",min = 10)
        String content,
        @Nullable
        @MultipartInteger
        String score,
        @Nullable
        @Size(message= "제품 이름이 입력되지 않았습니다.", min = 1)
        String productName,
        @ImageFiles
        List<MultipartFile> multipartImageFiles,
        @DeleteFileName
        List<String> deleteFileList
) {
}