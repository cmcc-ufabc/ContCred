package dao;

import java.sql.*;

public class Conexao {
	public Connection getConexao() {
        //System.out.println("Conectando ao Banco de Dados...");
        try {
            String url = "jdbc:mysql://localhost/contagemcred";
            Class.forName("com.mysql.jdbc.Driver"); 
            return DriverManager.getConnection(url, "root", "");
            //login e senha para o servidor: usu�rio (scc) senha(contagemcred)
            //url para o banco de dados: jdbc:mysql://localhost:3306/contagemcred
            //Usu�rio da m�quina que est� sendo usada como servidor e senha: usu�rio(root) senha(root)
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
