<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head></head>
<body>
Please do the registration
<form:form commandName="businessRegistration">
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

    <b>Business Name: </b><form:input path="bizName.businessName"/><br/>
    <b>Biz Types: </b><form:input path="bizName.businessTypes"/><br/>
    <b>Address: </b><form:input path="businessAddress"/><br/>
    <b>Phone: </b><form:input path="businessPhone"/><br/>

    <input type="submit" name="_eventId_submit" value="Submit" />
    <input type="submit" name="_eventId_cancel" value="Cancel" />
</form:form>
</body>
</html>