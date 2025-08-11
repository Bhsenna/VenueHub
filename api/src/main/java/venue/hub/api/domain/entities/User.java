package venue.hub.api.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import venue.hub.api.domain.dtos.user.UserUpdateDTO;
import venue.hub.api.domain.enums.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String sobrenome;
    private String login;
    private String senha;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CLIENT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    private boolean ativo = true;

    public User(String nome, String sobrenome, String login, String senha, UserRole role, Address address) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.login = login;
        this.senha = senha;
        this.address = address;
    }

    public void delete() {
        this.ativo = false;
    }

    public void update(UserUpdateDTO updateDTO) {
        if (updateDTO.getNome() != null && !updateDTO.getNome().isBlank()) {
            this.nome = updateDTO.getNome();
        }
        if (updateDTO.getSobrenome() != null && !updateDTO.getSobrenome().isBlank()) {
            this.sobrenome = updateDTO.getSobrenome();
        }
        if (updateDTO.getAddress() != null) {
            this.address.update(updateDTO.getAddress());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + this.role));

        return list;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }
}
