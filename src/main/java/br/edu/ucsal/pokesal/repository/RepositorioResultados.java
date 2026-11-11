package br.edu.ucsal.pokesal.repository;

/** Contrato responsável pela persistência dos resultados e geração de estatísticas do torneio. */
public interface RepositorioResultados {

  /**
   * Registra o resultado consolidado de uma partida finalizada.
   *
   * @param resultado Objeto contendo as estatísticas da partida.
   */
  void salvarResultado(ResultadoBatalha resultado);

  /** Compila e exibe o relatório de estatísticas finais do torneio (Requisito Autoral 04). */
  void emitirRelatorioEstatistico();
}
