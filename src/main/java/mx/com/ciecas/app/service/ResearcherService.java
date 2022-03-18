package mx.com.ciecas.app.service;

import mx.com.ciecas.app.models.documents.Researcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResearcherService {

	public Flux<Researcher> findAll();
	
	public Mono<Researcher> findById(String id);
	
	public Mono<Researcher> update(Researcher researcher);
	
	public Mono<Researcher> insert(Researcher researcher);
	
	public Mono<Void> delete(String id);

}
