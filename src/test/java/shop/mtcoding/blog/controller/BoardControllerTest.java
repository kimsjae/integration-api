package shop.mtcoding.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.atn.ATNType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import shop.mtcoding.blog._core.utils.JwtUtil;
import shop.mtcoding.blog.board.BoardRequest;
import shop.mtcoding.blog.user.SessionUser;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 1. 통합테스트 (스프링의 모든 빈을 IoC에 등록하고 테스트 하는 것)
 * 2. 배포직전 최종테스트
 */

@Transactional
@AutoConfigureMockMvc // MockMvc IoC 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // 모든 빈 IoC 로드
public class BoardControllerTest extends MyRestDoc{

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
    public void index_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                get("/")

        );

        // eye
        String respBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("respBody : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body[0].id").value(4));
        actions.andExpect(jsonPath("$.body.length()").value(4));
        actions.andExpect(jsonPath("$.body[0].title").value("제목4"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void detail_suc_test() throws Exception {
        // given
        Integer id = 4;

        // when
        ResultActions actions = mvc.perform(
                get("/api/boards/" + id + "/detail")
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.title").value("제목4"));
        actions.andExpect(jsonPath("$.body.content").value("내용4"));
        actions.andExpect(jsonPath("$.body.replies[0].owner").value(false));
        actions.andExpect(jsonPath("$.body.replies[0].userId").value(2));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    public void detail_fait_test() throws Exception {
        // given
        Integer id = 6;

        // when
        ResultActions actions = mvc.perform(
                get("/api/boards/" + id + "/detail")
                        .header("Authorization", "Bearer " + jwt)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("게시글을 찾을 수 없습니다"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findOne_suc_test() throws Exception {
        // given
        Integer id = 4;

        // when
        ResultActions actions = mvc.perform(
                get("/api/boards/" + id)
                        .header("Authorization", "Bearer " + jwt)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.title").value("제목4"));
        actions.andExpect(jsonPath("$.body.content").value("내용4"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findOne_fail_test() throws Exception {
        // given
        Integer id = 6;

        // when
        ResultActions actions = mvc.perform(
                get("/api/boards/" + id)
                        .header("Authorization", "Bearer " + jwt)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

        // then
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("게시글을 찾을 수 없습니다"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void save_suc_test() throws Exception {

        // given
        BoardRequest.SaveDTO reqDTO = new BoardRequest.SaveDTO();
        reqDTO.setTitle("제목5");
        reqDTO.setContent("내용5");
        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/api/boards")
                        .header("Authorization", jwt)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

//        int statusCode = actions.andReturn().getResponse().getStatus();
//        System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.title").value("제목5"));
        actions.andExpect(jsonPath("$.body.content").value("내용5"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void save_fail_test() throws Exception {

        // given
        BoardRequest.SaveDTO reqDTO = new BoardRequest.SaveDTO();
//        reqDTO.setTitle("제목5");
        reqDTO.setContent("내용5");
        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/api/boards")
                        .header("Authorization", jwt)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

//        int statusCode = actions.andReturn().getResponse().getStatus();
//        System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(400));
        actions.andExpect(jsonPath("$.msg").value("제목은 공백일 수 없습니다 : title"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void update_suc_test() throws Exception {

        // given
        Integer boardId = 1;
        BoardRequest.UpdateDTO reqDTO = new BoardRequest.UpdateDTO();
        reqDTO.setTitle("제목1 수정");
        reqDTO.setContent("내용1 수정");
        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                put("/api/boards/" + boardId)
                        .header("Authorization", jwt)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

//        int statusCode = actions.andReturn().getResponse().getStatus();
//        System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.title").value("제목1 수정"));
        actions.andExpect(jsonPath("$.body.content").value("내용1 수정"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void update_fail_test() throws Exception {

        // given
        Integer boardId = 3;
        BoardRequest.UpdateDTO reqDTO = new BoardRequest.UpdateDTO();
        reqDTO.setTitle("제목3 수정");
        reqDTO.setContent("내용3 수정");
        String reqBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                put("/api/boards/" + boardId)
                        .header("Authorization", jwt)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

//        int statusCode = actions.andReturn().getResponse().getStatus();
//        System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(403));
        actions.andExpect(jsonPath("$.msg").value("게시글을 수정할 권한이 없습니다"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
//        actions.andExpect(jsonPath("$.body.title").value("제목1 수정"));
//        actions.andExpect(jsonPath("$.body.content").value("내용1 수정"));
    }

    @Test
    public void delete_suc_test() throws Exception {

        // given
        Integer boardId = 1;

        // when
        ResultActions actions = mvc.perform(
                delete("/api/boards/" + boardId)
                        .header("Authorization", jwt)
        );

        // eye
//        String respBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println("respBody : " + respBody);

//        int statusCode = actions.andReturn().getResponse().getStatus();
//        System.out.println("statusCode : "+statusCode);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
//        actions.andExpect(jsonPath("$.body.title").value("제목1 수정"));
//        actions.andExpect(jsonPath("$.body.content").value("내용1 수정"));
    }
}
