/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattcpip.classes;

import static chattcpip.classes.ServidoresSockets.outEuEOutrosClientes;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import chattcpip.enums.ComandoEnum;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Evandro Santos
 */
public class ThreadMensagens extends Thread {
    private final Socket socketEuClient;
    private final InformacoesCliente informacoesEuCliente;
    private final ServidoresSockets servidorSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    ThreadMensagens(Socket client, InformacoesCliente cliente, ServidoresSockets servidorSocket, ObjectInputStream in, ObjectOutputStream out) {
        this.socketEuClient = client;
        this.informacoesEuCliente = cliente;
        this.servidorSocket = servidorSocket;
        this.in = in;
        this.out = out;
    }
    
    public void run() {
        try {
            Comando comandoObj;
            ComandoMensagem comandoMensagem;
            while ((comandoObj = (Comando) in.readObject()) != null) {
                InformacoesServidor informacoesServidor = InformacoesServidor.getInstance();
                Iterator<ObjectOutputStream> iterador = outEuEOutrosClientes.iterator();
                if (comandoObj.getComando() != null) {
                    switch (comandoObj.getComando()) {
                        case SAIR:
                            while (iterador.hasNext()) {
                                ObjectOutputStream outEuEOutroClient = iterador.next();
                                outEuEOutroClient.writeObject(new Comando(ComandoEnum.REMOVER, informacoesEuCliente));
                            }   
                            informacoesServidor.removerDeTodasAsMensagensOndeDeOuParaSeja(informacoesEuCliente.getId());
                            this.servidorSocket.desconectar(this.out, this.informacoesEuCliente);
                            this.socketEuClient.close();
                            break;
                        case RECARREGAR:
                            if (comandoObj instanceof ComandoMensagem) {
                                comandoMensagem = ((ComandoMensagem) comandoObj);
                                List<ComandoMensagem> mensagens;
                                if (comandoMensagem.getMensagemPara() == 0) {
                                    mensagens = informacoesServidor.getTodasAsMensagensPara(0);
                                } else {
                                    mensagens = informacoesServidor.getTodasAsMensagensDeParaOuParaDe(comandoMensagem.getMensagemDe(), comandoMensagem.getMensagemPara());
                                }
                                for (int i = 0, l = mensagens.size(); i < l; i++) {
                                    out.writeObject(mensagens.get(i));
                                    out.flush();
                                }
                            }
                            break;
                        case MENSAGEM:
                        case MENSAGEMALERTA:
                            if (comandoObj instanceof ComandoMensagem) {
                                comandoMensagem = ((ComandoMensagem) comandoObj);
                                boolean comAlerta = comandoObj.getComando() == ComandoEnum.MENSAGEMALERTA;
                                comandoMensagem.setMensagem("(" + informacoesEuCliente.getId() + ") " + informacoesEuCliente.getNome() + " diz: " + comandoMensagem.getMensagem());
                                System.out.println("\r\n" + comandoMensagem.getMensagem());
                                if (!comAlerta)
                                    informacoesServidor.addParaTodasAsMensagens(comandoMensagem);
                                if (comandoMensagem.getMensagemPara() == 0) {
                                    while (iterador.hasNext()) {
                                        ObjectOutputStream outEuEOutroCliente = iterador.next();
                                        outEuEOutroCliente.writeObject(comandoMensagem);
                                        outEuEOutroCliente.flush();
                                    }
                                } else {
                                    InformacoesCliente outroCliente = informacoesServidor.getClienteById(comandoMensagem.getMensagemPara());
                                    if (outroCliente != null) {
                                        out.writeObject(comandoMensagem);
                                        out.flush();

                                        int index = informacoesServidor.getIndexCliente(outroCliente);
                                        if (index > -1 && index < outEuEOutrosClientes.size()) {
                                            ObjectOutputStream outOutroCliente = outEuEOutrosClientes.get(index);
                                            outOutroCliente.writeObject(comandoMensagem);
                                            outOutroCliente.flush();
                                        }
                                    }
                                }
                            }
                            break;
                        case FILEUPLOAD:
                            if (comandoObj instanceof FileUpload) {
                                FileUpload fileUpload = (FileUpload) comandoObj;
                                String nomeArquivoNoServidor = fileUpload.getCurrentTimeMillis() + "_" + fileUpload.getNome();
                                Pattern extrairApenasONomeDoArquivo = Pattern.compile("_.{1,}$");
                                Matcher matcher = extrairApenasONomeDoArquivo.matcher(nomeArquivoNoServidor);
                                String nomeArquivo = "";
                                if (matcher.find())
                                    nomeArquivo = matcher.group();
                                nomeArquivo = nomeArquivo.substring(1);
                                String caminhoArquivoNoServidor = informacoesServidor.getDiretorioArquivos() + nomeArquivoNoServidor;
                                String mensagem = "(" + informacoesEuCliente.getId() + ") " + informacoesEuCliente.getNome() + " enviou o arquivo " + nomeArquivo + ", para baixar insira o caminho " + caminhoArquivoNoServidor;
                                ComandoMensagem mensagemObj = new ComandoMensagem(ComandoEnum.MENSAGEM, informacoesEuCliente, informacoesEuCliente.getId(), fileUpload.getArquivoPara(), mensagem);
                                informacoesServidor.addParaTodasAsMensagens(mensagemObj);
                                if (fileUpload.getArquivoPara() == 0) {
                                    while (iterador.hasNext()) {
                                        ObjectOutputStream outEuOuOutroCliente = iterador.next();
                                        outEuOuOutroCliente.writeObject(mensagemObj);
                                        outEuOuOutroCliente.flush();
                                    }
                                } else {
                                    InformacoesCliente outroCliente = informacoesServidor.getClienteById(fileUpload.getArquivoPara());
                                    if (outroCliente != null) {
                                        out.writeObject(mensagemObj);
                                        out.flush();

                                        int index = informacoesServidor.getIndexCliente(outroCliente);
                                        if (index > -1 && index < outEuEOutrosClientes.size()) {
                                            ObjectOutputStream outOutroCliente = outEuEOutrosClientes.get(index);
                                            outOutroCliente.writeObject(mensagemObj);
                                            outOutroCliente.flush();
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException error) {
            System.out.println(error.getMessage());
        }
    }
}
