//存放主要交互逻辑js代码
//注意：javascript模块化编程
var seckill = {
    //封装秒杀相关ajax的url
    URL : {
        now:function () {
            return '/time/now';
        },

        exposer : function (seckillId) {
            return '/' + seckillId + '/exposer';
        },

        execution : function (seckillId, md5) {
            return '/' + seckillId + '/' + md5 + '/execution';
        }
    },

    handleSeckill : function (seckillId, node) {
        //handle seckill
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">Start</button>');

        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']){
                var exposer = result['data'];
                if (exposer['exposed']){

                    //seckill has started
                    //get seckill url first
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log('killUrl:' + killUrl);

                    //register a click-event for button
                    //we use "one()" method here instead of "click()" method
                    //because method "one()" bind "killUrl" with "killBtn" only once,
                    //which can prevent server getting lots of seckill request with same seckillUrl.
                    $('#killBtn').one('click', function () {

                        //execute seckill request
                        //1. Disable the button
                        $(this).addClass('disabled');

                        //2. Send seckill request and execute seckill
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];

                                //3. Show seckill result
                                node.html('<span class="label label-success">' + stateInfo + '</span>')
                            }
                        });
                    });

                    node.show();

                }else {
                    //seckill has not started, due to different time on different computers
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end']

                    seckill.countDown(seckillId, now, start, end);
                }
            }else{
                console.log('result:' + result);
            }
        })
    },

    //This method validate the phone of user.
    validatePhone : function(phone){

        //isNaN(phone) : phone不是数字为true
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else {
            return false;
        }
    },

    countDown : function (seckillId, nowTime, startTime, endTime) {

        var seckillBox = $("#seckill-box");
        //check time
        if (nowTime > endTime){

            //seckill has ended
            seckillBox.html('Seckill has ended.');

        }else if (nowTime < startTime){

            //seckill has not started, we need to display time counting down
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {

                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);

            }).on('finish.countdown', function () {

                //get seckill url and execute seckill
                seckill.handleSeckill(seckillId, seckillBox);

            });
        }else {
            //seckill is ongoing
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },

    //详情页相关的秒杀逻辑
    detail : {

        //详情页初始化
        //params是json类型的参数
        init : function (params) {

            //手机登录验证和计时交互
            //规划我们的交互流程

            //get user's phone number from cookie
            var killPhone = $.cookie('killPhone');

            //javascript get json parameters by means of params['']
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];

            //validate user phone invoking method "validatePhone"
            if (!seckill.validatePhone(killPhone)){
                //绑定phone，控制输出
                //"#killPhoneModal"获取detail.jsp文件中的killPhoneModal节点
                var killPhoneModal = $("#killPhoneModal");
                killPhoneModal.modal({

                    //显示弹出层
                    show:true,

                    //禁止位置关闭，即禁止用户点击弹出层以外的其他地方关闭弹出层
                    backdrop:'static',

                    //关闭键盘事件
                    keyboard : false
                });

                //弹出层按钮点击事件
                $('#killPhoneBtn').click(function () {

                    //获取用户输入的手机号
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone' + inputPhone);//TODO
                    if (seckill.validatePhone(inputPhone)){

                        //将用户输入的手机号写入cookie
                        // 注意此处的killPhone应该与上面从cookie中获取用户手机号处一致
                        //$.cookie('killPhone', inputPhone, {expires:7, path : '/seckill'});
                        $.cookie('killPhone', inputPhone, {expires:7, path : '/'});

                        //刷新页面
                        //刷新页面之后会再次执行上面detail中的逻辑，
                        //即获取手机号、验证手机号等等
                        window.location.reload();
                    }else {
                        $('#killPhoneMessage').hide()
                            .html('<label class="label label-danger">手机号错误</label>')
                            .show(300);
                    }
                });
            }

            //User has logged in
            //计时交互
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                }else {
                    console.log('result:' + result);
                }
            });
        }
    }
}