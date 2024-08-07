package models;


import lombok.Getter;
import lombok.Setter;
import pojos.Param;
import services.DB;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="admin_roles")
@Getter @Setter
public class AdminRole extends ModelBase implements Comparable<AdminRole> {

	@Column(unique=true, nullable=false)
	private String role;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@Enumerated(EnumType.STRING)	
	@CollectionTable(name="admin_role_permissions", joinColumns=@JoinColumn(name="role_id"))
    @Column(name="permission")
    public List<AdminPermission> permissions = new ArrayList<>();

	public AdminRole() {}

	public AdminRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return this.role;
	}

	public static List<AdminRole> list() {
		Param param = Param.getAll("role", "asc");
		return DB.find(AdminRole.class, DB.where(), param);
	}

	@Override
	public int compareTo(AdminRole o) {
		return o.getId().compareTo(this.getId());
	}
}
