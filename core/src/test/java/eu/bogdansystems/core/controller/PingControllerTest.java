package eu.bogdansystems.core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
class PingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void pingReturnsHelloWorld() throws Exception {
		mockMvc.perform(get("/ping"))
				.andExpect(status().isOk())
				.andExpect(content().string("hello world"));
	}

}
