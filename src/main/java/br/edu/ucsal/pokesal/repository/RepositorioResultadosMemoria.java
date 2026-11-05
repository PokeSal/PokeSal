package br.edu.ucsal.pokesal.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação em memória do repositório para exibição e armazenamento
 * temporário dos resultados.
 */
public class RepositorioResultadosMemoria implements RepositorioResultados {

	private final List<ResultadoBatalha> resultados = new ArrayList<>();

	@Override
	public void salvarResultado(ResultadoBatalha resultado) {
		resultados.add(resultado);
		System.out.println("[REPOSITÓRIO] Resultado registrado com sucesso: Vencedor " + resultado.treinadorVencedor()
				+ " (" + resultado.quantidadeTurnos() + " turnos)");
	}

	public List<ResultadoBatalha> getResultados() {
		return resultados;
	}
}