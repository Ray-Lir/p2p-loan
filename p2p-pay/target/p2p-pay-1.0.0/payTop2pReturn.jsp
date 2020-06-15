<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/1/22
  Time: 16:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>pay返回同步返回参数给p2p</title>
</head>
<body>
<form method="post" action="${pay_p2p_return_url}">
    <input type="hidden" name="signVerified" value="${signVerified}"/>
    <c:choose>
        <c:when test="${not empty params}">
            <c:forEach items="${params}" var="paramMap">
                <input type="hidden" name="${paramMap.key}" value="${paramMap.value}"/>
            </c:forEach>
        </c:when>
    </c:choose>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
