package jpashop.jpabook.domain.entity.item;

import jakarta.persistence.*;
import jpashop.jpabook.domain.entity.Category;
import jpashop.jpabook.domain.entity.exception.NotEnoughtStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /* 비즈니스 로직*/
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }
    public void removeStock(int quantity){
        int resultStock = this.stockQuantity - quantity;
        if (resultStock < 0){
            throw new NotEnoughtStockException("need more stock");
        }
        this.stockQuantity = resultStock;
    }
}
