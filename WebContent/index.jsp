<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sistema de Contagem de Cr�ditos</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<style>
    .navbar {
      margin-bottom: 50px;
      border-radius: 0;
    }

     .jumbotron {
      background-color: #008000;
      color: #FFFFFF;
      margin-bottom: 0;
      height: 150px;
    }

    .row.content {
    	height: 500px;
    }

    .fundo{
    	background-color: #CCFFCC;
    }
    
    .menu{
    	background-color: #F2F2F2;
    }
    
    .sidenav {
      background-color: #FFFFFF;
      height: 100%;
    }

    footer {
      background-color: #CCFFCC;
      padding: 10px;
    }
</style>
</head>
<body>
<div class="jumbotron">
  <div class="container text-center">
    <h2>Sistema de Contagem de Cr�ditos</h2>
  </div>
</div>

<div class="container-fluid">
  <div class="row content">
    <div class="col-sm-2 sidenav hidden-xs">
      <ul class="nav nav-pills nav-stacked menu row content">
        <li class="fundo"><a href="index.jsp">Home</a></li>
        <li><a href="disciplina.jsp">Disciplina</a></li>
        <li><a href="ppc.jsp">Projeto Pedag�gico</a></li>
        <li><a href="convalidacao.jsp">Convalida��o</a></li>
        <li><a href="credito.jsp">Contagem de Cr�ditos</a></li>
      </ul><br>
    </div>
    <div class="col-sm-10">
    	<h2>Sobre o Sistema</h2>
    	<p>Sistema criado para auxliar nas atividades desenvolvidas pelo CMCC (Centro de Matem�tica, Computa��o e 
    	Cogni��o).</p>
    	<p>Esses sistema � respons�vel por automatizar a contagem de cr�ditos dos alunos matricualdos em algum 
    	dos cursos administrados pelo CMCC. Dessa forma � poss�vel avaliar o progresso do aluno no curso e verificar 
    	se ele est� apto para cursar alguma das disciplinas de est�gio ou se j� est� apto para fazer a integraliza��o 
    	do curso.</p>
    </div>
  </div>
</div>
</body>
</html>