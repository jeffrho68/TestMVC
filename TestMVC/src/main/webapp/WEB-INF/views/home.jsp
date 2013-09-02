<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>Bootstrap 101 Template</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="resources/bootstrap-3.0.0/dist/css/bootstrap.min.css" rel="stylesheet" media="screen">

<!-- Custom styles for this template -->
    <link href="resources/css/starter-template.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="resources/js/html5shiv.js"></script>
      <script src="resources/js/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	
	<div id="navmain" class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">TestMVC</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a>
            </li>
            <li><a id="about" href="#about">About</a>
            </li>
            <li><a id="contact" href="#contact">Contact</a>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
    
    <div class="container">
      <div class="starter-template">
       <h1>Hello, world!</h1>
	   <P>The time on the server is ${serverTime}.</P>
      </div>
	</div><!-- /.container -->
	
	<!-- About Modal -->
  <div class="modal fade" id="aboutModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">About TestMVC</h4>
        </div>
        <div class="modal-body">
        <p>This is a test application using Spring MVC and bootstrap.js</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->
  
  
	
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="resources/js/jquery-1.10.2.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="resources/bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>
	<script src="resources/js/home.js"></script>
</body>
</html>
