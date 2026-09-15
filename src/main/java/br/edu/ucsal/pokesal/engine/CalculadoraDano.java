package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class CalculadoraDano {

	public static int calcularDano(PokeSal atacante, PokeSal defensor, Terreno terreno) {
		double danoBase = ((double) atacante.getAtk() / defensor.getDef()) * ConstantesJogo.DANO_BASE;

		double efetividade = calcularEfetividade(atacante, defensor);
		double danoTerreno = calcularMultiplicadorTerreno(atacante, terreno);
		double critico = calcularAcertoCritico(efetividade);

		danoBase *= efetividade * danoTerreno * critico;

		int danoArredondado = (int) Math.round(danoBase);

		int danoFinal = Math.max(1, danoArredondado);

		return danoFinal;
	}

	public static double calcularEfetividade(PokeSal atacante, PokeSal defensor) {
		if (atacante.getTipo() == TipoElemental.FOGO && defensor.getTipo() == TipoElemental.PLANTA
				|| atacante.getTipo() == TipoElemental.AGUA && defensor.getTipo() == TipoElemental.FOGO
				|| atacante.getTipo() == TipoElemental.PLANTA && defensor.getTipo() == TipoElemental.AGUA) {
			System.out.println("\n[ELEMENTAL] Ataque SUPER EFETIVO!");
			return ConstantesJogo.MULT_SUPER_EFETIVO;
		} else if (atacante.getTipo() == TipoElemental.FOGO && defensor.getTipo() == TipoElemental.AGUA
				|| atacante.getTipo() == TipoElemental.AGUA && defensor.getTipo() == TipoElemental.PLANTA
				|| atacante.getTipo() == TipoElemental.PLANTA && defensor.getTipo() == TipoElemental.FOGO) {
			System.out.println("\n[ELEMENTAL] Ataque pouco efetivo...");
			return ConstantesJogo.MULT_POUCO_EFETIVO;
		} else {
			return ConstantesJogo.MULT_NEUTRO;
		}

	}

	public static double calcularMultiplicadorTerreno(PokeSal atacante, Terreno terreno) {
		if (terreno == Terreno.ASFALTO_QUENTE && atacante.getTipo() == TipoElemental.FOGO) {
			System.out.println("\n[TERRENO] O terreno ASFALTO QUENTE potencializou o ataque de fogo!");
			return ConstantesJogo.BONUS_ASFALTO_QUENTE;
		} else if (terreno == Terreno.POCA_CHUVA && atacante.getTipo() == TipoElemental.AGUA) {
			System.out.println("[TERRENO] O terreno POÇA DE CHUVA potencializou o ataque de água!");
			return ConstantesJogo.BONUS_POCA_CHUVA;
		} else {
			return ConstantesJogo.MULT_NEUTRO;
		}
	}

	public static double calcularAcertoCritico(double efetividade) {
		double chanceCritico;

		if (efetividade == ConstantesJogo.MULT_SUPER_EFETIVO) {
			chanceCritico = ConstantesJogo.CHANCE_CRITICO_VANTAGEM;
		} else {
			chanceCritico = ConstantesJogo.CHANCE_CRITICO_PADRAO;
		}

		if (Math.random() < chanceCritico) {
			return ConstantesJogo.MULT_CRITICO;
		} else {
			return ConstantesJogo.MULT_NEUTRO;
		}
	}
}
