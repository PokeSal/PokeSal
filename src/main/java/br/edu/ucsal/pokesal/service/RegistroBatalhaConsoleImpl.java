package br.edu.ucsal.pokesal.service;

import br.edu.ucsal.pokesal.model.EfeitoStatus;

/** Implementação do auditor de batalhas direcionando as mensagens para o console. */
public class RegistroBatalhaConsoleImpl implements RegistroBatalha {

  @Override
  public void registrarAtaque(String atacante, String defensor) {
    System.out.println("\n[AÇÃO] " + atacante + " atacou " + defensor + "!");
  }

  @Override
  public void registrarDano(String defensor, int dano, boolean critico, boolean errou) {
    if (errou) {
      System.out.println("[DANO] O ataque errou o alvo!");
    } else {
      String mensagem = "[DANO] " + defensor + " sofreu " + dano + " de dano";
      if (critico) {
        mensagem += " (ACERTO CRÍTICO!)";
      }
      System.out.println(mensagem);
    }
  }

  @Override
  public void registrarEfeitoStatus(String pokesal, EfeitoStatus status) {
    System.out.println("[STATUS] " + pokesal + " ficou " + status + "!");
  }

  @Override
  public void registrarEfeitoTerreno(String mensagem) {
    System.out.println("[TERRENO] " + mensagem);
  }

  @Override
  public void registrarUsoItem(String treinador, String item) {
    System.out.println("[MOCHILA] " + treinador + " usou o item " + item + "!");
  }
}
