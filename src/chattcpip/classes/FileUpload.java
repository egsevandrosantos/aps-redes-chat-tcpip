/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattcpip.classes;

import chattcpip.enums.ComandoEnum;
import java.io.Serializable;

/**
 *
 * @author Evandro Santos
 */
public class FileUpload extends FileDownload implements Serializable {
    private final String nome;
    private final long currentTimeMillis;
    private int arquivoPara;
    
    public FileUpload(ComandoEnum comando, InformacoesCliente informacoesCliente, byte[] fileBytes, String nome, int arquivoPara) {
        super(comando, informacoesCliente, fileBytes);
        this.nome = nome;
        this.currentTimeMillis = System.currentTimeMillis();
        this.arquivoPara = arquivoPara;
    }
    
    public String getNome() {
        return nome;
    }
    
    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }
    
    public int getArquivoPara() {
        return arquivoPara;
    }
}
