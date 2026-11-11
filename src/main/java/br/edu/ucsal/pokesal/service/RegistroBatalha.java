package br.edu.ucsal.pokesal.service;

import br.edu.ucsal.pokesal.model.EfeitoStatus;

/**
 * Contrato responsável pelo registro e auditoria dos eventos que ocorrem em cada turno da batalha.
 */
public interface RegistroBatalha {

  /**
   * Registra a execução de um ataque.
   *
   * @param atacante Nome do Pokésal que está atacando.
   * @param defensor Nome do Pokésal alvo.
   */
  void registrarAtaque(String atacante, String defensor);

  /**
   * Registra o dano provocado por um ataque.
   *
   * @param defensor Nome do Pokésal atingido.
   * @param dano Quantidade de pontos de dano aplicados.
   * @param critico Indica se o ataque foi um acerto crítico.
   * @param errou Indica se o ataque errou por falha de precisão.
   */
  void registrarDano(String defensor, int dano, boolean critico, boolean errou);

  /**
   * Registra a alteração ou aplicação de um efeito de status.
   *
   * @param pokesal Nome do Pokésal afetado.
   * @param status O {@link EfeitoStatus} aplicado.
   */
  void registrarEfeitoStatus(String pokesal, EfeitoStatus status);

  /**
   * Registra ativações ou alterações nas condições do terreno.
   *
   * @param mensagem Descrição do efeito do terreno.
   */
  void registrarEfeitoTerreno(String mensagem);

  /**
   * Registra o uso de um item da mochila.
   *
   * @param treinador Nome do treinador que consumiu o item.
   * @param item Nome do item utilizado.
   */
  void registrarUsoItem(String treinador, String item);
}
