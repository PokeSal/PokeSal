package br.edu.ucsal.pokesal.engine;

import br.edu.ucsal.pokesal.model.*;
import br.edu.ucsal.pokesal.util.ConstantesJogo;

public class GerenciadorDeBatalha {
    private PokeSal pokesal1;
    private PokeSal pokesal2;
    private PokeSal primeiroAtacante;
    private PokeSal segundoAtacante;
    private Terreno terreno;
    private int contadorTurno = 0;
    private PokeSal ultimoAtacante;
    private PokeSal vencedor;

    public GerenciadorDeBatalha(PokeSal pokesal1, PokeSal pokesal2, Terreno terreno) {
        this.pokesal1 = pokesal1;
        this.pokesal2 = pokesal2;
        this.terreno = terreno;
        this.vencedor = null;
    }

    public void definirOrdemAtuacao() {
        if (pokesal1.getSpd() >= pokesal2.getSpd()) {
            this.primeiroAtacante = pokesal1;
            this.segundoAtacante = pokesal2;
        } else {
            this.primeiroAtacante = pokesal2;
            this.segundoAtacante = pokesal1;
        }

    }

    private void aplicarDanoRecuo() {
        if (this.contadorTurno > 0 && this.contadorTurno % ConstantesJogo.INTERVALO_TURNO_RECUO == 0) {
            if (this.ultimoAtacante != null && this.ultimoAtacante.isVivo()) {
                int danoRecuo = (int) Math
                        .round(this.ultimoAtacante.getHpMaximo() * ConstantesJogo.PERCENTUAL_DANO_RECUO);

                this.ultimoAtacante.receberDano(danoRecuo);
            }
        }
    }

    public void iniciarBatalha() {
        while (pokesal1.isVivo() && pokesal2.isVivo()) {
            definirOrdemAtuacao();

            int danoPrimeiro = CalculadoraDano.calcularDano(this.primeiroAtacante, this.segundoAtacante, this.terreno);

            this.segundoAtacante.receberDano(danoPrimeiro);

            this.ultimoAtacante = this.primeiroAtacante;

            if (!this.segundoAtacante.isVivo()) {
                break;
            }

            int danoSegundo = CalculadoraDano.calcularDano(this.primeiroAtacante, this.segundoAtacante, this.terreno);

            this.primeiroAtacante.receberDano(danoSegundo);

            this.ultimoAtacante = this.segundoAtacante;

            if (!this.primeiroAtacante.isVivo()) {
                break;
            }

            this.contadorTurno++;

            aplicarDanoRecuo();

            if (!this.ultimoAtacante.isVivo())
                break;

        }

        if (this.pokesal1.isVivo()) {
            this.vencedor = this.pokesal1;
        } else {
            this.vencedor = this.pokesal2;
        }
    }

    public PokeSal getVencedor() {
        return this.vencedor;
    }
}
