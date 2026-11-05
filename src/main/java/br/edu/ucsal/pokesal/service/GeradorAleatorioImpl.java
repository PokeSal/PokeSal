package br.edu.ucsal.pokesal.service;

import java.util.Random;

public class GeradorAleatorioImpl implements GeradorAleatorio {

	private final Random random;
	public GeradorAleatorioImpl(Random random) {
	this.random = random;
	}
	
	
	@Override
	public int gerarNumero(int min, int max) {
		return min + random.nextInt((max - min) + 1);
	}

	@Override
	public double gerarDouble() {
		return random.nextDouble();
	}


	@Override
	public boolean testarChance(double percentual) {
		return this.random.nextDouble() < percentual;
	}

}
