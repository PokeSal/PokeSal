package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.Clock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.Terreno;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.repository.RepositorioResultados;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;

@ExtendWith(MockitoExtension.class)	
public class MecanicasBatalhaTest {
	@Mock
	private GeradorAleatorio geradorMock;
	@Mock
	private ServicoTerreno servicoTerrenoMock;
	@Mock
	private Clock clockMock;
	@Mock
	private RepositorioResultados repositorioMock;
	@Mock
	private RegistroBatalha registroBatalhaMock;
	private PokeSal planta = new PokeSal(TipoPokesal.BULBASAL);
	private PokeSal fogo = new PokeSal(TipoPokesal.CHARSAL);
	private Terreno terreno = Terreno.ASFALTO_QUENTE;
	private CalculadoraDano calculadoraDano;
	private GerenciadorDeBatalha gerenciador;
	
	@BeforeEach
	void setUp() {
		fogo = new PokeSal(TipoPokesal.CHARSAL);
		planta = new PokeSal(TipoPokesal.BULBASAL);
		terreno = Terreno.ASFALTO_QUENTE;
		calculadoraDano = new CalculadoraDano(geradorMock, servicoTerrenoMock);
		gerenciador = new GerenciadorDeBatalha(
			    fogo, 
			    planta, 
			    geradorMock, 
			    servicoTerrenoMock, 
			    clockMock, 
			    registroBatalhaMock, 
			    repositorioMock
			);
	
		
	}
	
	@Test
	@DisplayName("Validar dano multiplicativo (x1.5) em caso de acerto crítico")
	void testAcertoCriticoComGeradorMockado() {
	  when(geradorMock.gerarNumero(1, 100)).thenReturn(1);
	  when(geradorMock.testarChance(ConstantesJogo.CHANCE_CRITICO)).thenReturn(true);
	  when(servicoTerrenoMock.obterTerrenoAtual(clockMock)).thenReturn(terreno);

	  int danoObtido = calculadoraDano.calcularDano(fogo, planta, clockMock);

	  double danoBase = ((double) fogo.getAtk() / planta.getDef()) * ConstantesJogo.DANO_BASE;
	  double efetividade = 2.0;
	  double danoTerreno = 1.15;

	  int danoSemCritico = (int) Math.round(danoBase * efetividade * danoTerreno);
	  int danoEsperado = (int) Math.round(danoSemCritico * ConstantesJogo.MULTIPLICADOR_CRITICO);

	  assertEquals(danoEsperado, danoObtido);
	}
	
	@Test
	@DisplayName("Validar dano zero e HP inalterado quando a precisão falha")
	void testGolpeErraQuandoPrecisaoFalha() {
		when(geradorMock.gerarNumero(1, 100)).thenReturn(99);
		int hpInicial = planta.getHpAtual();
		int danoObtido = calculadoraDano.calcularDano(fogo, planta, clockMock);
		planta.receberDano(danoObtido);
		
		assertEquals(ConstantesJogo.DANO_ERRO, danoObtido);
		assertEquals(hpInicial, planta.getHpAtual());
	}
	
	@Test
	@DisplayName("Validar ativação do bônus de terreno Asfalto Quente apenas durante o dia")
	void testAsfaltoQuenteApenasDuranteDia() {
		when(geradorMock.gerarNumero(1, 100)).thenReturn(1);
		when(servicoTerrenoMock.obterTerrenoAtual(clockMock)).thenReturn(Terreno.ASFALTO_QUENTE);
		
		int danoDia = calculadoraDano.calcularDano(fogo, planta, clockMock);
		when(servicoTerrenoMock.obterTerrenoAtual(clockMock)).thenReturn(Terreno.CANTEIRO_CENTRAL);
		int danoNoite = calculadoraDano.calcularDano(fogo, planta, clockMock);
		
		assertTrue(danoDia>danoNoite);
		
		
	}
	
	@Test
	@DisplayName("Validar a ordem exata de chamadas ao RegistroBatalha usando InOrder")
	void testSequenciaDeEventosNoTurno() {
		InOrder inOrder = Mockito.inOrder(repositorioMock);
		gerenciador.executarTurnoAtaque(fogo, planta);
		inOrder.verify(repositorioMock).registrarDano(any(), anyInt());
		inOrder.verify(repositorioMock).registrarTurnosPartida(anyInt());
		inOrder.verifyNoMoreInteractions();
		
	}

}
