
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>p2pToPayAlipay</title>
</head>
<body>
<form method="post" action="${p2p_pay_alipay_url}">
    <input type="hidden" name="out_trade_no" value="${rechargeNo}"/>
    <input type="hidden" name="total_amount" value="${rechargeMoney}"/>
    <input type="hidden" name="subject" value="${subject}"/>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
