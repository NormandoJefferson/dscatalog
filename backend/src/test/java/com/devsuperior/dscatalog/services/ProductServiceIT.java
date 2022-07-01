package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

// Vamos usar @Transactional para garantir o reset do banco de dados
// após cada teste.

@SpringBootTest
@Transactional
public class ProductServiceIT {

	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;

	private Long existingId;
	private Long nonExistingId;
	
	// Total de produtos, pois já conhecemos o banco.
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L; // Número real de produtos no banco. 
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(existingId);
		
		// Testa se o total de registros no banco é igual ao que tinha antes - 1.
		// O repository.count mostra o total de registros no banco. 
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
		
		// Cria um objeto de página.
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		// Tem que ser falso que a página está vazia. 
		Assertions.assertFalse(result.isEmpty());
		
		// Testa se o número da página é o 0. 
		Assertions.assertEquals(0, result.getNumber());
		
		// Testa se o tamanho da página é 10.
		Assertions.assertEquals(10, result.getSize());
		
		// Verifica se o total de produtos buscado é o total do banco.
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
		
		// Cria um objeto de página que não existe no nosso banco.
		PageRequest pageRequest = PageRequest.of(50, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		// Tem que ser verdadeiro que a página está vazia. 
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPagedShouldReturOrderedPageWhenSortedByName() {
		
		// Cria um objeto de página ordenado por nome.
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		// Tem que ser falso que a página está vazia. 
		Assertions.assertFalse(result.isEmpty());
		
		// Vamos testar os primeiros 3 produtos se estão ordenados por nome.
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
}
