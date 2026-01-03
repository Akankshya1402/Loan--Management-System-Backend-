@DataMongoTest
class LoanAnalyticsRepositoryTest {

    @Autowired
    private LoanAnalyticsRepository repository;

    @Test
    void shouldSaveAndFetchAnalytics() {
        LoanAnalytics analytics = TestDataFactory.loanAnalytics();
        repository.save(analytics);

        assertTrue(repository.findById("PERSONAL").isPresent());
    }
}

