package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedEntityGraph(name = User.WITH_ROLES,
        attributeNodes = {@NamedAttributeNode("roles")})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WITH_ROLES="User.withRoles";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @JoinColumn(name = "userName", unique = true)
    private String username;
    @NotBlank
    @JoinColumn(name = "email", unique = true)
    private String email;
    @NotBlank
    private String password;
    private boolean enabled;
    @ManyToMany(mappedBy = "users")
    private Set<Role> roles = new LinkedHashSet<>();
    @Version
    private long version;

    protected User() {}

    public User(@NotBlank String username, @NotBlank String email,
                @NotBlank String password, boolean enabled) {
        this.username = username;
        this.email = email;
        this.password = "{bcrypt}" + password;
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = "{bcrypt}" + password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean add(Role role){
        boolean added = roles.add(role);
        if(!role.getUsers().contains(this)){
            role.add(this);
        }
        return added;
    }

    public boolean remove(Role role){
        boolean removed = roles.remove(role);
        if(role.getUsers().contains(this)){
            role.remove(this);
        }
        return removed;
    }

    public Set<Role> getRoles(){
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getEmail().equalsIgnoreCase(user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return username;
    }
}
