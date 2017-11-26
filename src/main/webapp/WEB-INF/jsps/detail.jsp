<%--
  Created by IntelliJ IDEA.
  User:
  Date: 2017/11/4
  Time: 10:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- import jstl tag -->
<%@include file="common/tag.jsp" %>

<!DOCTYPE html>
<html>

<head>
    <title>秒杀详情页</title>

    <!--静态包含-->
    <%@include file="common/head.jsp"%>

</head>

<body>

    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading">
                <h1>${secKill.name}</h1>
            </div>

            <div class="panel-body">
                <h2 class="text-danger">
                    <!--显示time图标-->
                    <span class="glyphicon glyphicon-time"></span>
                    <!--展示倒计时-->
                    <span class="glyphicon" id="seckill-box"></span>
                </h2>
            </div>
        </div>
    </div>

    <div id="killPhoneModal" class="modal fade">

        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>
                        秒杀电话：
                    </h3>
                </div>

                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone" id="killPhoneKey"
                                   placeholder="请填写手机号^O^" class="form-control"/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <!--验证信息-->
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneBtn" class="btn btn-success">
                        <span class="glyphicon glyohicon-phone"></span>
                        Submit
                    </button>
                </div>
            </div>
        </div>

    </div>

</body>

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!--使用CDN获取js公共插件-->
<!--jQuery cookie操作插件-->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

<!--jQuery countdown倒计时插件-->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<!--编写交互逻辑-->
<!--引入相关的javascript-->
<script src="/resources/script/seckill.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function () {
        //use EL expression pass parameters
        seckill.detail.init({
            seckillId : ${secKill.seckillId},
            //.time convert time to millisecond
            startTime : ${secKill.startTime.time},
            endTime : ${secKill.endTime.time}
        });
    });
</script>

</html>
