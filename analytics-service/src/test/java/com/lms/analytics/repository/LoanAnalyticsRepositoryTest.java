package com.lms.analytics.repository;

import com.lms.analytics.model.LoanAnalytics;
import com.lms.analytics.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
class LoanAnalyticsRepositoryTest {

    @Autowired
    private LoanAnalyticsRepository repository;

    @Test
    void shouldSaveAndFetchAnalytics() {

        LoanAnalytics analytics = TestDataFactory.loanAnalytics();

        repository.save(analytics);

        // "PERSONAL" is the @Id value in LoanAnalytics
        assertTrue(repository.findById("PERSONAL").isPresent());
    }
}
