package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	private Long existingId;
	private Long nonExistingId;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	// Total de produtos, pois já conhecemos o banco.
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L; // Número real de produtos no banco. 
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{
		
		ResultActions result = 
				// Passamos a mesma busca do Postman.
				mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		// Testa se o totalElements(total de elementos no Postman)
		// é igual ao número total de produtos.
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		
		// Testa se o campo content (do json) existe.
		result.andExpect(jsonPath("$.content").exists());
		
		// Testa se o content na posição 0.name é igual aos valores para ver 
		// se está ordenado por nome.
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		ProductDTO productDTO = Factory.createProductDTO(); // Instancia um ProductDTO
		
		// Converte objeto Java em uma string.
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		// Cria variáveis para pegar os valores e jogar nos testes. 
		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
						.content(jsonBody) // Recebe nosso objeto JSON.
						.contentType(MediaType.APPLICATION_JSON) // Tipo do corpo da requisição.
						.accept(MediaType.APPLICATION_JSON));  
		
		result.andExpect(status().isOk());
		
		// Testa se o id é o mesmo do existingId;
		result.andExpect(jsonPath("$.id").value(existingId));
		
		// Testamos se o nome realmente bate com o nome esperado que 
		// está na variável expectedName.
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
		
		ProductDTO productDTO = Factory.createProductDTO(); // Instancia um ProductDTO
		
		// Converte objeto Java em uma string.
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
						.content(jsonBody) // Recebe nosso objeto JSON.
						.contentType(MediaType.APPLICATION_JSON) // Tipo do corpo da requisição.
						.accept(MediaType.APPLICATION_JSON));  
		
		// Testa se está voltando um NotFound.
		result.andExpect(status().isNotFound());
	}
}
