package br.edu.utfpr.pb.pw45s.server.model;

import br.edu.utfpr.pb.pw45s.server.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.math.BigDecimal;

@Entity
@Table(name="tb_product")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 254)
    private String name;

    @NotNull
    @Size(min = 2, max = 1024)
    @Column(length = 1024, nullable = false)
    private String description;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
