package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

//Como estamos testando repository não vamos precisar carregar o contexto inteiro.
//Vamos carregar toda a infraestrutura do spring data JPA apenas.
@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository; // Injeta o repository de verdade.

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;

	// Carrega tudo isso antes de cada teste.
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L; // Só tem 25 produtos no nosso banco.
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId); // Apaga o código 1.

		// Busca o código 1 e joga na variável result que é um Optional do tipo Product.
		Optional<Product> result = repository.findById(existingId);

		Assertions.assertFalse(result.isPresent()); // Testa se o Optional retorna algum objeto.
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}

	// Quando vamos inserir um novo objeto o id dele está valendo nulo
	// pois ele vai ser autoincrementado, quando o id não é nulo ele vai
	// atualizar.
	@Test
	public void saveShouldPersistWithAutoincrementeWhenIdIsNull() {

		Product product = Factory.createProduct(); // Cria um produto.
		product.setId(null); // Insere id nulo no produto, pois o da fábrica é 1L.

		product = repository.save(product); // Salva no banco

		// Testa se o id não é mais nulo, ou seja, se foi incrementado.
		Assertions.assertNotNull(product.getId());

		// Testa se o id do produto inserido é o 26.
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {

		// Passa nosso id existente para a variável result que é um Optional
		// do tipo Product.
		Optional<Product> result = repository.findById(existingId);

		// Testa se é verdade que o optional está cheio.
		Assertions.assertTrue(result.isPresent());
	}

	@Test
	public void findByIdShouldReturnEmptyOptionaltWhenIdDoesNotExists() {

		// Passa nosso id não existente para a variável result que é um Optional
		// do tipo Product.
		Optional<Product> result = repository.findById(nonExistingId);

		// Testa se é falso que o Optional está cheio.
		Assertions.assertFalse(result.isPresent());
	}
}
