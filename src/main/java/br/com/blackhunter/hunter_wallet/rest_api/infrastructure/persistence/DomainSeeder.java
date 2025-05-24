package br.com.blackhunter.hunter_wallet.rest_api.infrastructure.persistence;

public interface DomainSeeder {
    boolean shouldSeed();
    void seed();
}
