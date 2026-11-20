package modelo;

public abstract class DispositivoMonitoramento {

    private final String codigoIdentificador;
    private final String localizacao;
    private boolean emOperacao;

    // o dispositivo nasce em operacao
    // usar colocarForaDeOperacao() para desativar
    protected DispositivoMonitoramento(String codigoIdentificador, String localizacao) {
        if (codigoIdentificador == null || codigoIdentificador.isBlank()) {
            throw new IllegalArgumentException("O código do dispositivo é obrigatório.");
        }
        if (localizacao == null || localizacao.isBlank()) {
            throw new IllegalArgumentException("A localização do dispositivo é obrigatória.");
        }
        this.codigoIdentificador = codigoIdentificador;
        this.localizacao = localizacao;
        this.emOperacao = true;
    }

    public String getCodigoIdentificador() {
        return codigoIdentificador;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public boolean isEmOperacao() {
        return emOperacao;
    }

    public void colocarEmOperacao() {
        this.emOperacao = true;
    }

    public void colocarForaDeOperacao() {
        this.emOperacao = false;
    }
}
