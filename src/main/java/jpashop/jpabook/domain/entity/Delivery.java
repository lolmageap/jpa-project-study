package jpashop.jpabook.domain.entity;

import jakarta.persistence.*;
import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;
    @OneToOne(mappedBy = "delivery")
    private Order order;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

}
