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
public class FileDownload extends Comando implements Serializable {
    private byte[] fileBytes;

    public FileDownload(ComandoEnum comando, InformacoesCliente informacoesCliente, byte[] fileBytes) {
        super(comando, informacoesCliente);
        this.fileBytes = fileBytes;
    }
    
    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
    
    public byte[] getFileBytes(){
        return fileBytes;
    }
}
