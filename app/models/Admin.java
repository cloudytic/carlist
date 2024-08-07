package models;

import lombok.Getter;
import lombok.Setter;
import pojos.Needed;
import services.PasswordService;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="admins")
@Getter @Setter
public class Admin extends ModelBase {
    @Column(nullable=false)
    public boolean active = true;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String hashedPassword;

    @Needed
    private String firstName;

    @Needed
    private String lastName;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="admins_roles",
            joinColumns=@JoinColumn(name="admin_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    public Set<AdminRole> roles = new HashSet<>();

    public String name() {
        return firstName + " "+ lastName;
    }

    public Set<AdminPermission> permissions() {
        Set<AdminPermission> perms = new HashSet<>();
        roles.forEach(role -> {
            perms.addAll(role.getPermissions());
        });
        return perms;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = PasswordService.hashPassword(password);
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
