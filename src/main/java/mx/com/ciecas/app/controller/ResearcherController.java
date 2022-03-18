package mx.com.ciecas.app.controller;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import mx.com.ciecas.app.models.documents.Researcher;
import mx.com.ciecas.app.service.ResearcherService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/researcher")
public class ResearcherController {

	@Autowired
	private ResearcherService service;

	//private static final Logger log = LoggerFactory.getLogger(ResearcherController.class);

	@GetMapping
	public Mono<ResponseEntity<Flux<Researcher>>> list() {
		return Mono.just(
				ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll())
		);
	}

	
	  @GetMapping("/{id}") 
	  public Mono<ResponseEntity<Researcher>> getById(@PathVariable String id){
		  
		  return service.findById(id)
				  .map(r -> ResponseEntity.ok()
						  .contentType(MediaType.APPLICATION_JSON)
						  .body(r))
				  .defaultIfEmpty(ResponseEntity.notFound().build());
		  /*
		  return service.findAll().filter(p -> p.getId().equals(id)).next().doOnNext(prod -> log.info(prod.getName()));
		  */
	  }
	  
	  @PostMapping
	  public Mono<ResponseEntity<Map<String, Object>>> create(@RequestBody @Valid Mono<Researcher> monoResearcher){
		  
		  Map<String, Object> response = new HashMap<String, Object>();
		  
		  return monoResearcher.flatMap(researcher -> {
			  return service
		  		.insert(researcher)
				.map(r -> {
					
					response.put("researcher", r);
					response.put("message", "Investigador creado con exito");
					response.put("timestamp", new Date());
					
					return ResponseEntity
				 	.created(URI.create("/api/researcher/".concat(r.getId())))
				 	.contentType(MediaType.APPLICATION_JSON)
				 	.body(response); 
				});
		  }).onErrorResume(exception -> {
			  // Se obtioene el flujo de excepcion
			  return Mono.just(exception)
					  
					  // Se hace un cast a un tipo de excepcion particular
					  .cast(WebExchangeBindException.class)
					  
					  // de la excepcion original se obtienen los errores que haya lanzado el @Valid por los campos del modelo
					  .flatMap(e -> Mono.just(e.getFieldErrors()) )
					  
					  /* ya teniendo la lista de errores se convierte en un flujo con el que trabajaremos utilizamos el metodo 
					     fromIterable de la clase Flux, como se pasa la lista directamente al metodo, trabajamos con el operador ::  */ 
					  .flatMapMany(Flux::fromIterable)
					  
					  // ahora como es un FLUJO, aca trabajaremos con cada elemento de este de forma individual
					  .map(fieldError -> String.format("El campo %s : %s .", fieldError.getField(), fieldError.getDefaultMessage()))
					  
					  // Una vez que ponemos bonito el mensaje de error creamos una lista
					  .collectList()
					  
					  //Ya tenemos la lista! ahora enviamos el ResponseEntity con BadRequest
					  .flatMap(list -> {
						 response.put("error", list);
						 response.put("status", HttpStatus.BAD_REQUEST.value());
						 response.put("timestamp", new Date());
						 // Recordando que lo que devolveremos es un Mono lo casteamos con just()
						 return Mono.just(ResponseEntity.badRequest().body(response)); 
					  });
		  });

	  }
	  
	  @PutMapping("/{id}")
	  public Mono<ResponseEntity<Researcher>> update(@RequestBody Researcher researcher, @PathVariable String id){
		  return service
				  		.findById(id)
				  		// Si es que existe un usuario con ese ID, se actualizan los campos pertinentes
				  		.flatMap(r -> {
				  			r.setName(researcher.getName());
				  			r.setSurname(researcher.getSurname());
				  			r.setEmail(researcher.getEmail());
				  			if(researcher.getIdGoogleScholar() != null) r.setIdGoogleScholar(researcher.getIdGoogleScholar()); 
				  			return service.update(r);
				  		})
				  		// Recibimos el objeto actualizado y lo devolvemos
				  		.map(r -> ResponseEntity
						 	.created(URI.create("/api/researcher/".concat(r.getId())))
						 	.contentType(MediaType.APPLICATION_JSON)
						 	.body(r) 
						)
				  		// Si no existe un registro con ese ID
				  		.defaultIfEmpty(ResponseEntity.notFound().build());
	  }
	  
	  
	  @DeleteMapping("/{id}")
		public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
			return service
			  		.findById(id)
			  		.flatMap(r -> service
			  				.delete(r.getId())
			  				.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
			  		)
			  		.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
			  		// Si no existe un registro con ese ID
			  		
		}
	 

}
