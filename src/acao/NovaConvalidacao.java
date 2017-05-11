package acao;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ConvalidacaoDAO;
import dao.ProjetoDAO;
import model.Convalidacao;

public class NovaConvalidacao implements Acao {
	public void executa(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String cod_disciplina = req.getParameter("cod_disciplina");
		String cod_convalidacao = req.getParameter("cod_convalidacao");
		String curso = req.getParameter("curso");
		int matriz = Integer.parseInt(req.getParameter("matriz"));
		//System.out.println("d: "+cod_disciplina);
		//System.out.println("c: "+cod_convalidacao);
		//System.out.println("sigla: "+curso);
		//System.out.println("matriz: "+matriz);
		
		curso = determinaCurso(curso);
		//System.out.println("curso: "+curso);
				
		Convalidacao c = new Convalidacao();
		c.setCod_disciplina(cod_disciplina);
		c.setCod_convalidacao(cod_convalidacao);
		
		ProjetoDAO p_dao = new ProjetoDAO();
		int cod_ppc = p_dao.buscaProjeto(matriz, curso);
		c.setCod_ppc(cod_ppc);
		
		ConvalidacaoDAO dao = new ConvalidacaoDAO();
		Convalidacao verifica = dao.buscaConvalidacao(cod_disciplina, cod_ppc);
		if(verifica.getCod_disciplina() != null){
			req.setAttribute("msg", "Convalida��o j� Existente!");
			RequestDispatcher rd = req.getRequestDispatcher("/info.jsp");
			rd.forward(req, resp);
		} else{
			dao.criaConvalidacao(c);
			
			req.setAttribute("msg", "Convalida��o criada com sucesso!");
			RequestDispatcher rd = req.getRequestDispatcher("/info.jsp");
			rd.forward(req, resp);
		}
	}
	
	//M�todo para verificar qual curso ser� usado para a contagem de cr�ditos
	public String determinaCurso(String sigla){
		String curso = "";
		switch (sigla){
		case "BCC":
			curso = "Bacharelado em Ci�ncia da Computa��o";
			break;
		case "BCT":
			curso = "Bacharelado em Ci�ncia e Tecnologia";
			break;
		case "BMAT":
			curso = "Bacharelado em Matem�tica";
			break;
		case "LMAT":
			curso = "Licenciatura em Matem�tica";
			break;
		case "BNC":
			curso = "Bacharelado em Neuroci�ncia";
			break;
		}
		return curso;
	}
}
