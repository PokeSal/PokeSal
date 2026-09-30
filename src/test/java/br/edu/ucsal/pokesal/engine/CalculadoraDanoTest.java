package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.ucsal.pokesal.model.Mochila;
import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Suíte de testes unitários automatizados para o motor de combate e regras do PokeSal.
 */
public class CalculadoraDanoTest {

  private PokeSal fogo;
  private PokeSal planta;
  private PokeSal agua;

  /**
   * Configuração inicial executada antes de cada caso de teste.
   */
  @BeforeEach
  public void setUp() {
    fogo = new PokeSal(TipoPokesal.CHARSAL);
    planta = new PokeSal(TipoPokesal.BULBASAL);
    agua = new PokeSal(TipoPokesal.SQUIRTSAL);
  }

  @Test
  @DisplayName("Validar multiplicadores de efetividade elemental (2.0x, 0.5x, 1.0x)")
  public void testVantagemElemental() {
    assertEquals(2.0, CalculadoraDano.calcularEfetividade(fogo, planta));
    assertEquals(0.5, CalculadoraDano.calcularEfetividade(fogo, agua));
    assertEquals(1.0, CalculadoraDano.calcularEfetividade(fogo, fogo));
  }

  @Test
  @DisplayName("Validar modificadores de dano e cura por efeito de terreno")
  public void testEfeitoTerrenoEstacionamentoUcsal() {
    assertEquals(1.15, CalculadoraDano.calcularMultiplicadorTerreno(fogo, Terreno.ASFALTO_QUENTE));
    assertEquals(1.10, CalculadoraDano.calcularMultiplicadorTerreno(agua, Terreno.POCA_CHUVA));

    planta.receberDano(20);
    int hpAntes = planta.getHpAtual();

    GerenciadorDeBatalha gerenciador =
        new GerenciadorDeBatalha(planta, fogo, Terreno.CANTEIRO_CENTRAL);
    gerenciador.aplicarCuraCanteiroCentral();

    assertEquals(hpAntes + 5, planta.getHpAtual());
  }

  @Test
  @DisplayName("Validar ordem de iniciativa do turno com base no atributo SPD")
  public void testOrdemDeAtaquePorVelocidade() {
    GerenciadorDeBatalha gerenciador =
        new GerenciadorDeBatalha(planta, agua, Terreno.CANTEIRO_CENTRAL);
    gerenciador.definirOrdemAtuacao();

    assertEquals(planta, gerenciador.getPrimeiroAtacante());
    assertEquals(agua, gerenciador.getSegundoAtacante());
  }

  @Test
  @DisplayName("Validar disparo de exceção ao tentar exceder limite de 2 itens na mochila")
  public void testUsoLimiteDeItensExcedido() {
    Mochila mochila = new Mochila();

    assertTrue(mochila.podeUsarItem());
    assertNotNull(mochila.usarItemPorIndice(0));

    assertTrue(mochila.podeUsarItem());
    assertNotNull(mochila.usarItemPorIndice(0));

    assertFalse(mochila.podeUsarItem());

    assertThrows(IllegalStateException.class, () -> {
      mochila.usarItemPorIndice(0);
    });
  }

  @Test
  @DisplayName("Validar valores limite (boundary values) de ATK, DEF e HP")
  public void testCalculoDanoBoundaryValues() {
    PokeSal defensorPlanta = new PokeSal(TipoPokesal.CHIKOSAL);
    int danoAtkMax = CalculadoraDano.calcularDano(planta, defensorPlanta, Terreno.CANTEIRO_CENTRAL);
    assertEquals(9, danoAtkMax);

    planta.receberDano(9999);
    assertEquals(0, planta.getHpAtual());
  }

  @Test
  @DisplayName("Requisito Autoral 1: Validar dano de recuo após 4 ataques consecutivos")
  public void testDanoRecuo() {
    final int hpInicial = fogo.getHpAtual();
    GerenciadorDeBatalha batalha = new GerenciadorDeBatalha(fogo, planta, Terreno.ASFALTO_QUENTE);

    for (int i = 0; i < 3; i++) {
      batalha.processarRecuoPorAtaque(fogo);
    }
    assertEquals(hpInicial, fogo.getHpAtual());

    batalha.processarRecuoPorAtaque(fogo);
    int danoRecuo = (int) Math.round(fogo.getHpMaximo() * 0.10);
    assertEquals(hpInicial - danoRecuo, fogo.getHpAtual());
  }

  @Test
  @DisplayName("Requisito Autoral 3: Validar ativação da passiva defensiva com HP <= 20%")
  public void testPassivaDefensivaBaixoHp() {
    int defesaInicial = fogo.getDef();
    int danoAtePassiva = fogo.getHpMaximo() - 19;

    fogo.receberDano(danoAtePassiva);

    assertEquals(19, fogo.getHpAtual());
    int defesaEsperada = (int) Math.round(defesaInicial * 1.20);
    assertEquals(defesaEsperada, fogo.getDef());
  }
}
