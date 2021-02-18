package study.querydsl.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QueryDslBasicTest{
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;
    @BeforeEach
    void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1" , 10 , teamA);
        Member member2 = new Member("member2" , 20 , teamA);
        Member member3 = new Member("member3" , 30 , teamB);
        Member member4 = new Member("member4" , 40 , teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void startJPQL() throws Exception{
        //given
        //member1 을 찾아라
        //when
        String qlString =
                "select m from Member m " +
                "where m.username = :username";
        Member fineMember = em.createQuery(qlString , Member.class)
                .setParameter("username" , "member1")
                .getSingleResult();
        //then
        assertThat(fineMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQueryDsl() throws Exception{
        //given
        QMember m = new QMember("m");
        Member member = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();
        //when

        //then
        assertThat(member.getUsername()).isEqualTo("member1");
    }
}
