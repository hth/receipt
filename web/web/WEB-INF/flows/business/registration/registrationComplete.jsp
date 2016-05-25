<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head></head>
<body>
<c:choose>
    <c:when test="${businessRegistration.businessUser.businessUserRegistrationStatus eq 'C'}">
        Registration complete. Once your details are verified you would be notified.
    </c:when>
    <c:otherwise>
        Registration not complete. We could not verify your details.
    </c:otherwise>
</c:choose>
</body>
</html>