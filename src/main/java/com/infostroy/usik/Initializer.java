package com.infostroy.usik;

import com.infostroy.usik.modal.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This class is executed during application initialization; it is necessary to clear old records in the database.
 *
 * @author N.Usik
 */
@Component
class Initializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    private final StudentRepository repository;

    public Initializer(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
        logger.warn("Init application, clear old records in the database");
        repository.deleteAll();
    }
}