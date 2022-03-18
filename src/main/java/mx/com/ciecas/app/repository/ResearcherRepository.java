package mx.com.ciecas.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import mx.com.ciecas.app.models.documents.Researcher;

@Repository
public interface ResearcherRepository  extends ReactiveMongoRepository<Researcher, String>{

}
