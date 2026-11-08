package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;

/**
 * Entidade principal que representa o Pokésal no combate.
 */
public class PokeSal {

  private final String nome;
  private final TipoElemental tipo;
  private final int hpMaximo;
  private int hpAtual;
  private final int atk;
  private final int def;
  private final int spd;
  private final int precisao;
  private EfeitoStatus statusAtual;
  private final Mochila mochila;
  private int ataquesConsecutivos;

  public PokeSal(TipoPokesal tipoPokesal) {
    this.nome = tipoPokesal.getNomeFormatado();
    this.tipo = tipoPokesal.getTipo();
    this.hpMaximo = tipoPokesal.getHpBase();
    this.hpAtual = tipoPokesal.getHpBase();
    this.atk = tipoPokesal.getAtkBase();
    this.def = tipoPokesal.getDefBase();
    this.spd = tipoPokesal.getSpdBase();
    this.precisao = tipoPokesal.getPrecisaoBase();
    this.statusAtual = EfeitoStatus.NENHUM;
    this.mochila = new Mochila();
    this.ataquesConsecutivos = 0;
  }

  public boolean isPassivaDefensivaAtiva() {
    return this.hpAtual > 0
        && (double) this.hpAtual / this.hpMaximo <= ConstantesJogo.LIMITAR_PASSIVA_DEFESA;
  }

  public int getDef() {
    if (isPassivaDefensivaAtiva()) {
      return (int) Math.round(this.def * ConstantesJogo.MULTIPLICADOR_PASSIVA_DEFESA);
    }
    return this.def;
  }

  public void receberDano(int dano) {
    this.hpAtual = Math.max(0, this.hpAtual - dano);
  }

  public void curar(int quantidade) {
    this.hpAtual = Math.min(this.hpMaximo, this.hpAtual + quantidade);
  }

  public boolean usarItemEspecifico(int indice) {
    if (!this.mochila.podeUsarItem()) {
      return false;
    }

    Item item = this.mochila.getItens().get(indice);
    boolean itemUtilizado = false;

    if (ConstantesJogo.STATUS_TODOS.equalsIgnoreCase(item.getCuraStatus())) {
      if (this.statusAtual != EfeitoStatus.NENHUM) {
        this.statusAtual = EfeitoStatus.NENHUM;
        itemUtilizado = true;
      }
    } else if (item.getPontosCura() > 0) {
      if (this.hpAtual < this.hpMaximo) {
        curar(item.getPontosCura());
        itemUtilizado = true;
      }
    }

    if (itemUtilizado) {
      this.mochila.usarItemPorIndice(indice);
    }

    return itemUtilizado;
  }

  public boolean receberRecompensa(Item item) {
    return this.mochila.adicionarItem(item);
  }

  public int incrementarAtaquesConsecutivos() {
    this.ataquesConsecutivos++;
    return this.ataquesConsecutivos;
  }

  public boolean isVivo() {
    return this.hpAtual > 0;
  }

  public String getNome() { return nome; }
  public TipoElemental getTipo() { return tipo; }
  public int getHpMaximo() { return hpMaximo; }
  public int getHpAtual() { return hpAtual; }
  public int getAtk() { return atk; }
  public int getSpd() { return spd; }
  public int getPrecisao() { return precisao; }
  public EfeitoStatus getStatusAtual() { return statusAtual; }
  public void setStatusAtual(EfeitoStatus statusAtual) { this.statusAtual = statusAtual; }
  public Mochila getMochila() { return mochila; }
  public int getAtaquesConsecutivos() { return ataquesConsecutivos; }
}