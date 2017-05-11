package controller;

import java.awt.Desktop;

//import org.apache.tomcat.util.http.fileupload.*;
//import org.apache.tomcat.util.http.fileupload.disk.*;
//import org.apache.tomcat.util.http.fileupload.servlet.*;

//import java.util.Iterator;
//import java.util.List;
import java.io.*;
import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileItemFactory;
//import org.apache.commons.fileupload.FileUploadException;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import dao.ConvalidacaoDAO;
import dao.DisciplinaDAO;
import dao.ProjetoDAO;
import model.Convalidacao;
import model.Disciplina;
import model.Grade;
import model.PPC;

@WebServlet("/upload")
@MultipartConfig
public class FileController extends HttpServlet {
	
	private String nome = "";
	private int ra = 0;
	private String sigla = "";
	private int matriz = 0;
	private String curso = "";
	private int cursado_bct = 0;
	private int cred_obrigatorio_bct = 0;
	private float p_bct = 0.0f;
	private int cursado_obrigatorio_curso = 0;
	private int cred_obrigatorio_curso = 0;
	private float p_curso = 0.0f;
	private int cursado_limitado = 0;
	private int cred_limitado_curso = 0;
	private float p_limitado = 0.0f;
	private int cursado_livre = 0;
	private int cred_curso_livre = 0;
	private float p_livre = 0.0f;
	private int cred_total_curso = 0;
	private int cursado_total = 0;
	private float p_total = 0.0f;
	private List<Disciplina> obrigatoria_bct;
	private List<Disciplina> obrigatoria_curso;
	private List<Disciplina> limitadas;
	private List<Disciplina> livres;
	private List<Disciplina> obrigatorias;
	private List<Grade> grade_ppc;
	private List<Grade> grade_bct;
	private List<Disciplina> cursadas;
	private List<Disciplina> nao_encontradas;
	private List<Disciplina> nao_catalogada;
	private List<String> pendentes;
	private List<Convalidacao> convalidacoes;
	private String path;
	private String fileName;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		nome = req.getParameter("nome");
		ra = Integer.parseInt(req.getParameter("ra"));
		sigla = req.getParameter("curso");
		matriz = Integer.parseInt(req.getParameter("matriz"));
		//Determina qual curso ser� feita a contagem
		curso = determinaCurso(sigla);
		String opcao = req.getParameter("opcao");
		System.out.println("opcao: "+opcao);
		//Buscar a lista de mat�rias da matriz do curso
		ProjetoDAO p_dao = new ProjetoDAO();
		int p_id = p_dao.buscaProjeto(matriz, curso);
		System.out.println("cod: "+p_id);
		grade_ppc = p_dao.buscaGrade(p_id);
		System.out.println("grade: "+grade_ppc.size());
		int i;
		cred_obrigatorio_curso = 0;
		int cred_apenas_curso = 0;
		cred_limitado_curso = 0;
		cred_curso_livre = 0;
		PPC projeto = p_dao.retornaProjeto(p_id);
		cred_obrigatorio_curso = projeto.getCred_obrigatorias();
		cred_limitado_curso = projeto.getCred_limitadas();
		cred_curso_livre = projeto.getCred_livres();
		cred_apenas_curso = cred_obrigatorio_curso + cred_limitado_curso + cred_curso_livre;
		
		//M�todo para determinar qual matriz do BC&T usar
		List<PPC> bct = p_dao.listaProjetos();
		int ultimo_projeto = 0;
		int cod_ppc = 0;
		cred_obrigatorio_bct = 0;
		for(i=0;i<bct.size();i++){
			if(ultimo_projeto < bct.get(i).getMatriz() && matriz >= bct.get(i).getMatriz()){
				ultimo_projeto = bct.get(i).getMatriz();
				cod_ppc = bct.get(i).getCod_ppc();
				cred_obrigatorio_bct = bct.get(i).getCred_obrigatorias();
			}
		}
		grade_bct = p_dao.buscaGrade(cod_ppc);
		System.out.println("bct "+ultimo_projeto+": "+grade_bct.size()+" total: "+cred_obrigatorio_bct);
		
		gravarArquivo(req);
		
		//Gravar o arquivo do hist�rico do aluno
		/*String path = "C:\\Users\\Erick\\Documents\\Eclipse Projects\\ContCred\\files\\";
		Part filePart = req.getPart("file");
	    String fileName = getFileName(filePart);
	    System.out.println(fileName);
	    OutputStream out = null;
	    InputStream filecontent = null;
	    //PrintWriter writer = resp.getWriter();

	    try {
	        out = new FileOutputStream(new File(path + fileName));
	        filecontent = filePart.getInputStream();
	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        //writer.println("New file " + fileName + " created at " + path);
	        //LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", new Object[]{fileName, path});
	    } catch (FileNotFoundException fne) {
	        //writer.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");//
	        //writer.println("<br/> ERROR: " + fne.getMessage());
	        //LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{fne.getMessage()});
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	        //if (writer != null) {
	            writer.close();
	        }//
	        System.out.print("Finalizado");
	    }*/
	    
	    cursadas = lerHistorico(path,fileName);
	    System.out.println("pendentes: "+pendentes.size());
	    nao_encontradas = new ArrayList<>();
		obrigatorias = new ArrayList<>();
		limitadas = new ArrayList<>();
		obrigatoria_bct = new ArrayList<>();
		//int cred_obrigatorio = 0;
		cursado_bct = 0;
		cursado_obrigatorio_curso = 0;
		cursado_limitado= 0;
		boolean encontrada;
		boolean bd = false;
		boolean es = false;
		boolean pe = false;
		for(Disciplina cursada : cursadas){
			verificarPPC(cursada);
			if(cursada.getNome().equals("BANCO DE DADOS") && cursada.getP() == 0){
				bd = true;
			} else if(cursada.getNome().equals("Banco de Dados") && cursada.getP() == 0){
				bd = true;
			} else if(cursada.getNome().equals("ENGENHARIA DE SOFTWARE") && cursada.getP() == 0){
				es = true;
			} else if(cursada.getNome().equals("Engenharia de Software") && cursada.getP() == 0){
				es = true;
			} else if(cursada.getNome().equals("Programa��o Estruturada")){
				pe = true;
			}
			/*encontrada = false;
			//System.out.println("cod para verificar: "+cursada.getCod_disciplina());
			for(i=0;i<grade_ppc.size();i++){
				//System.out.println(grade_ppc.get(i).getCod_disciplina());
				String cod = grade_ppc.get(i).getCod_disciplina();
				if(cod.equals(cursada.getCod_disciplina())){
					encontrada = true;
					//creditos += cursada.getT() + cursada.getP();
					
					//verifica se � obrigat�ria ou limitada
					String status = grade_ppc.get(i).getStatus();
					if(status.equals("Obrigat�ria")){
						//cred_obrigatorio += cursada.getT() + cursada.getP();
						cursado_obrigatorio_curso += cursada.getT() + cursada.getP();
						obrigatorias.add(cursada);
					} else{
						cursado_limitado+= cursada.getT() + cursada.getP();
						limitadas.add(cursada);
					}
				}
			}
			for(i=0;i<grade_bct.size();i++){
				String cod = grade_bct.get(i).getCod_disciplina();
				if(cod.equals(cursada.getCod_disciplina())){
					encontrada= true;
					
					//verifica se � obrigat�ria
					String status = grade_bct.get(i).getStatus();
					if(status.equals("Obrigat�ria")){
						//cred_obrigatorio += cursada.getT() + cursada.getP();
						cursado_bct += cursada.getT() + cursada.getP();
						obrigatoria_bct.add(cursada);
					}
				}
			}
			//Disciplina n�o encontrada
			if(encontrada == false){
				nao_encontradas.add(cursada);
			}*/
		}
		System.out.println("N�o Encontradas: "+nao_encontradas.size());
		
		//Verificar as convalida��es nas disciplinas que n�o foram encontradas 
		ConvalidacaoDAO c_dao = new ConvalidacaoDAO();
		DisciplinaDAO d_dao = new DisciplinaDAO();
		convalidacoes = new ArrayList<>();
		livres = new ArrayList<>();
		if(!nao_encontradas.isEmpty()){
			//System.out.println("Convalida��es");
			buscaConvalidacoes(p_id, cod_ppc);
			/*for(Disciplina nao_encontrada : nao_encontradas){
				//System.out.println(nao_encontrada.getCod_disciplina()+" "+p_id);
				Convalidacao c = c_dao.buscaConvalidacao(nao_encontrada.getCod_disciplina(), p_id);
				if(c.getCod_convalidacao() != null){
					//System.out.println("c: "+c.getCod_convalidacao());
					convalidacoes.add(c);
				} else{
					//Conta como livre
					//System.out.println("n: "+nao_encontrada.getCod_disciplina()+" "+nao_encontrada.getNome());
					livres.add(nao_encontrada);
				}
			}*/
			System.out.println("Convalidadas: "+convalidacoes.size());
			boolean convalidado;
			for(Convalidacao convalidacao: convalidacoes){
				verificaConvalidacao(convalidacao);
				/*convalidado = false;
				//Verifica na grade do ppc
				for(i=0;i<grade_ppc.size();i++){
					String cod = grade_ppc.get(i).getCod_disciplina();
					if(cod.equals(convalidacao.getCod_convalidacao())){
						convalidado = true;
						//Disciplina d = d_dao.buscaDisciplina(convalidacao.getCod_convalidacao());
						//creditos += d.getT() + d.getP();
								
						//System.out.println("convalidada: "+d.getNome()+" "+d.getT()+" "+d.getP());
						//verifica se � obrigat�ria ou limitada
						String status = grade_ppc.get(i).getStatus();
						if(status.equals("Obrigat�ria")){
							//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
							Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
							//cred_obrigatorio += convalidada.getT() + convalidada.getP();//d.getT() + d.getP();
							cursado_obrigatorio_curso += convalidada.getT() + convalidada.getP();
							obrigatorias.add(convalidada);
							System.out.println("origem: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
						} else{
							//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
							Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
							cursado_limitado+= convalidada.getT() + convalidada.getP();//d.getT() + d.getP();
							limitadas.add(convalidada);
							System.out.println("origem: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
						}
					}
				}
				//Verifica na grade do bct
				for(i=0;i<grade_bct.size();i++){
					String cod = grade_bct.get(i).getCod_disciplina();
					if(cod.equals(convalidacao.getCod_convalidacao())){
						convalidado = true;
						//Disciplina d = d_dao.buscaDisciplina(convalidacao.getCod_convalidacao());
								
						//System.out.println("convalidada: "+d.getNome()+" "+d.getT()+" "+d.getP());
						//verifica se � obrigat�ria
						String status = grade_ppc.get(i).getStatus();
						if(status.equals("Obrigat�ria")){
							//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
							Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
							//cred_obrigatorio += convalidada.getT() + convalidada.getP();//d.getT() + d.getP();
							cursado_bct += convalidada.getT() + convalidada.getP();
							obrigatorias.add(convalidada);
							System.out.println("origem: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
						}
					}
				}
				//Convalida��o n�o encontrada nos projetos pedag�gicos
				if(convalidado == false){
					//Conta os cr�ditos como livres
					//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
					Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
					//Disciplina d = d_dao.buscaDisciplina(convalidacao.getCod_convalidacao());
					livres.add(convalidada);
					System.out.println("origem: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
				}*/
			}
		}
		
		//Contar o restante como livre
		System.out.println("Livres: "+livres.size());
		cursado_livre = 0;
		for(Disciplina livre : livres){
			//System.out.println("l: "+livre.getCod_disciplina());
			cursado_livre += livre.getT() + livre.getP();
		}
				
		//Exibir os resultados da contagem
		//Faz a soma de todos os cr�ditos contados e as porcentagens de cada tipo de cr�dito
		System.out.println("Obrigat�rios: "+cursado_obrigatorio_curso + " + " + cursado_bct);
		System.out.println("Limitados: "+cursado_limitado);
		System.out.println("Livres: "+cursado_livre);
		System.out.println("N�o Catalogadas: "+nao_catalogada.size());
		for(Disciplina catalogada : nao_catalogada){
			System.out.println("nc: "+catalogada.getCod_disciplina()+" "+catalogada.getNome());
		}
		
		req.setAttribute("msg", "<h1>Relat�rio da Contagem de Cr�ditos</h1><br>");

		//C�lculo das porcentagens de cr�ditos do curso
		cursado_total = 0;
		int excedente = 0;
		int reduzidos = 0;
		
		//Verifica se alguma disciplina foi cursada com redu��o de cr�ditos
		if(matriz < 2015){
			if(bd){
				reduzidos += 2;
			}
			if(es){
				reduzidos += 2;
			}
			if(pe){
				reduzidos = 0;
			}
		}
		//Verifica as quantidades de obrigat�rias, limitadas e livres antes de fazer as porcentagens
		int compensados = 0;
		if(cursado_limitado > cred_limitado_curso){
			excedente = cursado_limitado - cred_limitado_curso;
			cursado_limitado = cred_limitado_curso;
			if(reduzidos > 0 && excedente >= reduzidos){
				excedente -= reduzidos;
				compensados = reduzidos;
			}
			cursado_livre += excedente;
			/*if(excedente + cursado_livre >= 12){
				cursado_livre = 12;
			} else{
				cursado_livre += excedente;
			}*/
		}
		if(compensados > 0){
			cursado_obrigatorio_curso += compensados;
		}
		if(cursado_obrigatorio_curso > cred_obrigatorio_curso){
			cursado_obrigatorio_curso = cred_obrigatorio_curso;
		}
		if(cursado_livre > cred_curso_livre){
			cursado_livre = cred_curso_livre;
			//cursado_total = cursado_obrigatorio_curso + cursado_bct + cursado_limitado+ cred_curso_livre;
		} else{
			//cursado_total = cursado_obrigatorio_curso + cursado_bct + cursado_limitado+ cursado_livre;
		}	
		
		//C�lculos das porcentagens do curso
		cursado_total = cursado_obrigatorio_curso + cursado_bct + cursado_limitado+ cursado_livre;
		cred_total_curso = cred_apenas_curso + cred_obrigatorio_bct;
		p_bct = (float) (100*cursado_bct)/cred_obrigatorio_bct;
		p_curso = (float) (100*cursado_obrigatorio_curso)/cred_obrigatorio_curso;
		p_total = (float) (100*cursado_total)/cred_total_curso;
		p_limitado = (float) (100*cursado_limitado)/cred_limitado_curso;
		p_livre = (float) (100*cursado_livre)/cred_curso_livre;
			
		//P�gina de resposta para exibir o relat�rio
		req.setAttribute("sigla", sigla);
		req.setAttribute("total_bct", cred_obrigatorio_bct);
		req.setAttribute("curso_obrigatorio", cred_obrigatorio_curso);
		req.setAttribute("total_curso", cred_total_curso);
		req.setAttribute("total_limitada", cred_limitado_curso);
		req.setAttribute("total_livre", cred_curso_livre);
		req.setAttribute("cursada_bct", cursado_bct);
		req.setAttribute("cursada_curso", cursado_obrigatorio_curso);
		req.setAttribute("cursada_total", cursado_total);
		req.setAttribute("cursada_limitada", cursado_limitado);
		req.setAttribute("cursada_livre", cursado_livre);
		req.setAttribute("p_bct", p_bct+"%");
		req.setAttribute("p_curso", p_curso+"%");
		req.setAttribute("p_total", p_total+"%");
		req.setAttribute("p_limitada", p_limitado+"%");
		req.setAttribute("p_livre", p_livre+"%");
		req.setAttribute("bct", obrigatoria_bct);
		req.setAttribute("curso", obrigatorias);
		req.setAttribute("limitadas", limitadas);
		req.setAttribute("livres", livres);
		req.setAttribute("nao_catalogadas", nao_catalogada);
		
		//N�o funciona
		/*resp.setContentType("aplication/pdf");
		PrintWriter o = new PrintWriter("Relatori27.pdf");
		o = resp.getWriter();
		String filepath = "C:\\Users\\Erick\\Documents\\eclipse\\Relatorio27.pdf";
		resp.setHeader("Content-Disposition", "inline; filename=" + filepath + ";");
		FileOutputStream fileout = new FileOutputStream("C:\\Users\\Erick\\Documents\\eclipse\\Relatorio27.pdf");
		fileout.close();
		o.close();*/
		//Funciona
		/*String serverHomeDir = System.getenv("CATALINA_HOME");
		String reportDestination = "C:\\Users\\Erick\\Documents\\eclipse\\Relatorio27.pdf";
		FileInputStream fis = new FileInputStream(new File(reportDestination));
		org.apache.commons.io.IOUtils.copy(fis, resp.getOutputStream());
		resp.setContentType("aplication/pdf");
		resp.setHeader("Content-Disposition", "attachment; filename=" + reportDestination);
		resp.flushBuffer();*/
		
		//M�todo para escolher a forma de exibi��o do relat�rio
		if(opcao.equals("abrir")){
			RequestDispatcher rd = req.getRequestDispatcher("/relatorio.jsp");
			rd.forward(req, resp);
		} else if(opcao.equals("baixar")){
			//Envio da resposta e montagem do pdf
			geraPDF();
			String name = nome+"_"+ra+".pdf";
			name = name.replaceAll(" ", "_");
			String serverHomeDir = System.getenv("CATALINA_HOME");
			String reportDestination = "C:\\Users\\Erick\\Documents\\eclipse\\"+name;
			//String reportDestination = name;//Caminho do servidor Tomcat
			//Diret�rio do Servidor para baixar o relat�rio: /home/erickaugusto/Arquivos/ 
			FileInputStream fis = new FileInputStream(new File(reportDestination));
			org.apache.commons.io.IOUtils.copy(fis, resp.getOutputStream());
			resp.setContentType("aplication/pdf");
			resp.setHeader("Content-Disposition", "attachment; filename=" + name);
			resp.flushBuffer();
		} else{
			//P�gina de erro caso algo n�o esteja certo
			RequestDispatcher rd = req.getRequestDispatcher("/error.jsp");
			rd.forward(req, resp);
		}
		//RequestDispatcher rd = req.getRequestDispatcher("/sucesso.jsp");
		//rd.forward(req, resp);
	}

	//M�todo para verificar qual curso ser� usado para a contagem de cr�ditos
	public String determinaCurso(String sigla){
		String curso = "";
		switch (sigla){
		case "BCC":
			curso = "Bacharelado em Ci�ncia da Computa��o";
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
	
	//Verifica o nome do arquivo (m�todo padr�o que existia na documenta��o para upload de arquivos)
	private String getFileName(final Part part) {
	    //final String partHeader = part.getHeader("content-disposition");
	    //LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
	
	//M�todo para ler as informa��es do hist�rico do aluno
	public List<Disciplina> lerHistorico(String path, String fileName) throws IOException{
		FileReader arq = null;
		List<Disciplina> cursadas = new ArrayList<>();//Preencher com os dados do arquivo do aluno
		nao_catalogada = new ArrayList<>();
		pendentes = new ArrayList<>();
		try {
			arq = new FileReader(path+""+fileName);
			BufferedReader buffer = new BufferedReader(arq);
			String linha = buffer.readLine();
			linha = buffer.readLine();
			
			//La�o para a leitura das linhas do arquivo
			String[] campos;
			DisciplinaDAO d_dao = new DisciplinaDAO();
			while(linha != null){
				if(!linha.contains("Quadrimestre")){
					linha = linha.replaceAll("\"", "");
					campos = linha.split("\t",-1);
					if(campos.length > 1){
						//System.out.println(campos[0]+" "+campos[1]+" "+campos[2]+" "+campos[3]+" "+campos[4]+" "+campos[5]);
						Disciplina cursada = d_dao.buscaDisciplina(campos[0]);
						
						//cursada.setCod_disciplina(campos[0]);
						//cursada.setNome(campos[1]);
						if(cursada.getCod_disciplina() != null && (campos[5].equals("Aprovado") || campos[5].equals("Apr.S.Nota") 
								|| campos[5].equals("Disc.Equiv") || campos[5].equals("Aproveitamento"))){
							//cursada.setNome(cursada.getNome().toUpperCase());
							//cursadas.add(cursada);
							//Lista para armazenar disciplina aprovadas com D e podem ser refeitas
							if(campos[4].equals("D") && !pendentes.contains(cursada.getNome())){
								pendentes.add(cursada.getNome());
								cursada.setNome(cursada.getNome().toUpperCase());
								cursadas.add(cursada);
							} else if(!pendentes.contains(cursada.getNome())){
								cursada.setNome(cursada.getNome().toUpperCase());
								cursadas.add(cursada);
							}
							/*if(pendentes.size() > 0 && !pendentes.contains(cursada.getNome())){
								cursada.setNome(cursada.getNome().toUpperCase());
								cursadas.add(cursada);
							}*/
						} else if(cursada.getCod_disciplina() == null && campos.length > 1){
							if(campos.length > 2 && (campos[5].equals("Aprovado") || campos[5].equals("Apr.S.Nota") 
									|| campos[5].equals("Disc.Equiv") || campos[5].equals("Aproveitamento"))){
								//System.out.println("ne: "+campos[0]+" "+campos[1]+" "+campos[2]+" "+campos[3]+" "+campos[4]+" "+campos[5]);
								Disciplina d = new Disciplina();
								d.setCod_disciplina(campos[0]);
								d.setNome(campos[1].toUpperCase());
								d.setT(Integer.parseInt(campos[3]));
								d.setP(0);
								d.setI(0);
								nao_catalogada.add(d);
							}
						}
					}
				}
				if(linha.contains("DISCIPLINAS CURSADAS EM MOBILIDADE")){
					linha = null;
					System.out.println("�ltima linha");
				} else{
					linha = buffer.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("cursadas: "+cursadas.size());
		return cursadas;
	}
	
	//M�todo para ler o arquivo informado pelo usu�rio
	public void gravarArquivo(HttpServletRequest req) throws IOException{
		//path = "C:\\Users\\Erick\\Documents\\Eclipse Projects\\ContCred\\files\\";//Caminho local 1
		//path = "webapps/upload_files/";//Esse caminho n�o funciona no servidor Tomcat
		path = "C:\\Users\\Erick\\Documents\\Files\\";//Caminho local 2
		//Caminho usado para salvar os arquivos na m�quina que est� sendo usada como servidor
		//path = "/home/charles/Documentos/Arquivos_ContCred/";
		//Diret�rio do Servidor para salvar o arquivo: /home/erickaugusto/ 
		Part filePart = null;
		try {
			filePart = req.getPart("file");
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    fileName = getFileName(filePart);
	    System.out.println(fileName);
	    OutputStream out = null;
	    InputStream filecontent = null;
	    //PrintWriter writer = resp.getWriter();

	    try {
	        out = new FileOutputStream(new File(path + fileName));
	        filecontent = filePart.getInputStream();
	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        //writer.println("New file " + fileName + " created at " + path);
	        //LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", new Object[]{fileName, path});
	    } catch (FileNotFoundException fne) {
	        /*writer.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");*/
	        //writer.println("<br/> ERROR: " + fne.getMessage());
	        //LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{fne.getMessage()});
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	        /*if (writer != null) {
	            writer.close();
	        }*/
	        System.out.print("Finalizado");
	    }
	}
	
	//M�todo para verificar se a disciplina pertence ao ppc
	public void verificarPPC(Disciplina cursada){
		int i;
		boolean encontrada = false;
		for(i=0;i<grade_ppc.size();i++){
			String cod = grade_ppc.get(i).getCod_disciplina();
			if(cod.equals(cursada.getCod_disciplina())){
				encontrada = true;
				//verifica se � obrigat�ria ou limitada
				String status = grade_ppc.get(i).getStatus();
				if(status.equals("Obrigat�ria")){
					//cred_obrigatorio += cursada.getT() + cursada.getP();
					cursado_obrigatorio_curso += cursada.getT() + cursada.getP();
					obrigatorias.add(cursada);
				} else{
					cursado_limitado+= cursada.getT() + cursada.getP();
					limitadas.add(cursada);
				}
			}
		}
		for(i=0;i<grade_bct.size();i++){
			String cod = grade_bct.get(i).getCod_disciplina();
			if(cod.equals(cursada.getCod_disciplina())){
				encontrada= true;
				//verifica se � obrigat�ria
				String status = grade_bct.get(i).getStatus();
				if(status.equals("Obrigat�ria")){
					//cred_obrigatorio += cursada.getT() + cursada.getP();
					cursado_bct += cursada.getT() + cursada.getP();
					obrigatoria_bct.add(cursada);
				}
			}
		}
		//Disciplina n�o encontrada
		if(encontrada == false){
			nao_encontradas.add(cursada);
		}
	}
	
	//M�todo para verificar se h� convalida��es no hist�rico
	public void buscaConvalidacoes(int p_id, int cod_ppc){
		ConvalidacaoDAO c_dao = new ConvalidacaoDAO();
		for(Disciplina nao_encontrada : nao_encontradas){
			//System.out.println(nao_encontrada.getCod_disciplina()+" "+p_id);
			Convalidacao c = c_dao.buscaConvalidacao(nao_encontrada.getCod_disciplina(), p_id);
			Convalidacao bct = c_dao.buscaConvalidacao(nao_encontrada.getCod_disciplina(), cod_ppc);
			if(c.getCod_convalidacao() != null){
				//System.out.println("c: "+c.getCod_convalidacao());
				convalidacoes.add(c);
			} else if(bct.getCod_convalidacao() != null){
				convalidacoes.add(bct);
			} else{
				//Conta como livre
				//System.out.println("n: "+nao_encontrada.getCod_disciplina()+" "+nao_encontrada.getNome());
				livres.add(nao_encontrada);
			}
		}
	}
	
	//M�todo para verificar se a convalida��o pertence ao PPC
	public void verificaConvalidacao(Convalidacao convalidacao){
		System.out.println("convalidada: "+convalidacao.getCod_convalidacao());
		boolean convalidado = false;
		DisciplinaDAO d_dao = new DisciplinaDAO();
		int i;
		String status = "";
		//Verifica na grade do ppc
		for(i=0;i<grade_ppc.size();i++){
			String cod = grade_ppc.get(i).getCod_disciplina();
			if(cod.equals(convalidacao.getCod_convalidacao())){
				convalidado = true;
				//Disciplina d = d_dao.buscaDisciplina(convalidacao.getCod_convalidacao());
				//creditos += d.getT() + d.getP();
						
				//System.out.println("convalidada: "+d.getNome()+" "+d.getT()+" "+d.getP());
				//verifica se � obrigat�ria ou limitada
				status = grade_ppc.get(i).getStatus();
				if(status.equals("Obrigat�ria")){
					//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
					Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
					//cred_obrigatorio += convalidada.getT() + convalidada.getP();//d.getT() + d.getP();
					convalidada.setNome(convalidada.getNome().toUpperCase());
					cursado_obrigatorio_curso += convalidada.getT() + convalidada.getP();
					obrigatorias.add(convalidada);
					System.out.println("origem o: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
				} else{
					//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
					Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
					convalidada.setNome(convalidada.getNome().toUpperCase());
					cursado_limitado+= convalidada.getT() + convalidada.getP();//d.getT() + d.getP();
					limitadas.add(convalidada);
					System.out.println("origem m: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
				}
			}
		}
		//Verifica na grade do bct
		for(i=0;i<grade_bct.size();i++){
			String cod = grade_bct.get(i).getCod_disciplina();
			if(cod.equals(convalidacao.getCod_convalidacao())){
				convalidado = true;
				//Disciplina d = d_dao.buscaDisciplina(convalidacao.getCod_convalidacao());
						
				//System.out.println("convalidada: "+d.getNome()+" "+d.getT()+" "+d.getP());
				//verifica se � obrigat�ria
				status = grade_bct.get(i).getStatus();
				if(status.equals("Obrigat�ria")){
					//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
					Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
					convalidada.setNome(convalidada.getNome().toUpperCase());
					//cred_obrigatorio += convalidada.getT() + convalidada.getP();//d.getT() + d.getP();
					cursado_bct += convalidada.getT() + convalidada.getP();
					obrigatoria_bct.add(convalidada);
					System.out.println("origem t: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
				} else{
					convalidado = false;
				}
			}
		}
		//Convalida��o n�o encontrada nos projetos pedag�gicos
		if(convalidado == false){
			//Conta os cr�ditos como livres
			//Buscar a disciplina de origem para adicionar os cr�ditos e colocar na lista
			Disciplina convalidada = d_dao.buscaDisciplina(convalidacao.getCod_disciplina());
			convalidada.setNome(convalidada.getNome().toUpperCase());
			//Disciplina d = d_dao.buscaDisciplina(convalidacao.getCod_convalidacao());
			livres.add(convalidada);
			System.out.println("origem l: "+convalidada.getNome()+" "+convalidada.getT()+" "+convalidada.getP());
		}
	}
	
	//M�todo para gerar o PDF
	public void geraPDF(){
		Document document = new Document(PageSize.A4);
		document.setMargins(10, 10, 50, 50);
		//document.setMarginMirroring(true);
		String name = nome+"_"+ra+".pdf";
		name = name.replaceAll(" ", "_");
        try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(name));
			document.open();
	        
	        Font title = FontFactory.getFont(FontFactory.TIMES_BOLD,14,BaseColor.BLACK);
	        Font campos = FontFactory.getFont(FontFactory.TIMES_BOLD,12,BaseColor.WHITE);
	        
	        //Cabe�alho das Tabelas de Disciplinas
	        Paragraph c_1 = new Paragraph("C�DIGO",campos);
	        Paragraph c_2 = new Paragraph("DISCIPLINA",campos);
	        Paragraph c_3 = new Paragraph("CR�DITOS",campos);
	        PdfPCell h_1 = new PdfPCell(c_1);
	        PdfPCell h_2 = new PdfPCell(c_2);
	        PdfPCell h_3 = new PdfPCell(c_3);
	        h_1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        h_1.setBackgroundColor(new BaseColor(0,102,0));
	        h_2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        h_2.setBackgroundColor(new BaseColor(0,102,0));
	        h_3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        h_3.setBackgroundColor(new BaseColor(0,102,0));
	        
	        //Lista de Disciplinas do BC&T
	        PdfPTable bct = new PdfPTable(3);
	        bct.setWidths(new int[]{2,6,2});
	        //Cabe�alho
	        bct.addCell(h_1);
	        bct.addCell(h_2);
	        bct.addCell(h_3);
	        //La�o para gerar o conte�do da tabela
	        int i, tp = 0;
	        for(i=0;i<obrigatoria_bct.size();i++){
	        	Paragraph col1;
		        Paragraph col2;
		        Paragraph col3;
		        PdfPCell linha1;
		        PdfPCell linha2;
		        PdfPCell linha3;
	        	tp = obrigatoria_bct.get(i).getT() + obrigatoria_bct.get(i).getP();
	        	if(i%2==0){
	        		col1 = new Paragraph(obrigatoria_bct.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(204,255,204));
	        		col2 = new Paragraph(obrigatoria_bct.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(204,255,204));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(204,255,204));
	        	} else{
	        		col1 = new Paragraph(obrigatoria_bct.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(255,255,153));
	        		col2 = new Paragraph(obrigatoria_bct.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(255,255,153));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(255,255,153));
	        	}
	        	bct.addCell(linha1);
	        	bct.addCell(linha2);
	        	bct.addCell(linha3);
	        	//bct.addCell(obrigatoria_bct.get(i).getCod_disciplina());
	        	//bct.addCell(obrigatoria_bct.get(i).getNome());
	        	//bct.addCell(Integer.toString(tp));
	        }
	        
	        //Lista de Disciplinas do Curso
	        PdfPTable curso = new PdfPTable(3);
	        curso.setWidths(new int[]{2,6,2});
	        //Cabe�alho
	        curso.addCell(h_1);
	        curso.addCell(h_2);
	        curso.addCell(h_3);
	        //La�o para gerar o conte�do da tabela
	        Paragraph col1;
	        Paragraph col2;
	        Paragraph col3;
	        PdfPCell linha1;
	        PdfPCell linha2;
	        PdfPCell linha3;
	        for(i=0;i<obrigatorias.size();i++){
	        	tp = obrigatorias.get(i).getT() + obrigatorias.get(i).getP();
	        	if(i%2==0){
	        		col1 = new Paragraph(obrigatorias.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(204,255,204));
	        		col2 = new Paragraph(obrigatorias.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(204,255,204));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(204,255,204));
	        	} else{
	        		col1 = new Paragraph(obrigatorias.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(255,255,153));
	        		col2 = new Paragraph(obrigatorias.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(255,255,153));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(255,255,153));
	        	}
	        	curso.addCell(linha1);
	        	curso.addCell(linha2);
	        	curso.addCell(linha3);
	        	//curso.addCell(obrigatorias.get(i).getCod_disciplina());
	        	//curso.addCell(obrigatorias.get(i).getNome());
	        	//tp = obrigatorias.get(i).getT() + obrigatorias.get(i).getP();
	        	//curso.addCell(Integer.toString(tp));
	        }
	        
	        //Lista de Disciplinas de Op��o Limitada
	        PdfPTable limitada = new PdfPTable(3);
	        limitada.setWidths(new int[]{2,6,2});
	        //Cabe�alho
	        limitada.addCell(h_1);
	        limitada.addCell(h_2);
	        limitada.addCell(h_3);
	        //La�o para gerar o conte�do da tabela
	        for(i=0;i<limitadas.size();i++){
	        	tp = limitadas.get(i).getT() + limitadas.get(i).getP();
	        	if(i%2==0){
	        		col1 = new Paragraph(limitadas.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(204,255,204));
	        		col2 = new Paragraph(limitadas.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(204,255,204));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(204,255,204));
	        	} else{
	        		col1 = new Paragraph(limitadas.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(255,255,153));
	        		col2 = new Paragraph(limitadas.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(255,255,153));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(255,255,153));
	        	}
	        	limitada.addCell(linha1);
	        	limitada.addCell(linha2);
	        	limitada.addCell(linha3);
	        	//limitada.addCell(limitadas.get(i).getCod_disciplina());
	        	//limitada.addCell(limitadas.get(i).getNome());
	        	//tp = limitadas.get(i).getT() + limitadas.get(i).getP();
	        	//limitada.addCell(Integer.toString(tp));
	        }
	        
	        //Lista de Disciplinas Livres
	        PdfPTable livre = new PdfPTable(3);
	        livre.setWidths(new int[]{2,6,2});
	        //Cabe�alho
	        livre.addCell(h_1);
	        livre.addCell(h_2);
	        livre.addCell(h_3);
	        //La�o para gerar o conte�do da tabela
	        for(i=0;i<livres.size();i++){
	        	tp = livres.get(i).getT() + livres.get(i).getP();
	        	if(i%2==0){
	        		col1 = new Paragraph(livres.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(204,255,204));
	        		col2 = new Paragraph(livres.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(204,255,204));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(204,255,204));
	        	} else{
	        		col1 = new Paragraph(livres.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(255,255,153));
	        		col2 = new Paragraph(livres.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(255,255,153));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(255,255,153));
	        	}
	        	livre.addCell(linha1);
	        	livre.addCell(linha2);
	        	livre.addCell(linha3);
	        	//livre.addCell(livres.get(i).getCod_disciplina());
	        	//livre.addCell(livres.get(i).getNome());
	        	//tp = livres.get(i).getT() + livres.get(i).getP();
	        	//livre.addCell(Integer.toString(tp));
	        }
	        
	        //Lista de Disciplinas n�o catalogadas
	        PdfPTable nao_catalogadas = new PdfPTable(3);
	        nao_catalogadas.setWidths(new int[]{2,6,2});
	        //Cabe�alho
	        nao_catalogadas.addCell(h_1);
	        nao_catalogadas.addCell(h_2);
	        nao_catalogadas.addCell(h_3);
	        //La�o para gerar o conte�do da tabela
	        for(i=0;i<nao_catalogada.size();i++){
	        	tp = nao_catalogada.get(i).getT() + nao_catalogada.get(i).getP();
	        	if(i%2==0){
	        		col1 = new Paragraph(nao_catalogada.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(204,255,204));
	        		col2 = new Paragraph(nao_catalogada.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(204,255,204));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(204,255,204));
	        	} else{
	        		col1 = new Paragraph(nao_catalogada.get(i).getCod_disciplina());
	        		linha1 = new PdfPCell(col1);
	        		linha1.setBackgroundColor(new BaseColor(255,255,153));
	        		col2 = new Paragraph(nao_catalogada.get(i).getNome());
	        		linha2 = new PdfPCell(col2);
	        		linha2.setBackgroundColor(new BaseColor(255,255,153));
	        		col3 = new Paragraph(Integer.toString(tp));
	        		linha3 = new PdfPCell(col3);
	        		linha3.setBackgroundColor(new BaseColor(255,255,153));
	        	}
	        	nao_catalogadas.addCell(linha1);
	        	nao_catalogadas.addCell(linha2);
	        	nao_catalogadas.addCell(linha3);
	        	//livre.addCell(livres.get(i).getCod_disciplina());
	        	//livre.addCell(livres.get(i).getNome());
	        	//tp = livres.get(i).getT() + livres.get(i).getP();
	        	//livre.addCell(Integer.toString(tp));
	        }
	        
	        //Montando o arquivo
	        PdfPTable relatorio = new PdfPTable(1);
	        
	        Paragraph p = new Paragraph("RELAT�RIO DO SISTEMA DE CONTAGEM DE CR�DITOS",title);
	        PdfPCell cell = new PdfPCell(p);
	        cell.setBorder(PdfPCell.NO_BORDER);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Paragraph r = new Paragraph(" ");
	        PdfPCell row = new PdfPCell(r);
	        row.setRowspan(3);
	        row.setBorder(PdfPCell.NO_BORDER);
	        relatorio.addCell(row);
	        relatorio.addCell(cell);
	        relatorio.addCell(row);
	        document.add(relatorio);

	        //Informa��es do Aluno
	        PdfPTable aluno = new PdfPTable(2);
	        aluno.setWidths(new int[]{1,5});
	        Paragraph info_nome = new Paragraph("NOME: ",title);
	        Paragraph info_ra = new Paragraph("RA: ",title);
	        Paragraph aluno_nome = new Paragraph(nome, title);
	        Paragraph aluno_ra = new Paragraph(Integer.toString(ra),title);
	        Paragraph info_matriz = new Paragraph("MATRIZ: ",title);
	        Paragraph ano_matriz = new Paragraph(Integer.toString(matriz),title);
	        PdfPCell info1 = new PdfPCell(info_nome);
	        PdfPCell info2 = new PdfPCell(info_ra);
	        PdfPCell info3 = new PdfPCell(aluno_nome);
	        PdfPCell info4 = new PdfPCell(aluno_ra);
	        PdfPCell info5 = new PdfPCell(info_matriz);
	        PdfPCell info6 = new PdfPCell(ano_matriz);
	        info1.setBorder(PdfPCell.NO_BORDER);
	        info2.setBorder(PdfPCell.NO_BORDER);
	        info3.setBorder(PdfPCell.NO_BORDER);
	        info4.setBorder(PdfPCell.NO_BORDER);
	        info5.setBorder(PdfPCell.NO_BORDER);
	        info6.setBorder(PdfPCell.NO_BORDER);
	        //aluno.addCell(row);
	        //aluno.addCell(row);
	        aluno.addCell(info1);
	        aluno.addCell(info3);
	        aluno.addCell(info2);
	        aluno.addCell(info4);
	        aluno.addCell(info5);
	        aluno.addCell(info6);
	        aluno.addCell(row);
	        aluno.addCell(row);
	        document.add(aluno);
	        //document.add(new Paragraph(""));
	        
	        PdfPTable porcentagens = new PdfPTable(4);
	        //Cabe�alho
	        Paragraph i_1 = new Paragraph("Tipo de Disciplina",campos);
	        Paragraph i_2 = new Paragraph("Deve Cursar",campos);
	        Paragraph i_3 = new Paragraph("Cursou",campos);
	        Paragraph i_4 = new Paragraph("Porcentagem",campos);
	        PdfPCell pc_1 = new PdfPCell(i_1);
	        PdfPCell pc_2 = new PdfPCell(i_2);
	        PdfPCell pc_3 = new PdfPCell(i_3);
	        PdfPCell pc_4 = new PdfPCell(i_4);
	        pc_1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        pc_1.setBackgroundColor(new BaseColor(0,102,0));
	        pc_2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        pc_2.setBackgroundColor(new BaseColor(0,102,0));
	        pc_3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        pc_3.setBackgroundColor(new BaseColor(0,102,0));
	        pc_4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        pc_4.setBackgroundColor(new BaseColor(0,102,0));
	        porcentagens.addCell(pc_1);
	        porcentagens.addCell(pc_2);
	        porcentagens.addCell(pc_3);
	        porcentagens.addCell(pc_4);
	        //Conte�do da tabela informativa
	        Paragraph pg_1;
	        Paragraph pg_2;
	        Paragraph pg_3;
	        Paragraph pg_4;
	        PdfPCell cl_1;
	        PdfPCell cl_2;
	        PdfPCell cl_3;
	        PdfPCell cl_4;
	        
	        pg_1 = new Paragraph("Obrigat�rias BC&T");
	        cl_1 = new PdfPCell(pg_1);
	        cl_1.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_1);
	        pg_2 = new Paragraph(Integer.toString(cred_obrigatorio_bct));
	        cl_2 = new PdfPCell(pg_2);
	        cl_2.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_2);
	        pg_3 = new Paragraph(Integer.toString(cursado_bct));
	        cl_3 = new PdfPCell(pg_3);
	        cl_3.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_3);
	        pg_4 = new Paragraph(Float.toString(p_bct)+"%");
	        cl_4 = new PdfPCell(pg_4);
	        cl_4.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_4);
	        
	        pg_1 = new Paragraph("Obrigat�rias "+sigla);
	        cl_1 = new PdfPCell(pg_1);
	        cl_1.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_1);
	        pg_2 = new Paragraph(Integer.toString(cred_obrigatorio_curso));
	        cl_2 = new PdfPCell(pg_2);
	        cl_2.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_2);
	        pg_3 = new Paragraph(Integer.toString(cursado_obrigatorio_curso));
	        cl_3 = new PdfPCell(pg_3);
	        cl_3.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_3);
	        pg_4 = new Paragraph(Float.toString(p_curso)+"%");
	        cl_4 = new PdfPCell(pg_4);
	        cl_4.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_4);
	        
	        pg_1 = new Paragraph("Limitadas "+sigla);
	        cl_1 = new PdfPCell(pg_1);
	        cl_1.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_1);
	        pg_2 = new Paragraph(Integer.toString(cred_limitado_curso));
	        cl_2 = new PdfPCell(pg_2);
	        cl_2.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_2);
	        pg_3 = new Paragraph(Integer.toString(cursado_limitado));
	        cl_3 = new PdfPCell(pg_3);
	        cl_3.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_3);
	        pg_4 = new Paragraph(Float.toString(p_limitado)+"%");
	        cl_4 = new PdfPCell(pg_4);
	        cl_4.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_4);
	        
	        pg_1 = new Paragraph("Livres "+sigla);
	        cl_1 = new PdfPCell(pg_1);
	        cl_1.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_1);
	        pg_2 = new Paragraph(Integer.toString(cred_curso_livre));
	        cl_2 = new PdfPCell(pg_2);
	        cl_2.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_2);
	        pg_3 = new Paragraph(Integer.toString(cursado_livre));
	        cl_3 = new PdfPCell(pg_3);
	        cl_3.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_3);
	        pg_4 = new Paragraph(Float.toString(p_livre)+"%");
	        cl_4 = new PdfPCell(pg_4);
	        cl_4.setBackgroundColor(new BaseColor(255,255,153));
	        porcentagens.addCell(cl_4);
	        
	        pg_1 = new Paragraph("Total do Curso");
	        cl_1 = new PdfPCell(pg_1);
	        cl_1.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_1);
	        pg_2 = new Paragraph(Integer.toString(cred_total_curso));
	        cl_2 = new PdfPCell(pg_2);
	        cl_2.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_2);
	        pg_3 = new Paragraph(Integer.toString(cursado_total));
	        cl_3 = new PdfPCell(pg_3);
	        cl_3.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_3);
	        pg_4 = new Paragraph(Float.toString(p_total)+"%");
	        cl_4 = new PdfPCell(pg_4);
	        cl_4.setBackgroundColor(new BaseColor(204,255,204));
	        porcentagens.addCell(cl_4);

	        document.add(porcentagens);
	        
	        PdfPTable bct_table = new PdfPTable(1);
	        Paragraph p2 = new Paragraph("DISCIPLINAS OBRIGAT�RIAS DO BC&T",title);
	        PdfPCell cell2 = new PdfPCell(p2);
	        cell2.setBorder(PdfPCell.NO_BORDER);
	        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        bct_table.addCell(row);
	        bct_table.addCell(cell2);
	        bct_table.addCell(row);
	        document.add(bct_table);
	        //document.add(new Paragraph(""));
	        //document.add(new Paragraph(""));
	        document.add(bct);
	        //document.add(new Paragraph(""));
	        
	        PdfPTable curso_table = new PdfPTable(1);
	        Paragraph p3 = new Paragraph("DISCIPLINAS OBRIGAT�RIAS DO "+sigla,title);
	        PdfPCell cell3 = new PdfPCell(p3);
	        cell3.setBorder(PdfPCell.NO_BORDER);
	        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        curso_table.addCell(row);
	        curso_table.addCell(cell3);
	        curso_table.addCell(row);
	        document.add(curso_table);
	        //document.add(new Paragraph(""));
	        //document.add(new Paragraph(""));
	        document.add(curso);
	        //document.add(new Paragraph(""));
	        
	        PdfPTable limitadas_table = new PdfPTable(1);
	        Paragraph p4 = new Paragraph("DISCIPLINAS LIMITADAS DO "+sigla,title);
	        PdfPCell cell4 = new PdfPCell(p4);
	        cell4.setBorder(PdfPCell.NO_BORDER);
	        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        limitadas_table.addCell(row);
	        limitadas_table.addCell(cell4);
	        limitadas_table.addCell(row);
	        document.add(limitadas_table);
	        //document.add(new Paragraph(""));
	        //document.add(new Paragraph(""));
	        document.add(limitada);
	        //document.add(new Paragraph(""));
	        
	        PdfPTable livres_table = new PdfPTable(1);
	        Paragraph p5 = new Paragraph("DISCIPLINAS LIVRES DO "+sigla,title);
	        PdfPCell cell5 = new PdfPCell(p5);
	        cell5.setBorder(PdfPCell.NO_BORDER);
	        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
	        livres_table.addCell(row);
	        livres_table.addCell(cell5);
	        livres_table.addCell(row);
	        document.add(livres_table);
	        //document.add(new Paragraph(""));
	        //document.add(new Paragraph(""));
	        document.add(livre);
	        
	        PdfPTable calalogo_table = new PdfPTable(1);
	        Paragraph p6 = new Paragraph("DISCIPLINAS N�O ENCONTRADAS NO BANCO DE DADOS DO SISTEMA",title);
	        PdfPCell cell6 = new PdfPCell(p6);
	        cell6.setBorder(PdfPCell.NO_BORDER);
	        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
	        calalogo_table.addCell(row);
	        calalogo_table.addCell(cell6);
	        calalogo_table.addCell(row);
	        document.add(calalogo_table);
	        //document.add(new Paragraph(""));
	        //document.add(new Paragraph(""));
	        document.add(nao_catalogadas);
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
        document.close();
	}
}