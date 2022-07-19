package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	// O service depende do repository.
	@Autowired
	private CategoryRepository repository;

	// Vai garantir a integridade (ou faz tudo ou não faz nada, e todas
	// as outras garantias de transação) da transação e o parâmetro readOnly
	// vai dizer que não precisa travar o banco pois é apenas leitura.
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {

		// A lista de categorias vai receber o repositorio do findAll.
		Page<Category> list = repository.findAll(pageable);

		// Para cada objeto x da lista (de categorias) nós instanciamos um novo DTO.
		return list.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		// findById retorna um Optional do tipo Category, perceba
		// que vamos buscar no repositoy um Category e não um CategoryDTO.
		Optional<Category> obj = repository.findById(id);

		// OBS: O método get (obj.get() ) obetem o objeto dentro do Optional,
		// não usamos apenas o get pq tratamos um exceção.
		// Usaremos o orElse para retornar outra coisa caso o objeto Optional
		// esteja vazio, vamos usar o Throw para retornar uma exceção caso
		// esteja vazio e lançamos a nossa exceção.
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

		// O método retorna um DTO e não a entidade, por isso usamos um new
		// e passamos a entidade como argumento para o DTO. Lembre que um
		// dos construtores do DTO recebe uma entity.
		return new CategoryDTO(entity);
	}

	// Vamos pegar o DTO que chegou como argumento e converter
	// para uma entidade (Category) para salvar no banco..
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category(); // Instancia um Category.
		entity.setName(dto.getName()); // Seta o nome (não seta id).
		// Salva a entidade no banco. O método save por padrão retorna
		// uma referência para a entidade salva, por isso temos que
		// colocar em uma variável entity.
		entity = repository.save(entity);

		// O método retorna um DTO e não a entidade, por isso usamos um new
		// e passamos a entidade como argumento para o DTO. Lembre que um
		// dos construtores do DTO recebe uma entity.
		return new CategoryDTO(entity);
	}

	// Não vamos usar o findById para buscar pois estariamos tocando
	// o banco de dados 2 vezes e para atualizar basta 1 comando, por isso
	// usamos o save, que vai apenas 1 vez.
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		// Pode dar uma exceção se tentarmos salvar um id que não existe.
		try {
			// getOne vai instanciar um objeto provisório com os dados
			// e com o id, só quando mandarmos salvar é que vamos no banco.
			// Colocamos esse objeto provisório numa entidade para se comunicar
			// com o banco.
			Category entity = repository.getOne(id);
			// Setamos o nome da entidade que está só na memória. (id não)
			entity.setName(dto.getName());
			entity = repository.save(entity); // Salvamos no banco.

			// Retornamos um DTO da entidade.
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found" + id);
		}
	}

	// Não vamos usar @Transactional, pois vamos ter que capturar uma
	// exceção e se usarmos @Transactional não podemos capturar a exceção.
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}

		// Se tentarmos deletar um id que não existe.
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found" + id);
		}

		// Não pode deletar categorias que tem produtos.
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
}
