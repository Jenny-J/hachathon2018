<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html lang="en">
<head>

	<!-- Access the bootstrap Css like this, 
		Spring boot will handle the resource mapping automcatically -->
	  <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.3.7/css/bootstrap.min.css" />  
	 <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> 
	 <c:url value="/css/main.css" var="jstlCss" />
	<link href="${jstlCss}" rel="stylesheet" />
	
	
</head>
<body>

	<nav class="navbar navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Analytic Tool</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="/">Trigger Model</a></li>
					<li class="active"><a href="#">Run Trigger</a></li>
					<li><a href="/predictive">Predictive Model</a></li>
					<li><a href="#about">Run Predictive</a></li>
					<li><a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference" target="_blank">JavaScript Help</a></li>
					<li><a href="https://www.tutorialspoint.com/r/index.htm" target="_blank">R Help</a></li>
				</ul>
			</div>
		</div>
	</nav>
<form:form	action="/predictive/run/retrieve" method="POST"  modelAttribute="form">
	<div class="container">
	<h1>Run Predictive Analyzer </h1>
		<div class="starter-template">
			<%-- <h2>Message: ${message}</h2> --%>
			 <div class="form-group">
			
		      <label for="sel1">Predictive Model:</label>
		      <form:select class="form-control" path="selectedAlgIndex"  onchange='this.form.submit()'>
			    <c:forEach var="one" items="${form.allAlg}" varStatus="loop">
			        <option value="${loop.index}" ><c:out value="${one.algorithmName}"/></option>
			    </c:forEach>
			    
			 </form:select>
	        
  		</div>
		
		<div class="starter-template">
			
	
				  <label>Predictive Model Name:  <c:out value="${form.selectedAlg.algorithmName}"/> </label>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  
				  <label>Language:  <c:out value="${form.selectedAlg.language}"/> </label>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  
				 <label>Table:  <c:out value="${form.selectedAlg.tableName}"/> </label>
				  <hr />
				<label>Function Definition:  </label>
				<pre><c:out value="${form.selectedAlg.algorithmFunction}"/></pre>
				 <hr />				
				<label>Partition Column:  <c:out value="${form.selectedAlg.partitionCol}"/> </label>
				<br/>
				<label>Run Duration From Current Time:</label> <form:input path="hours" size="20"/> <label>Hours</label>
				<br/>
				
				<label>RMS IDs (separated by ,):</label> <form:input path="rmsIDs" size="20"/>
				<div>
				<input type="submit" name="param" value="Run" class="btn"/>
				</div>
			
		</div>
		
			<div class="starter-template">
			 <h1><c:out value="${form.message} "/> </h1>
			</div>
	
	</div>
	</div>
	
	</form:form>		
	<script type="text/javascript" src="/webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script>
	document.getElementById("selectedAlgIndex").selectedIndex = <c:out value="${form.selectedAlgIndex}"/>;
	
	</script>

</body>

</html>