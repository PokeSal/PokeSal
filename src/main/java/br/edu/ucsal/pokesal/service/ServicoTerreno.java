package br.edu.ucsal.pokesal.service;

import br.edu.ucsal.pokesal.model.Terreno;
import java.time.Clock;

/**
 * Contrato responsável pela consulta e obtenção das condições de terreno
 * ativas.
 */
public interface ServicoTerreno {

	/**
	 * Obtém o terreno atual da batalha considerando as condições climáticas e o
	 * horário.
	 *
	 * @param clock Instância do relógio do sistema para validação do horário.
	 * @return O objeto {@link Terreno} correspondente.
	 */
	Terreno obterTerrenoAtual(Clock clock);
}