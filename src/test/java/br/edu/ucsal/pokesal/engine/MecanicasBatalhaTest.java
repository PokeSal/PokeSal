package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Suíte de testes unitários isolados com Mockito para as mecânicas internas de
 * combate (Issue #92). Conforme exigido no edital da Fase 03 (Seção 4.3).
 */
@ExtendWith(MockitoExtension.class)
public class MecanicasBatalhaTest {

	@Mock
	private GeradorAleatorio geradorMock;

	@Mock
	private ServicoTerreno servicoTerrenoMock;

	@Mock
	private RegistroBatalha registroBatalhaMock;

	private final Clock clockDefault = Clock.systemDefaultZone();

	private PokeSal fogo;
	private PokeSal planta;
	private CalculadoraDano calculadoraDano;
	private GerenciadorDeBatalha gerenciador;

	@BeforeEach
	void setUp() {
		fogo = new PokeSal(TipoPokesal.CHARSAL);
		planta = new PokeSal(TipoPokesal.BULBASAL);
		calculadoraDano = new CalculadoraDano(geradorMock, servicoTerrenoMock);
		gerenciador = new GerenciadorDeBatalha(fogo, planta, geradorMock, servicoTerrenoMock, clockDefault,
				registroBatalhaMock);
	}

	@Test
	@DisplayName("Validar dano multiplicativo (x1.5) quando o gerador retorna acerto crítico")
	void testAcertoCriticoComGeradorMockado() {
		when(geradorMock.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
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
		when(geradorMock.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO))
				.thenReturn(99);
		int hpInicial = planta.getHpAtual();

		int danoObtido = calculadoraDano.calcularDano(fogo, planta, clockDefault);
		planta.receberDano(danoObtido);

		assertEquals(ConstantesJogo.DANO_ERRO, danoObtido);
		assertEquals(hpInicial, planta.getHpAtual());
	}

	@Test
	@DisplayName("Validar ativação do bônus de terreno Asfalto Quente apenas durante o dia")
	void testAsfaltoQuenteApenasDuranteDia() {
		when(geradorMock.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);

		when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);
		int danoDia = calculadoraDano.calcularDano(fogo, planta, clockDefault);

		when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.CANTEIRO_CENTRAL);
		int danoNoite = calculadoraDano.calcularDano(fogo, planta, clockDefault);

		assertTrue(danoDia > danoNoite);
	}

	@Test
	@DisplayName("Validar a ordem exata de chamadas ao RegistroBatalha usando InOrder")
	void testSequenciaDeEventosNoTurno() {
		when(geradorMock.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO)).thenReturn(1);
		when(servicoTerrenoMock.obterTerrenoAtual(clockDefault)).thenReturn(Terreno.ASFALTO_QUENTE);

		InOrder inOrder = inOrder(registroBatalhaMock);

		gerenciador.executarTurnoAtaque(fogo, planta);

		inOrder.verify(registroBatalhaMock).registrarAtaque(fogo.getNome(), planta.getNome());
		inOrder.verify(registroBatalhaMock).registrarDano(eq(planta.getNome()), anyInt(), anyBoolean(), anyBoolean());
	}
}