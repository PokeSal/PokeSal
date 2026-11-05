package br.edu.ucsal.pokesal.repository;

/**
 * Contrato de repositório responsável pela persistência dos resultados do torneio.
 */
public interface RepositorioResultados {

  /**
   * Salva o resultado consolidado de uma batalha concluída.
   *
   * @param resultado Objeto contendo os dados da partida.
   */
  void salvarResultado(ResultadoBatalha resultado);
}