<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head></head>
<body>
Please confirm your profile
<form:form commandName="businessRegistration">
    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

    <b>First Name: </b><form:input path="firstName"/><br/>
    <b>Last Name: </b><form:input path="lastName"/><br/>
    <b>Address: </b><form:input path="address"/><br/>
    <b>Phone: </b><form:input path="phone"/><br/>

    <c:if test="${!businessRegistration.emailValidated}">
    <b>Email Address : </b>${businessRegistration.email}<br/> has not been validated.
        Please validated email address to continue business account registration.
    </c:if>

    <c:if test="${businessRegistration.emailValidated}">
    <input type="submit" name="_eventId_submit" value="Submit" />
    </c:if>
    <input type="submit" name="_eventId_cancel" value="Cancel" />
</form:form>
</body>
</html>