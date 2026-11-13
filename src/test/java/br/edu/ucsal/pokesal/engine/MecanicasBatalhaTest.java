package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Suíte de testes unitários isolados com Mockito para as mecânicas internas de combate.
 */
@ExtendWith(MockitoExtension.class)
public class MecanicasBatalhaTest {

  @Mock
  private GeradorAleatorio geradorMock;

  @Mock
  private ServicoTerreno servicoTerrenoMock;

  @Mock
  private RegistroBatalha registroBatalhaMock;

  @InjectMocks
  private CalculadoraDano calculadoraDano;

  private final Clock clockDefault = Clock.systemDefaultZone();

  private PokeSal fogo;
  private PokeSal planta;
  private GerenciadorDeBatalha gerenciador;

  @BeforeEach
  void setUp() {
    fogo = new PokeSal(TipoPokesal.CHARSAL);
    planta = new PokeSal(TipoPokesal.BULBASAL);
    gerenciador = new GerenciadorDeBatalha(fogo, planta, geradorMock, servicoTerrenoMock,
        clockDefault, registroBatalhaMock);
  }

  @Test
  @DisplayName("Validar dano multiplicativo (x1.5) quando o gerador retorna acerto crítico")
  void testAcertoCriticoComGeradorMockado() {
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
    when(geradorMock.testarChance(ConstantesJogo.CHANCE_CRITICO)).thenReturn(true);
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);

    int danoObtido = calculadoraDano.calcularDano(fogo, planta, clockDefault);

    double danoBase = ((double) fogo.getAtk() / planta.getDef()) * ConstantesJogo.DANO_BASE;
    double efetividade = 2.0;
    double danoTerreno = 1.15;

    int danoSemCritico = (int) Math.round(danoBase * efetividade * danoTerreno);
    int danoEsperado = (int) Math.round(danoSemCritico * ConstantesJogo.MULTIPLICADOR_CRITICO);

    assertEquals(danoEsperado, danoObtido);
  }

  @Test
  @DisplayName("Validar dano zero e HP do alvo inalterado quando a precisão falha")
  void testGolpeErraQuandoPrecisaoFalha() {
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(99);
    int hpInicial = planta.getHpAtual();

    int danoObtido = calculadoraDano.calcularDano(fogo, planta, clockDefault);
    planta.receberDano(danoObtido);

    assertEquals(ConstantesJogo.DANO_ERRO, danoObtido);
    assertEquals(hpInicial, planta.getHpAtual());
  }

  @Test
  @DisplayName("Validar ativação do bônus de terreno Asfalto Quente apenas durante o dia")
  void testAsfaltoQuenteApenasDuranteDia() {
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);

    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
    int danoDia = calculadoraDano.calcularDano(fogo, planta, clockDefault);

    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.CANTEIRO_CENTRAL);
    int danoNoite = calculadoraDano.calcularDano(fogo, planta, clockDefault);

    assertTrue(danoDia > danoNoite);
  }

  @Test
  @DisplayName("Validar retornos em sequência com thenReturn() e verificação com atLeastOnce()")
  void testRetornosEmSequenciaEAtLeastOnce() {
    // Configura precisão sempre com sucesso (1)
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
    // Retorno em sequência: 1º ataque normal (sem crítico), 2º ataque com crítico
    when(geradorMock.testarChance(ConstantesJogo.CHANCE_CRITICO)).thenReturn(false, true);
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);

    int danoNormal = calculadoraDano.calcularDano(fogo, planta, clockDefault);
    int danoCritico = calculadoraDano.calcularDano(fogo, planta, clockDefault);

    assertTrue(danoCritico > danoNormal);
    assertEquals((int) Math.round(danoNormal * ConstantesJogo.MULTIPLICADOR_CRITICO), danoCritico);

    // Executa turno com o gerenciador para registrar eventos
    gerenciador.executarTurnoAtaque(fogo, planta);

    // Validação com atLeastOnce() conforme exigido pelo edital
    verify(registroBatalhaMock, atLeastOnce()).registrarAtaque(eq(fogo.getNome()), eq(planta.getNome()));
    verify(registroBatalhaMock, atLeastOnce()).registrarDano(eq(planta.getNome()), anyInt(), anyBoolean(), anyBoolean());
  }

  @Test
  @DisplayName("Validar a ordem exata de chamadas ao RegistroBatalha: ataque -> dano -> status -> terreno -> item")
  void testSequenciaDeEventosNoTurno() {
    when(geradorMock.gerarNumero(
        ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
    when(geradorMock.testarChance(ConstantesJogo.CHANCE_CRITICO)).thenReturn(false);
    when(geradorMock.testarChance(ConstantesJogo.CHANCE_EFEITO_STATUS)).thenReturn(true);

    // Prepara o atacante para que o próximo ataque seja o 4º consecutivo (aciona recuo - efeito de terreno/ambiente)
    fogo.incrementarAtaquesConsecutivos();
    fogo.incrementarAtaquesConsecutivos();
    fogo.incrementarAtaquesConsecutivos();

    // Planta perde HP para poder usar a poção
    planta.receberDano(30);

    InOrder inOrder = inOrder(registroBatalhaMock);

    // 1. Executa turno de ataque (dispara ataque, dano, status e recuo/terreno)
    gerenciador.executarTurnoAtaque(fogo, planta);

    // 2. Executa turno de item
    gerenciador.executarTurnoItem(planta, "Ash", 0);

    // Valida a ordem exata exigida pelo edital: ataque -> dano -> status -> terreno -> item
    inOrder.verify(registroBatalhaMock).registrarAtaque(fogo.getNome(), planta.getNome());
    inOrder.verify(registroBatalhaMock).registrarDano(eq(planta.getNome()), anyInt(), anyBoolean(), anyBoolean());
    inOrder.verify(registroBatalhaMock).registrarEfeitoStatus(eq(planta.getNome()), any());
    inOrder.verify(registroBatalhaMock).registrarEfeitoTerreno(anyString());
    inOrder.verify(registroBatalhaMock).registrarUsoItem(eq("Ash"), anyString());
  }

  @Test
  @DisplayName("Validar ramos de controle do GerenciadorDeBatalha: paralisia, iniciativa e terreno")
  void testCaminhosDeControleBatalha() {
    when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.CANTEIRO_CENTRAL);

    // 1. Iniciativa quando pokesal2 é mais veloz que pokesal1
    PokeSal veloz = new PokeSal(TipoPokesal.CHARSAL); // SPD 65
    PokeSal lento = new PokeSal(TipoPokesal.SQUIRTSAL); // SPD 43
    GerenciadorDeBatalha batalhaVel = new GerenciadorDeBatalha(
        lento, veloz, geradorMock, servicoTerrenoMock, clockDefault, registroBatalhaMock);
    batalhaVel.definirOrdemAtuacao();
    assertEquals(veloz, batalhaVel.getPrimeiroAtacante());
    assertEquals(lento, batalhaVel.getSegundoAtacante());

    // 2. Paralisia impedindo ação no turno de ataque e no turno de item
    fogo.setStatusAtual(br.edu.ucsal.pokesal.model.EfeitoStatus.PARALISADO);
    assertTrue(gerenciador.executarTurnoAtaque(fogo, planta));
    assertEquals(br.edu.ucsal.pokesal.model.EfeitoStatus.NENHUM, fogo.getStatusAtual());

    planta.setStatusAtual(br.edu.ucsal.pokesal.model.EfeitoStatus.PARALISADO);
    assertTrue(gerenciador.executarTurnoItem(planta, "Ash", 0));
    assertEquals(br.edu.ucsal.pokesal.model.EfeitoStatus.NENHUM, planta.getStatusAtual());

    // 3. Uso de item quando HP já está cheio (retorna false)
    assertFalse(gerenciador.executarTurnoItem(fogo, "Gary", 0));

    // 4. Cura do Canteiro Central para Planta com dano
    planta.receberDano(20);
    int hpAntes = planta.getHpAtual();
    gerenciador.aplicarCuraCanteiroCentral();
    assertTrue(planta.getHpAtual() > hpAntes);
  }
}
