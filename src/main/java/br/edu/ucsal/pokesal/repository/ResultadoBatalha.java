package br.edu.ucsal.pokesal.repository;

/**
 * Representa os dados consolidados do resultado de uma batalha.
 *
 * @param treinadorVencedor Nome do treinador vencedor.
 * @param treinadorPerdedor Nome do treinador perdedor.
 * @param pokesalVencedor   Nome do Pokésal utilizado pelo vencedor.
 * @param pokesalPerdedor   Nome do Pokésal utilizado pelo perdedor.
 * @param quantidadeTurnos  Total de turnos decorridos na batalha.
 */
public record ResultadoBatalha(String treinadorVencedor, String treinadorPerdedor, String pokesalVencedor,
		String pokesalPerdedor, int quantidadeTurnos) {
}