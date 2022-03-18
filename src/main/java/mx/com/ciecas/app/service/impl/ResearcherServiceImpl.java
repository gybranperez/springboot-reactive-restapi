package mx.com.ciecas.app.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.com.ciecas.app.models.documents.Researcher;
import mx.com.ciecas.app.repository.ResearcherRepository;
import mx.com.ciecas.app.service.ResearcherService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ResearcherServiceImpl implements ResearcherService{

	private static final Logger log = LoggerFactory.getLogger(ResearcherServiceImpl.class);
	@Autowired
	private ResearcherRepository repo;
	
	@Override
	public Flux<Researcher> findAll() {
		
		return repo.findAll();
	}

	@Override
	public Mono<Researcher> findById(String id) {
		return repo.findById(id);
	}

	@Override
	public Mono<Researcher> update(Researcher researcher) {
		researcher.setDateModified(new Date());
		return repo.save(researcher);
	}

	@Override
	public Mono<Researcher> insert(Researcher researcher) {
		researcher.setDateModified(new Date());
		researcher.setDateCreated(new Date());
		return repo.insert(researcher);
	}

	@Override
	public Mono<Void> delete(String id) {
		return repo.deleteById(id);
	}

}
