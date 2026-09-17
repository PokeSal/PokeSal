package br.edu.ucsal.pokesal.engine;

import java.util.Random;

import br.edu.ucsal.pokesal.model.EfeitoStatus;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoElemental;
import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class CalculadoraDano {

	public static int calcularDano(PokeSal atacante, PokeSal defensor, Terreno terreno) {
		double danoBase = ((double) atacante.getAtk() / defensor.getDef()) * ConstantesJogo.DANO_BASE;

		double efetividade = calcularEfetividade(atacante, defensor);
		double danoTerreno = calcularMultiplicadorTerreno(atacante, terreno);

		danoBase *= efetividade * danoTerreno;

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
			System.out.println("\n[TERRENO] O terreno POÇA DE CHUVA potencializou o ataque de água!");
			return ConstantesJogo.BONUS_POCA_CHUVA;
		} else {
			return ConstantesJogo.MULT_NEUTRO;
		}
	}

	public static void aplicarEfeitoStatus(PokeSal atacante, PokeSal defensor) {
		Random rand = new Random();
		boolean aplicarEfeito = false;

		if (defensor.getStatusAtual() != EfeitoStatus.NENHUM) {
			return;
		}

		double chanceAplicarEfeito = rand.nextDouble(0, 1);

		if (chanceAplicarEfeito < ConstantesJogo.CHANCE_EFEITO_STATUS) {
			aplicarEfeito = true;
		}

		if (aplicarEfeito && atacante.getTipo() == TipoElemental.FOGO && defensor.getTipo() != TipoElemental.FOGO) {
			defensor.setStatusAtual(EfeitoStatus.QUEIMADO);
			System.out.println("\n[STATUS] " + defensor.getNome() + " ficou QUEIMADO pelo ataque!");
		} else if (aplicarEfeito && atacante.getTipo() == TipoElemental.AGUA
				&& defensor.getTipo() != TipoElemental.AGUA) {
			defensor.setStatusAtual(EfeitoStatus.PARALISADO);
			System.out.println("\n[STATUS] " + defensor.getNome() + " ficou PARALISADO pelo ataque!");
		} else if (aplicarEfeito && atacante.getTipo() == TipoElemental.PLANTA
				&& defensor.getTipo() != TipoElemental.PLANTA) {
			defensor.setStatusAtual(EfeitoStatus.ENVENENADO);
			System.out.println("\n[STATUS] " + defensor.getNome() + " ficou ENVENENADO pelo ataque!");
		}
	}

}