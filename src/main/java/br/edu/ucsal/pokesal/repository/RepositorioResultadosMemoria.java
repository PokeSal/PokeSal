package br.edu.ucsal.pokesal.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// É bom pensar se essas estatísticas estao boas pra pegar ou se podemos trocar por outras

/** Implementação em memória do repositório com cálculo estatístico acumulado. */
public class RepositorioResultadosMemoria implements RepositorioResultados {

  private final List<ResultadoBatalha> resultados = new ArrayList<>();

  @Override
  public void salvarResultado(ResultadoBatalha resultado) {
    resultados.add(resultado);
    System.out.println("[REPOSITÓRIO] Resultado registrado: Vencedor "
        + resultado.treinadorVencedor() + " (" + resultado.quantidadeTurnos() + " turnos)");
  }

  @Override
  public void emitirRelatorioEstatistico() {
    System.out.println("\n📊 ==================================================");
    System.out.println("   RELATÓRIO ESTATÍSTICO DO TORNEIO (REQ AUTORAL 04)");
    System.out.println("==================================================");

    if (resultados.isEmpty()) {
      System.out.println("Nenhuma partida foi registrada no repositório.");
      System.out.println("==================================================\n");
      return;
    }

    Map<String, Integer> danoPorPokesal = new HashMap<>();
    for (ResultadoBatalha res : resultados) {
      danoPorPokesal.put(res.pokesalVencedor(),
          danoPorPokesal.getOrDefault(res.pokesalVencedor(), 0) + res.danoCausadoVencedor());
      danoPorPokesal.put(res.pokesalPerdedor(),
          danoPorPokesal.getOrDefault(res.pokesalPerdedor(), 0) + res.danoCausadoPerdedor());
    }

    Map.Entry<String, Integer> mvp =
        danoPorPokesal.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);

    ResultadoBatalha partidaMaisLonga = resultados.stream()
        .max(Comparator.comparingInt(ResultadoBatalha::quantidadeTurnos)).orElse(resultados.get(0));

    int totalItensUsados = resultados.stream().mapToInt(ResultadoBatalha::itensUsadosPartida).sum();

    System.out.println("⭐ MVP do Torneio: "
        + (mvp != null ? mvp.getKey() + " (" + mvp.getValue() + " de dano total)" : "N/A"));
    System.out.println("⏱️  Partida Mais Duradoura: " + partidaMaisLonga.treinadorVencedor()
        + " VS " + partidaMaisLonga.treinadorPerdedor() + " (" + partidaMaisLonga.quantidadeTurnos()
        + " turnos)");
    System.out.println("🎒 Consumo de Suprimentos: " + totalItensUsados + " item(ns) utilizado(s)");
    System.out.println("==================================================\n");
  }

  public List<ResultadoBatalha> getResultados() {
    return resultados;
  }
}
