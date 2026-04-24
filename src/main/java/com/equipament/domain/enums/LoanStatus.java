package com.equipament.domain.enums;

public enum LoanStatus {
    /**
     * O técnico está configurando a máquina (Instalando SO, softwares, etc).
     * AC2: Bloqueia o item no inventário.
     */
    PREPARACAO,

    /**
     * A configuração técnica terminou, e o item foi enviado ao Administrativo
     * para colher a assinatura do termo de responsabilidade.
     */
    AGUARDANDO_ASSINATURA,

    /**
     * O colaborador já recebeu o equipamento e o termo foi assinado.
     * Equivale ao estado "Em Uso".
     */
    ENTREGUE,

    /**
     * O equipamento foi devolvido pelo colaborador e o processo de
     * empréstimo foi encerrado com sucesso.
     */
    DEVOLVIDO,

    /**
     * Caso o processo de empréstimo seja interrompido antes da entrega.
     */
    CANCELADO
}
