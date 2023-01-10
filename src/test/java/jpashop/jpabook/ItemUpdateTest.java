package jpashop.jpabook;

import jakarta.persistence.EntityManager;
import jpashop.jpabook.domain.entity.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception{
        Book book = em.find(Book.class, 802L);

        book.setName("asdfqwer");

    }

}
