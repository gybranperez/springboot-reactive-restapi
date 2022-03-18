package mx.com.ciecas.app.models.documents;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "researchers")
@Data
@AllArgsConstructor  
@NoArgsConstructor
public class Researcher {
	@Id
	private String id;
	@NotNull
	private String name;
	@NotNull
	private String surname;
	@NotNull
	@Email
	private String email;
	private String idGoogleScholar;
	
	private Date dateCreated;
	private Date dateModified;
	
	public Researcher(String id, String name, String surname, @Email String email) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
	}
	public Researcher(String id, String name, String surname, @Email String email, String idGoogleScholar) {
		this(id, name, surname, email);
		this.idGoogleScholar = idGoogleScholar;
	}

}