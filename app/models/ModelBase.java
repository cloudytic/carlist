package models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter @Setter
public class ModelBase implements Serializable, ModelInterface {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
    public Long id;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date created = new Date();
}
