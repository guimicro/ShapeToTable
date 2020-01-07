
package shapetotable.domain;

/**
 *
 * @author Guilherme
 */
public class CadastroBanco {
    
    private int idBanco;
    private String nomeConexao;
    private String usuario;
    private String senha;
    private String TNS; 
    private String host;
    private String sid;
    private int porta;

    public void CadastroBanco(){
        
    }
    
    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getNomeConexao() {
        return nomeConexao;
    }

    public void setNomeConexao(String nomeConexao) {
        this.nomeConexao = nomeConexao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTNS() {
        return TNS;
    }

    public void setTNS(String TNS) {
        this.TNS = TNS;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }
    
}
