package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
public class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save() {

        /** save() 메서드
         *
         * 새로운 엔티티면 저장( persist )
         * 새로운 엔티티가 아니면 병합( merge )
         * 새로운 엔티티를 판단하는 기본 전략
         * 식별자가 객체일 때 null 로 판단
         * 식별자가 자바 기본 타입일 때 0 으로 판단
         * Persistable 인터페이스를 구현해서 판단 로직 변경 가능
         */

        Item item = new Item("AAA");
        itemRepository.save(item);

        /** save에 ctrl+b, 왼쪽에 초록색 버튼 눌러서 SimpleJpaRepository.java에 들어가면 보이는 모습
         *
         *    @Transactional
         *    @Override
         *    public <S extends T> S save(S entity) {
         *
         * 		Assert.notNull(entity, "Entity must not be null.");
         *
         * 		if (entityInformation.isNew(entity)) {
         * 			em.persist(entity);
         * 			return entity;
         *        } else {
         * 			return em.merge(entity);
         *        }
         *    }
         */
    }
}
