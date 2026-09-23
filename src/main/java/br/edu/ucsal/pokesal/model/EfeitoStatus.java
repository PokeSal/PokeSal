package br.edu.ucsal.pokesal.model;

/**
 * Representa os efeitos de status negativos que um Pokésal pode sofrer durante a batalha.
 */
public enum EfeitoStatus {

  /** Nenhum efeito ativo. */
  NENHUM,

  /** Causa dano contínuo ao final de cada turno. */
  QUEIMADO,

  /** Causa dano contínuo ao final de cada turno. */
  ENVENENADO,

  /** Faz o Pokésal perder a vez de atuar no turno. */
  PARALISADO;
}
