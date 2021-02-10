package com.cos.book.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class BookControllerIntegreTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@BeforeEach
	public void init() {
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
	}
	
	
	
	@Test
	public void save_테스트() throws Exception{
		//given
		Book book = new Book(1, "스프링 따라하기", 3.5, 20.000);
		String content = new ObjectMapper().writeValueAsString(book);
		
		//when
		ResultActions resultActions = mockMvc.perform(post("/book")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value("스프링 따라하기"))
			.andDo(MockMvcResultHandlers.print());
		
	}
	//두번째 테스트
		@Test
		public void findAll_테스트() throws Exception {
			//given
			List<Book> books = new ArrayList<>();
			books.add(new Book(1,"스프링부트 따라하기",3.5,20.000));
			books.add(new Book(2,"리엑트 따라하기",4.5,18.000));
			books.add(new Book(3,"JUNIT 따라하기",4.0,22.000));
			bookRepository.saveAll(books);
			
			//when
			ResultActions resultActions = mockMvc.perform(get("/book")
					.accept(MediaType.APPLICATION_JSON_UTF8)); //기대하는 값 
			
			//then = 내가 기대하는 값
			resultActions
			 .andExpect(status().isOk())
			 .andExpect(jsonPath("$", Matchers.hasSize(3))) //3개를 기대함 = 컬렉션 사이즈 
			 .andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
			 .andDo(MockMvcResultHandlers.print());
		}
		//세번째 테스트
		@Test
		public void findById_테스트() throws Exception{
			//given
			Integer id = 1;
			
			List<Book> books = new ArrayList<>();
			books.add(new Book(1,"스프링부트 따라하기",3.5,20.000));
			books.add(new Book(2,"리엑트 따라하기",4.5,18.000));
			books.add(new Book(3,"JUNIT 따라하기",4.0,22.000));
			bookRepository.saveAll(books);
			//when
			ResultActions resultActions = mockMvc.perform(get("/book/{id}",id)
					.accept(MediaType.APPLICATION_JSON_UTF8));
			
			//then
			resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("스프링부트 따라하기"))
			.andDo(MockMvcResultHandlers.print());
		}
		//네번째 테스트
		@Test
		public void delete_테스트() throws Exception{
			//given
			Integer id = 1;
			List<Book> books = new ArrayList<>();
			books.add(new Book(1,"스프링부트 따라하기",3.5,20.000));
			books.add(new Book(2,"리엑트 따라하기",4.5,18.000));
			books.add(new Book(3,"JUNIT 따라하기",4.0,22.000));
			bookRepository.saveAll(books);
			
			//when
			ResultActions resultActions = mockMvc.perform(delete("/book/{id}",id)
					.contentType(MediaType.TEXT_PLAIN)); //=application/json 요청을 json으로
			
			//then
			resultActions
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print());
			
			MvcResult requestResult = resultActions.andReturn();
			String result = requestResult.getResponse().getContentAsString(); //상태코드가 맞는지
			
			assertEquals("ok", result);
		}
	
}
