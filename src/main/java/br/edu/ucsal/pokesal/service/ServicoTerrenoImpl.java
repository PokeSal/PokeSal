package br.edu.ucsal.pokesal.service;

import br.edu.ucsal.pokesal.model.Terreno;
import java.time.Clock;
import java.time.LocalTime;

/** Implementação concreta do serviço de terreno baseada em horário. */
public class ServicoTerrenoImpl implements ServicoTerreno {

  private static final LocalTime INICIO_ASFALTO_QUENTE = LocalTime.of(6, 0);
  private static final LocalTime FIM_ASFALTO_QUENTE = LocalTime.of(14, 0);
  private static final LocalTime FIM_CANTEIRO_CENTRAL = LocalTime.of(18, 0);

  @Override
  public Terreno obterTerrenoAtual(Clock clock) {
    LocalTime horaAtual = LocalTime.now(clock);
    if (!horaAtual.isBefore(INICIO_ASFALTO_QUENTE) && horaAtual.isBefore(FIM_ASFALTO_QUENTE)) {
      return Terreno.ASFALTO_QUENTE;
    }
    if (!horaAtual.isBefore(FIM_ASFALTO_QUENTE) && horaAtual.isBefore(FIM_CANTEIRO_CENTRAL)) {
      return Terreno.CANTEIRO_CENTRAL;
    }
    return Terreno.POCA_CHUVA;
  }
}

