/*
 * @(#)DomainSeeder.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.infrastructure.persistence;

public interface DomainSeeder {
    boolean shouldSeed();
    void seed();
}
