package com.shannontheoret.machikoro.dao;

import com.shannontheoret.machikoro.entity.Game;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class  GameDao {

    private EntityManager entityManager;

    @Autowired
    public GameDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Game findByCode(String code) {
        Session currentSession = entityManager.unwrap(Session.class);
        return currentSession.get(Game.class, code);
    }

    @Transactional
    public void save(Game game) {
        if (game.getCode() == null) {
            entityManager.persist(game);
        } else {
            entityManager.merge(game);
        }
    }
}