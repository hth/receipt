<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head></head>
<body>
This is review registration
<form:form commandName="businessRegistration">
    <b>First Name: </b><form:input path="firstName"/><br/>
    <b>Last Name: </b><form:input path="lastName"/><br/>
    <b>Address: </b><form:input path="address"/><br/>
    <b>Phone: </b><form:input path="phoneFormatted"/><br/>

    <b>Business Name: </b><form:input path="businessName"/><br/>
    <b>Biz Types: </b><form:input path="businessTypes"/><br/>
    <b>Address: </b><form:input path="businessAddress"/><br/>
    <b>Phone: </b><form:input path="businessPhoneFormatted"/><br/>

    <input type="submit" name="_eventId_confirm" value="Confirm" />
    <input type="submit" name="_eventId_revise" value="Revise" />
    <input type="submit" name="_eventId_cancel" value="Cancel" />
</form:form>
</body>
</html>
