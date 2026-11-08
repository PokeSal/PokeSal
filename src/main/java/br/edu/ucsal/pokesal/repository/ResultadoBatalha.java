package br.edu.ucsal.pokesal.repository;

/**
 * Encapsula os dados consolidados do resultado de uma partida do torneio.
 *
 * @param treinadorVencedor Nome do treinador vencedor.
 * @param treinadorPerdedor Nome do treinador perdedor.
 * @param pokesalVencedor Nome do Pokésal vencedor.
 * @param pokesalPerdedor Nome do Pokésal perdedor.
 * @param quantidadeTurnos Total de turnos decorridos na partida.
 * @param danoCausadoVencedor Total de dano infligido pelo Pokésal vencedor.
 * @param danoCausadoPerdedor Total de dano infligido pelo Pokésal perdedor.
 * @param itensUsadosPartida Total de itens consumidos durante a partida.
 */
public record ResultadoBatalha(
    String treinadorVencedor,
    String treinadorPerdedor,
    String pokesalVencedor,
    String pokesalPerdedor,
    int quantidadeTurnos,
    int danoCausadoVencedor,
    int danoCausadoPerdedor,
    int itensUsadosPartida
) {}