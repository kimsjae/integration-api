package shop.mtcoding.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import shop.mtcoding.blog._core.utils.JwtUtil;
import shop.mtcoding.blog.board.BoardRequest;
import shop.mtcoding.blog.reply.ReplyRequest;
import shop.mtcoding.blog.user.SessionUser;
import shop.mtcoding.blog.user.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 1. 통합테스트 (스프링의 모든 빈을 IoC에 등록하고 테스트 하는 것)
 * 2. 배포직전 최종테스트
 */

@Transactional
@AutoConfigureMockMvc // MockMvc IoC 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 모든 빈 IoC 로드
public class ReplyControllerTest extends MyRestDoc{

    private ObjectMapper om = new ObjectMapper();

    private static String jwt;

    @BeforeAll
    public static void setUp() {
        jwt = JwtUtil.create(
                User.builder()
                        .id(1)
                        .username("ssar")
                        .password("1234")
                        .email("ssar@nate.com")
                        .build());
    }

    @Test
    public void save_suc_test() throws Exception {

        // given
        ReplyRequest.SaveDTO reqDTO = new ReplyRequest.SaveDTO();
        reqDTO.setBoardId(1);
        reqDTO.setComment("추가댓글");
        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/api/replies")
                        .header("Authorization", jwt)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        ResultActions actions2 = mvc.perform(
                get("/api/boards/" + reqDTO.getBoardId() + "/detail")
                        .header("Authorization", "Bearer " + jwt)
        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("작성한 댓글 : " + respBody);

        String respBody2 = actions2.andReturn().getResponse().getContentAsString();
        System.out.println("댓글을 작성한 게시글 : " + respBody2);

//        int statusCode = actions.andReturn().getResponse().getStatus();
//        System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.comment").value("추가댓글"));
        actions2.andExpect(jsonPath("$.body.replies[0].comment").value("추가댓글"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void save_fail_test() throws Exception {

        // given
        ReplyRequest.SaveDTO reqDTO = new ReplyRequest.SaveDTO();
        reqDTO.setBoardId(6);
        reqDTO.setComment("추가댓글");
        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/api/replies")
                        .header("Authorization", jwt)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );


        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("작성한 댓글 : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("없는 게시글에 댓글을 작성할 수 없어요"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
