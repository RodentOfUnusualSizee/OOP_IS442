package com.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Component
public class MyStartupRunner implements ApplicationRunner {

    private final EntityManager entityManager;

    @Autowired
    public MyStartupRunner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Execute the SQL statement to alter the table, because positions uses
        // serialization to store, as it is nested, default limit is too small
        entityManager.createNativeQuery("ALTER TABLE user_portfolio ALTER COLUMN POSITIONS VARBINARY(2048);")
                .executeUpdate();
    }
}
