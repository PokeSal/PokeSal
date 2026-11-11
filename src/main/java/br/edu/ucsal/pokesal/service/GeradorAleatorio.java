package br.edu.ucsal.pokesal.service;

/** Gera os valores aleatorios de todo o projeto utilizando um intervalo em espcífico. */
public interface GeradorAleatorio {
  /**
   * Gera um número inteiro dentro de um intervalo.
   *
   * @param min Valor mínimo do intervalo.
   * @param max Valor máximo do intervalo.
   * @return Um valor aleátorio contido dentro do intervalo.
   */
  int gerarNumero(int min, int max);

  /**
   * Gera um número decimal pseudoaleatório com distribuição uniforme.
   *
   * @return Um valor decimal entre 0.0 e 1.0.
   */
  double gerarDouble();

  /**
   * Avalia a ocorrência de um evento com base em uma probabilidade informada.
   *
   * @param percentual Probabilidade de ocorrer em valores decimais.
   * @return Verdadeiro se cair dentro da probabilidade, falso caso o contrário;
   */
  boolean testarChance(double percentual);
}
