package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.ucsal.pokesal.model.Mochila;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes unitários para o cálculo de dano e regras do PokeSal.
 */
public class CalculadoraDanoTest {

  @Test
  public void testVantagemElemental() {
    PokeSal fogo = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal planta = new PokeSal(TipoPokesal.BULBASAL);

    // Fogo possui vantagem contra Planta
    assertEquals(2.0, CalculadoraDano.calcularEfetividade(fogo, planta));

    // Fogo possui desvantagem contra Água
    PokeSal agua = new PokeSal(TipoPokesal.SQUIRTSAL);
    assertEquals(0.5, CalculadoraDano.calcularEfetividade(fogo, agua));

    // Pokésais do mesmo tipo possuem efetividade neutra
    PokeSal fogo2 = new PokeSal(TipoPokesal.CYNDASAL);
    assertEquals(1.0, CalculadoraDano.calcularEfetividade(fogo, fogo2));
  }

  @Test
  public void testEfeitoTerrenoEstacionamentoUcsal() {
    PokeSal fogo = new PokeSal(TipoPokesal.CHARSAL);

    // Asfalto quente aumenta o dano dos ataques de Fogo em 15%
    assertEquals(1.15, CalculadoraDano.calcularMultiplicadorTerreno(fogo, Terreno.ASFALTO_QUENTE));

    PokeSal pokesal1 = new PokeSal(TipoPokesal.BULBASAL);
    PokeSal pokesal2 = new PokeSal(TipoPokesal.CHARSAL);

    // O Canteiro Central recupera HP de Pokésais do tipo Planta
    pokesal1.receberDano(20);

    int hpAntes = pokesal1.getHpAtual();

    GerenciadorDeBatalha gerenciador =
        new GerenciadorDeBatalha(pokesal1, pokesal2, Terreno.CANTEIRO_CENTRAL);

    gerenciador.aplicarCuraCanteiroCentral();

    int hpDepois = pokesal1.getHpAtual();

    // BulbaSal possui 95 HP máximo e recebe 5% de cura, totalizando 5 HP
    assertEquals(hpAntes + 5, hpDepois);

    PokeSal agua = new PokeSal(TipoPokesal.SQUIRTSAL);

    // Poça de chuva aumenta o dano dos ataques de Água em 10%
    assertEquals(1.10, CalculadoraDano.calcularMultiplicadorTerreno(agua, Terreno.POCA_CHUVA));
  }

  @Test
  public void testOrdemDeAtaquePorVelocidade() {
    PokeSal pokesal1 = new PokeSal(TipoPokesal.BULBASAL);
    PokeSal pokesal2 = new PokeSal(TipoPokesal.TOTOSAL);

    GerenciadorDeBatalha gerenciador =
        new GerenciadorDeBatalha(pokesal1, pokesal2, Terreno.CANTEIRO_CENTRAL);

    // BulbaSal possui SPD maior que TotoSal e deve atacar primeiro
    gerenciador.definirOrdemAtuacao();

    assertEquals(pokesal1, gerenciador.getPrimeiroAtacante());
    assertEquals(pokesal2, gerenciador.getSegundoAtacante());

    // Executa novamente para confirmar que a ordem continua baseada na SPD
    GerenciadorDeBatalha gerenciador2 =
        new GerenciadorDeBatalha(pokesal1, pokesal2, Terreno.CANTEIRO_CENTRAL);

    gerenciador2.definirOrdemAtuacao();

    assertEquals(pokesal1, gerenciador2.getPrimeiroAtacante());
    assertEquals(pokesal2, gerenciador2.getSegundoAtacante());
  }

  @Test
  public void testUsoLimiteDeItensExcedido() {

    Mochila mochila = new Mochila();

    // O primeiro item pode ser utilizado normalmente
    assertTrue(mochila.podeUsarItem());
    assertNotNull(mochila.usarItemPorIndice(0));

    // O segundo item também pode ser utilizado
    assertTrue(mochila.podeUsarItem());
    assertNotNull(mochila.usarItemPorIndice(0));

    // Após dois usos, o limite de itens da batalha foi atingido
    assertFalse(mochila.podeUsarItem());

    // Uma terceira tentativa deve ser recusada
    assertNull(mochila.usarItemPorIndice(0));
  }

  @Test
  public void testCalculoDanoBoundaryValues() {

    // ATK máximo: BulbaSal possui 57 de ATK.
    PokeSal atacanteAtkMax = new PokeSal(TipoPokesal.BULBASAL);
    PokeSal defensorAtkMax = new PokeSal(TipoPokesal.CHIKOSAL);

    int danoAtkMax =
        CalculadoraDano.calcularDano(atacanteAtkMax, defensorAtkMax, Terreno.CANTEIRO_CENTRAL);

    assertEquals(9, danoAtkMax);

    // ATK mínimo: SquirtSal possui 49 de ATK.
    PokeSal atacanteAtkMin = new PokeSal(TipoPokesal.SQUIRTSAL);
    PokeSal defensorAtkMin = new PokeSal(TipoPokesal.TOTOSAL);

    int danoAtkMin =
        CalculadoraDano.calcularDano(atacanteAtkMin, defensorAtkMin, Terreno.CANTEIRO_CENTRAL);

    assertEquals(7, danoAtkMin);

    // DEF máximo: 67.
    PokeSal atacanteDefMax = new PokeSal(TipoPokesal.CHARSAL);
    PokeSal defensorDefMax = new PokeSal(TipoPokesal.CYNDASAL);

    int danoDefMax =
        CalculadoraDano.calcularDano(atacanteDefMax, defensorDefMax, Terreno.CANTEIRO_CENTRAL);

    assertEquals(8, danoDefMax);

    // DEF mínimo: 65.
    PokeSal atacanteDefMin = new PokeSal(TipoPokesal.SQUIRTSAL);
    PokeSal defensorDefMin = new PokeSal(TipoPokesal.CHIKOSAL);

    int danoDefMin =
        CalculadoraDano.calcularDano(atacanteDefMin, defensorDefMin, Terreno.CANTEIRO_CENTRAL);

    assertEquals(4, danoDefMin);

    // HP mínimo: o HP nunca pode ficar abaixo de zero.
    PokeSal pokeSal = new PokeSal(TipoPokesal.BULBASAL);

    pokeSal.receberDano(9999);

    assertEquals(0, pokeSal.getHpAtual());
  }

  // Requisito Autoral 1: Dano de recuo após ataques consecutivos
  @Test
  public void testDanoRecuo() {

    PokeSal atacante = new PokeSal(TipoPokesal.CHARSAL);
    final int hpInicial = atacante.getHpAtual();

    // Os três primeiros ataques não devem causar dano de recuo.
    GerenciadorDeBatalha batalha = new GerenciadorDeBatalha(atacante,
        new PokeSal(TipoPokesal.BULBASAL), Terreno.ASFALTO_QUENTE);

    batalha.processarRecuoPorAtaque(atacante);
    batalha.processarRecuoPorAtaque(atacante);
    batalha.processarRecuoPorAtaque(atacante);

    assertEquals(hpInicial, atacante.getHpAtual());

    // O quarto ataque ativa o dano de recuo.
    batalha.processarRecuoPorAtaque(atacante);

    final int hpMaximo = atacante.getHpMaximo();
    int danoRecuo = (int) Math.round(hpMaximo * 0.10);

    assertEquals(hpInicial - danoRecuo, atacante.getHpAtual());
  }

  // Requisito Autoral 03: Passiva Defensiva de Baixo HP
  @Test
  public void testPassivaDefensivaBaixoHp() {

    PokeSal pokeSal = new PokeSal(TipoPokesal.CHARSAL);

    int defesaInicial = pokeSal.getDef();
    final int hpMaximo = pokeSal.getHpMaximo();

    // Reduz o HP para 19, ficando abaixo do limite de 20%.
    int dano = hpMaximo - 19;
    pokeSal.receberDano(dano);

    assertEquals(19, pokeSal.getHpAtual());

    // A defesa deve receber o bônus permanente de 20%.
    int defesaEsperada = (int) Math.round(defesaInicial * 1.20);

    assertEquals(defesaEsperada, pokeSal.getDef());

    // A passiva deve ser ativada somente uma vez durante a batalha.
    pokeSal.receberDano(1);

    assertEquals(defesaEsperada, pokeSal.getDef());
  }
}
