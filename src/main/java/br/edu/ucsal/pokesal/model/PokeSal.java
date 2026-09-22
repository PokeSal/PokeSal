package br.edu.ucsal.pokesal.model;

import br.edu.ucsal.pokesal.util.ConstantesJogo;

/**
 * Representa a entidade principal do Pokésal, armazenando seus atributos base, vida e mochila.
 */
public class PokeSal {

  private String nome;
  private int hpMaximo;
  private int hpAtual;
  private int atk;
  private int def;
  private int spd;
  private TipoElemental tipo;
  private boolean passivaDefesaAtivada;
  private Mochila mochila;
  private EfeitoStatus statusAtual = EfeitoStatus.NENHUM;
  private int contadorAtaquesConsecutivos = 0;

  /**
   * Construtor que instancia um Pokésal com base no modelo do TipoPokesal fornecido.
   *
   * @param tipo O modelo do Pokésal escolhido.
   */
  public PokeSal(TipoPokesal tipo) {
    this.nome = tipo.getNomeFormatado();
    this.hpMaximo = tipo.getHpBase();
    this.hpAtual = hpMaximo;
    this.atk = tipo.getAtkBase();
    this.def = tipo.getDefBase();
    this.spd = tipo.getSpdBase();
    this.tipo = tipo.getTipo();
    this.passivaDefesaAtivada = false;
    this.mochila = new Mochila();
  }

  /**
   * Verifica se o Pokésal possui vida maior que zero.
   *
   * @return Verdadeiro se o Pokésal estiver vivo, falso caso esteja desmaiado.
   */
  public boolean isVivo() {
    return this.hpAtual > 0;
  }

  /**
   * Reduz o HP do Pokésal e ativa a passiva defensiva de baixo HP caso a condição seja atingida.
   *
   * @param quantidadeDano Valor numérico do dano a ser aplicado.
   */
  public void receberDano(int quantidadeDano) {
    this.hpAtual -= quantidadeDano;

    if (this.hpAtual < 0) {
      this.hpAtual = 0;
    }

    if (this.hpAtual > 0 && !passivaDefesaAtivada
        && this.hpAtual <= (hpMaximo * ConstantesJogo.LIMITAR_PASSIVA_DEFESA)) {
      this.def = (int) Math.round(this.def * ConstantesJogo.MULTIPLICADOR_PASSIVA_DEFESA);
      this.passivaDefesaAtivada = true;
      System.out.println("\n[PASSIVA] Passiva de defesa ativada!");
    }
  }

  /**
   * Restaura os pontos de vida (HP) do Pokésal respeitando o limite máximo.
   *
   * @param quantidadeCura Quantidade de pontos de vida a ser recuperada.
   */
  public void curar(int quantidadeCura) {
    this.hpAtual += quantidadeCura;
    if (this.hpAtual > this.hpMaximo) {
      this.hpAtual = this.hpMaximo;
    }
  }

  /**
   * Valida e consome um item específico da mochila aplicando os efeitos correspondentes.
   *
   * @param indice Índice do item escolhido na lista da mochila.
   * @return Verdadeiro se o item foi consumido com sucesso, falso caso contrário.
   */
  public boolean usarItemEspecifico(int indice) {
    if (indice < 0 || indice >= this.mochila.getItens().size()) {
      return false;
    }

    Item itemConsultado = this.mochila.getItens().get(indice);

    boolean precisaCuraHp = (itemConsultado.getPontosCura() > 0 && this.hpAtual < this.hpMaximo);
    boolean precisaCuraStatus = (itemConsultado.getCuraStatus().equalsIgnoreCase(ConstantesJogo.STATUS_TODOS)
        && this.statusAtual != EfeitoStatus.NENHUM);

    if (!precisaCuraHp && !precisaCuraStatus) {
      return false;
    }

    Item itemUsado = this.mochila.usarItemPorIndice(indice);
    if (itemUsado != null) {
      if (itemUsado.getPontosCura() > 0) {
        curar(itemUsado.getPontosCura());
      }
      if (itemUsado.getCuraStatus().equalsIgnoreCase(ConstantesJogo.STATUS_TODOS)) {
        this.statusAtual = EfeitoStatus.NENHUM;
      }

      zerarAtaquesConsecutivos();
      return true;
    }

    return false;
  }

  /**
   * Incrementa o contador de ataques consecutivos do Pokésal para controle de dano de recuo.
   *
   * @return O número atualizado de ataques seguidos.
   */
  public int incrementarAtaquesConsecutivos() {
    this.contadorAtaquesConsecutivos++;
    return this.contadorAtaquesConsecutivos;
  }

  /**
   * Reinicia a contagem de ataques consecutivos do Pokésal.
   */
  public void zerarAtaquesConsecutivos() {
    this.contadorAtaquesConsecutivos = 0;
  }

  /**
   * Obtém a contagem atual de ataques consecutivos executados sem pausa.
   *
   * @return O número de ataques consecutivos.
   */
  public int getContadorAtaquesConsecutivos() {
    return contadorAtaquesConsecutivos;
  }

  /**
   * Obtém a mochila de itens do Pokésal.
   *
   * @return O objeto Mochila.
   */
  public Mochila getMochila() {
    return mochila;
  }

  /**
   * Obtém o nome do Pokésal.
   *
   * @return O nome do Pokésal.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Obtém os pontos de vida máximos (HP Máximo) do Pokésal.
   *
   * @return O HP máximo.
   */
  public int getHpMaximo() {
    return hpMaximo;
  }

  /**
   * Obtém os pontos de vida atuais (HP Atual) do Pokésal.
   *
   * @return O HP atual.
   */
  public int getHpAtual() {
    return hpAtual;
  }

  /**
   * Obtém o atributo de ataque (ATK) do Pokésal.
   *
   * @return O valor do ataque.
   */
  public int getAtk() {
    return atk;
  }

  /**
   * Obtém o atributo de defesa (DEF) do Pokésal.
   *
   * @return O valor da defesa.
   */
  public int getDef() {
    return def;
  }

  /**
   * Obtém o atributo de velocidade (SPD) do Pokésal.
   *
   * @return O valor da velocidade.
   */
  public int getSpd() {
    return spd;
  }

  /**
   * Obtém o tipo elemental do Pokésal.
   *
   * @return O enum TipoElemental associado.
   */
  public TipoElemental getTipo() {
    return tipo;
  }

  /**
   * Obtém o efeito de status atualmente ativo no Pokésal.
   *
   * @return O enum EfeitoStatus atual.
   */
  public EfeitoStatus getStatusAtual() {
    return statusAtual;
  }

  /**
   * Define o efeito de status atual do Pokésal.
   *
   * @param statusAtual O novo efeito de status a ser atribuído.
   */
  public void setStatusAtual(EfeitoStatus statusAtual) {
    this.statusAtual = statusAtual;
  }
}