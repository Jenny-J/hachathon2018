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
	
	<script>
	function changeParameter(checkedValue, obj)
	{
		var id = '#'+checkedValue;
		if(obj.checked)
		{
			 var newTR = $('#parameters').find(id);
			 
			 if (!newTR.length)
			 {
				 var newTR= "<tr id=\"" + checkedValue +"\"><td width=\"35%\">" + checkedValue + "</td><td width=\"35%\"><input type=\"radio\" name=\"selectedTypes[" + 
				     checkedValue + "]\" value=\"StringList\"/>String List <input type=\"radio\" name=\"selectedTypes[" + 
				     checkedValue + "]\" value=\"NumberList\"/>Number List </td><td width=\"15%\"><input type=\"text\" name=\"defaultValues["+ 
				     checkedValue + "]\" /></td><td width=\"15%\"><input type=\"text\" name=\"testValues["+ 
				     checkedValue + "]\" /></td></tr>";
				     
				 $('#parameters').append(newTR);
			 }
			 
			
		}
		else{
			
			var newTR = $('#parameters').find(id);
			 if (newTR.length)
			{
				 newTR.remove();
			}
		}
	
		
		
	}
	
	</script>
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
					<li><a href="/run/retrievetrigger">Run Trigger</a></li>
					<li class="active"><a href="#">Predictive Model</a></li>
					<li><a href="/predictive/run/retrieve">Run Predictive</a></li>
					<li><a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference" target="_blank">JavaScript Help</a></li>
					<li><a href="https://www.tutorialspoint.com/r/index.htm" target="_blank">R Help</a></li>
				</ul>
			</div>
		</div>
	</nav>
<form:form	action="/predictive/test" method="POST"  modelAttribute="form">
	<div class="container">

		<div class="starter-template">
		   <h2>Table</h2>
		      <form:select class="form-control" path="tableName" onchange='this.form.submit()'>
		       <form:options items="${allTables}" /> 
			 </form:select>
			<h2>Columns</h2>
			<%-- <h2>Message: ${message}</h2> --%>
			<table>
			<c:forEach var="i" items="${form.allColumns}" varStatus="status">
			 <c:if test="${status.count %4==1 }">
			  <tr><td>   <c:out value = "${i}"/> <form:checkbox path="selectedColumns" value="${i}" onchange="changeParameter('${i}', this)" /></td>
			 </c:if>
			  <c:if test="${status.count %4==2 || status.count %4==3}">
             	  <td>   <c:out value = "${i}"/> <form:checkbox path="selectedColumns" value="${i}" onchange="changeParameter('${i}', this)" /></td>
         	 </c:if>
             <c:if test="${status.count %4==0 }">
             	  <td>   <c:out value = "${i}"/> <form:checkbox path="selectedColumns" value="${i}" onchange="changeParameter('${i}', this)" /></td></tr>
         	 </c:if>
            
  		</c:forEach>
  		</table>
		</div>
		
		<div class="starter-template">
				<h1>Predictive Model Entry</h1>
			<label>Model Name: </label>	<form:input type="text" path="algorithmData.algorithmName" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label>Language: <form:radiobutton  path="algorithmData.language" value="JavaScript"/>Java Script &nbsp;&nbsp;&nbsp;&nbsp;
				<form:radiobutton  path="algorithmData.language" value="R" />R</label> 
			<hr>
			<label>Function Definition: </label>
				<br>
				<form:textarea path="algorithmData.algorithmFunction"  rows="15" cols="100" ></form:textarea>
				
				<table width="90%">
				<thead><tr><td width="35%"><label>Parameter Name</label> </td>
					<td width="35%" ><label>Data Type (Datetime 2018-08-13T15:23:01Z, as String type)</label></td>
					<td width="15%"><label>Default Value</label></td>
					<td width="15%" ><label>Test Value (separated by ,)</label></td>
					</tr></thead>
				<tbody id="parameters">
				<c:forEach var="selected" items="${form.selectedColumns}" varStatus="status">
				<tr id="${selected}"><td width="35%"><c:out value="${selected}"/></td>
					<%-- 	<td width="35%"><input type="radio" name="selectedTypes[${selected}]" value="String"/>String 
						<input type="radio"  name="selectedTypes[${selected}]" value="Number" />Number</td>
						<td width="15%"><input type="text" name="defaultValues[${selected}]" value=""/></td>
						<td width="15%"><input type="text" name="testValues[${selected}]"/></td> --%>	
						<td width="35%">
						<form:radiobutton  path="selectedTypes['${selected}']" value="StringList" />String List
						<form:radiobutton  path="selectedTypes['${selected}']" value="NumberList" />Number List
					<%-- 	<form:radiobutton  path="selectedTypes['${selected}']" value="DateTimeList" />DateTime List (ex:2018-08-13T15:23:01Z) --%>
						</td>
						<td width="15%"><form:input type="text" path="defaultValues['${selected}']" /> </td>
						<td width="15%"><form:input type="text" path="testValues['${selected}']"/></td>
				</tr>
						
						  
        
				</c:forEach>
				</tbody>	
				</table>
				<br/>
				<div class="span4">
				<div class='pull-left'>
				  <input type="submit" name="param" value="Test" class="btn"/>
				 </div>
				 <div class='pull-right'>
				  <input type="submit" name="param" value="Save" class="btn"/>
				 </div>
				</div>
				<div>
				<!-- <input type="submit" name="param" value="Test" class="btn"/>  --><h1><c:out value="${form.message} "/> </h1>
				</div>
				<!-- <div align="right" >
				<input type="submit" name="param" value="Save" class="btn"/>
				
				</div> -->
				
				
			
			
		</div>
		
		<div class="starter-template">
			<p> </p>
			</div>
	
	</div>
	</form:form>
	
	<script type="text/javascript" src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</body>

</html>