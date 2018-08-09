<%@include file="/libs/foundation/global.jsp" %><%
%><%@page session="false" %><%

%>
<div class="page"
        id="scroll-top"
        role="main"
        ng-controller="MainCtrl"
        ng-init="app.resource = '${resourcePath}'; init();">

	<form class="coral-Form coral-Form--vertical acs-form"
        novalidate
        name="params"
        ng-hide="app.running">
        
        <section class="coral-Form-fieldset">
        	 <div class="coral-Form-fieldwrapper">
    			<label id="label-vertical-0" class="coral-Form-fieldlabel">Available Schedulers: </label>
        		<coral-select class="coral-Form-field" name="pid" placeholder="---Please select---" id="pid" ng-model="app.pid" labelledby="label-vertical-0"></coral-select>
				<!-- button class="coral-Button coral-Button--primary" ng-click="getExpression()">Get Expression</button -->
        		<br/>
        	</div>
        	<div class="coral-Form-fieldwrapper cron-selector" style="display:none">
        		<div id='selector' class="coral-Form-field"></div>
       			<span id='selector-val' style="display:none"></span>
        	</div>
        	<div class="coral-Form-fieldwrapper cron-selector" style="display:none">
        		<button class="coral-Button coral-Button--primary coral-Form-field" ng-click="updateExpression()">Update</button>
        	</div>
        </section>
        
        
	</form>
	


</div>