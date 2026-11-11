package br.edu.ucsal.pokesal.service;

import br.edu.ucsal.pokesal.model.Terreno;
import java.time.Clock;
import java.time.LocalTime;

/** Implementação concreta do serviço de terreno baseada em horário. */
public class ServicoTerrenoImpl implements ServicoTerreno {

  @Override
  public Terreno obterTerrenoAtual(Clock clock) {
    LocalTime horaAtual = LocalTime.now(clock);
    boolean ehDia =
        horaAtual.isAfter(LocalTime.of(6, 0)) && horaAtual.isBefore(LocalTime.of(18, 0));
    return ehDia ? Terreno.ASFALTO_QUENTE : Terreno.POCA_CHUVA;
  }
}
