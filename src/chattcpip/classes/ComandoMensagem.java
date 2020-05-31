/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattcpip.classes;

import chattcpip.enums.ComandoEnum;

/**
 *
 * @author Evandro Santos
 */
public class ComandoMensagem extends Comando {
    private int mensagemDe;
    private int mensagemPara;
    private String mensagem;
    
    public ComandoMensagem(ComandoEnum comando, InformacoesCliente informacoesCliente, int mensagemDe, int mensagemPara, String mensagem) {
        super(comando, informacoesCliente);
        this.mensagemDe = mensagemDe;
        this.mensagemPara = mensagemPara;
        this.mensagem = mensagem;
    }
    
    public int getMensagemDe() {
        return mensagemDe;
    }
    
    public int getMensagemPara() {
        return mensagemPara;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
