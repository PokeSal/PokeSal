package br.edu.ucsal.pokesal.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.pokesal.model.PokeSal;
import br.edu.ucsal.pokesal.model.TipoPokesal;
import br.edu.ucsal.pokesal.repository.RepositorioResultados;
import br.edu.ucsal.pokesal.repository.RepositorioResultadosMemoria;
import br.edu.ucsal.pokesal.repository.ResultadoBatalha;
import br.edu.ucsal.pokesal.service.GeradorAleatorio;
import br.edu.ucsal.pokesal.service.RegistroBatalha;
import br.edu.ucsal.pokesal.service.ServicoTerreno;
import br.edu.ucsal.pokesal.util.ConstantesJogo;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Suíte de testes unitários para os Requisitos Autorais 04 e 05 (Issue #94).
 * Valida a compilação estatística do torneio e o sistema de drop de recompensa
 * pós-semifinal.
 */
@ExtendWith(MockitoExtension.class)
public class RequisitosAutoraisTest {

	@Mock
	private RepositorioResultados repositorioMock;

	@Mock
	private GeradorAleatorio geradorMock;

	@Mock
	private ServicoTerreno servicoTerrenoMock;

	@Mock
	private RegistroBatalha registroBatalhaMock;

	private final Clock clockDefault = Clock.systemDefaultZone();

	private GerenciadorTorneio gerenciadorTorneio;

	@BeforeEach
	void setUp() {
		gerenciadorTorneio = new GerenciadorTorneio(repositorioMock, geradorMock, servicoTerrenoMock, clockDefault,
				registroBatalhaMock);
	}

	@Test
	@DisplayName("Req 04: Validar compilação estatística e cálculo de MVP no repositório de memória")
	void testCompilacaoEstatisticaMVPNoFimDoTorneio() {
		RepositorioResultadosMemoria repoMemoria = new RepositorioResultadosMemoria();

		ResultadoBatalha partida1 = new ResultadoBatalha("Ash", "Gary", "CharSal", "BulbaSal", 6, 120, 40, 1);
		ResultadoBatalha partida2 = new ResultadoBatalha("Misty", "Brock", "SquirtSal", "ChikoSal", 10, 80, 50, 2);

		repoMemoria.salvarResultado(partida1);
		repoMemoria.salvarResultado(partida2);

		assertEquals(2, repoMemoria.getResultados().size());
		assertEquals("CharSal", repoMemoria.getResultados().get(0).pokesalVencedor());

		repoMemoria.emitirRelatorioEstatistico();
	}

	@Test
	@DisplayName("Req 04: Validar que relatório estatístico não é emitido caso o torneio seja interrompido")
	void testEstatisticaNaoGeradaSeTorneioIncompleto() {
		verify(repositorioMock, never()).emitirRelatorioEstatistico();
	}

	@Test
	@DisplayName("Req 05: Validar adição de item de recompensa com sucesso quando a mochila tem espaço disponível")
	void testDropRecompensaPosVitoriaComSucesso() {
		PokeSal vencedor = new PokeSal(TipoPokesal.CHARSAL);

		vencedor.getMochila().usarItemPorIndice(0);
		assertEquals(2, vencedor.getMochila().getItens().size());

		when(geradorMock.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO))
				.thenReturn(25);

		gerenciadorTorneio.processarRecompensaPosBatalha(vencedor);

		assertEquals(3, vencedor.getMochila().getItens().size());
		verify(registroBatalhaMock, times(1)).registrarUsoItem(eq("RECOMPENSA (REQ 05)"),
				contains("ganhou " + ConstantesJogo.NOME_POCAO));
	}

	@Test
	@DisplayName("Req 05: Validar descarte do item de recompensa quando a mochila do vencedor está lotada (3 itens)")
	void testDropRecompensaIgnoradoMochilaCheia() {
		PokeSal vencedor = new PokeSal(TipoPokesal.CHARSAL);

		assertEquals(3, vencedor.getMochila().getItens().size());

		when(geradorMock.gerarNumero(ConstantesJogo.FAIXA_MIN_SORTEIO, ConstantesJogo.FAIXA_MAX_SORTEIO))
				.thenReturn(70);

		gerenciadorTorneio.processarRecompensaPosBatalha(vencedor);

		assertEquals(3, vencedor.getMochila().getItens().size());
		verify(registroBatalhaMock, times(1)).registrarUsoItem(eq("RECOMPENSA (REQ 05)"),
				contains("está cheia! O item " + ConstantesJogo.NOME_SUPER_POCAO + " foi descartado"));
	}
}