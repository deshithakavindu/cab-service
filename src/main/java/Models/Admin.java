package Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "customer")
public class Admin {

	  @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "id")
      private int id; // Auto-incrementing ID
	
	 @Column(name = "username", nullable = false, unique = true)
	    private String username;

	    @Column(name = "password", nullable = false)
	    private String password;
}
