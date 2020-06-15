<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/1/15
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试页面</title>
</head>
<body>
<h1>历史平均年化收益率：${historyAverageRate}</h1>
<h1>平台注册总人数：${allUserCount}</h1>
<h1>平台累计投资金额：${allBidMoney}</h1>
<h2>测试:<fmt:formatNumber value="1234567890" type="currency"/></h2>
</body>
</html>
