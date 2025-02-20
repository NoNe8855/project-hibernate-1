package com.game.repository;

import com.game.entity.Player;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

   private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.HBM2DDL_AUTO, "update");// Автоматическое обновление схемы
        properties.put(Environment.SHOW_SQL, "true"); // Показывать SQL-запросы в консоли
        properties.put(Environment.FORMAT_SQL, "true"); // Форматировать SQL-запросы

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();

    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery("SELECT * FROM player LIMIT :limit OFFSET :offset", Player.class)
                    .setParameter("limit", pageSize)
                    .setParameter("offset", (pageNumber - 1) * pageSize)
                    .list();
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            return session.createNamedQuery("Player.getAllCount", Long.class)
                    .getSingleResult()
                    .intValue();
        }

    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
            return player;
        }

    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(player);
            session.getTransaction().commit();
            return player;
        }

    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Player.class, id));
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(player);
            session.getTransaction().commit();

        }

    }

    @PreDestroy
    public void beforeStop() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }

    }
}