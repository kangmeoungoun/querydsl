package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

/**
 * The type Query dsl basic test.
 */
@SpringBootTest
@Transactional
public class QueryDslBasicTest{
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    /**
     * Before.
     */
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

    /**
     * Start jpql.
     *
     * @throws Exception the exception
     */
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

    /**
     * Start query dsl.
     *
     * @throws Exception the exception
     */
    @Test
    void startQueryDsl() throws Exception{
        //given
        Member fineMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();
        //when

        //then
        assertThat(fineMember.getUsername()).isEqualTo("member1");
    }

    /**
     * Search.
     *
     * @throws Exception the exception
     */
    @Test
    void search() throws Exception{
        //given
        //when
        Member fineMember = queryFactory
                .selectFrom(member)
                .where(
                         member.username.eq("member1")
                       , member.age.eq(10))
                .fetchOne();
        //then
        assertThat(fineMember.getUsername()).isEqualTo("member1");
    }

    /**
     * Result fetch.
     *
     * @throws Exception the exception
     */
    @Test
    void resultFetch() throws Exception{
        //given
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
        Member fetchOne = queryFactory
                .selectFrom(QMember.member)
                .fetchOne();
        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        results.getTotal();
        List<Member> content = results.getResults();

        long l = queryFactory
                .selectFrom(member)
                .fetchCount();

        //when

        //then
    }
    /*
    * 회원정렬 순서
    * 1.회원 나이 내림차순
    * 2.회원 이름 올림차순
    * 단 2에서 회원 이름이 없으면 마지막에 출력
    * */
    @Test
    void sort() throws Exception{
        em.persist(new Member(null,100));
        em.persist(new Member("member5",100));
        em.persist(new Member("member6",100));
        //given
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc() , member.username.asc().nullsLast())
                .fetch();
        //when
        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        //then
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }
}
