package io.github.czjena.local_trade.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.github.czjena.local_trade.controller.ReviewController;
import io.github.czjena.local_trade.model.Review;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.request.ReviewRequestDto;
import io.github.czjena.local_trade.response.ReviewResponseDto;
import io.github.czjena.local_trade.service.JwtService;
import io.github.czjena.local_trade.service.ReviewService;
import io.github.czjena.local_trade.service.TradeService;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
public class ReviewControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private ReviewResponseDto reviewResponseDto;
    private List<ReviewResponseDto> reviewResponseDtoList;
    private Trade  trade;
    private UserDetails userDetails;



    @BeforeEach
    public void setup() {
        reviewResponseDto = new ReviewResponseDto(5,"good", UUID.randomUUID());
        reviewResponseDtoList = new ArrayList<>();
        for(int i = 0; i<5; i++){
            reviewResponseDtoList.add(reviewResponseDto);
        }

        trade = new Trade();
        trade.setTradeId(reviewResponseDto.reviewId());
        userDetails = mock(UserDetails.class);

    }
    @Test
    @WithMockUser("test@test.com")
    public void getAllMyReviewsControllerTest_returnsOk() throws Exception {

        when(reviewService.getAllMyReviews(any(UserDetails.class))).thenReturn(reviewResponseDtoList);

        mockMvc.perform(get("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("good"))
                .andExpect(jsonPath("$[0].reviewId").value(reviewResponseDto.reviewId().toString()));



    }
    @Test
    public void getAllMyReviewsControllerTestUserNotLoggedIn_returnsUnauthorized() throws Exception {

        mockMvc.perform(get("/reviews")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("test@test.com")
    public void getAllMyReviewsControllerTestButReturnsNothing() throws Exception {
        when(reviewService.getAllMyReviews(any(UserDetails.class))).thenReturn(List.of());

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test@test.com")
    public void postReview_thenReviewIsPosted() throws Exception {
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5,"good");

        when(reviewService.postReview(any(UserDetails.class),eq(trade.getTradeId()),any(ReviewRequestDto.class))).thenReturn(reviewResponseDto);

        String reviewJson = objectMapper.writeValueAsString(reviewRequestDto);

        mockMvc.perform(post("/reviews/"+ trade.getTradeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("test@test.com")
    public void postReviewWithBadData_thenReturnsBadRequest() throws Exception {
        var reviewRequestDto = new ReviewRequestDto(99,"good");

        String reviewJson = objectMapper.writeValueAsString(reviewRequestDto);
        mockMvc.perform(post("/reviews/"+ trade.getTradeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

    }
    @Test
    public void postReviewWithBadUser_thenReturnsUnauthorized() throws Exception {

        mockMvc.perform(post("/reviews/"+ trade.getTradeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

    }


}
