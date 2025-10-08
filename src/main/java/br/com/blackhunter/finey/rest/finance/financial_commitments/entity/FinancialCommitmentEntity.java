package br.com.blackhunter.finey.rest.finance.financial_commitments.entity;

import java.util.UUID;

public class FinancialCommitmentEntity {
    private UUID financialCommitmentId;
    private String commitmentName;
    private String commitmentType;

    // estrturar depois os dados dos compromissos financeiros.
    // o backoffice terá tarefas agendadas para notificar o usuário quando a data do compromisso estiver chegando e no dia.
}
