package io.github.czjena.local_trade.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Data
@Entity
@EqualsAndHashCode(exclude = "favoritedAdvertisements")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @Email
    private String email;
    private String password;
    private String role = "ROLE_USER";
    private UUID userId =  UUID.randomUUID();

    private int ratingCount;
    private double averageRating;

    @ToString.Exclude
    @ManyToMany(mappedBy = "favoritedByUsers", fetch = FetchType.LAZY )
    private Set<Advertisement> favoritedAdvertisements = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false, name ="created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }
    @Override
    public boolean isEnabled(){
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Users user)) return false;
        if (userId == null || user.userId == null) return false;
        return userId.equals(user.userId);
    }
    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }




}
