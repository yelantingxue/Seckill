package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.dto.SecKillResult;
import org.seckill.entities.SecKill;
import org.seckill.enums.SecKillStateEnum;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class SecKillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<SecKill> list = secKillService.getSecKillList();
        model.addAttribute("list", list);
        return "list";
    }

    @RequestMapping(value = "/{secKillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("secKillId") Long secKillId, Model model) {
        if (secKillId == null) {
            return "redirect:/list";
        }

        SecKill secKill = secKillService.queryById(secKillId);
        if (secKill == null) {
            return "forward:/list";
        }

        model.addAttribute("secKill", secKill);
        return "detail";
    }

    @RequestMapping(value = "/{secKillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<Exposer> exposer(@PathVariable("secKillId") Long seckillId) {
        SecKillResult<Exposer> result;
        try {
            Exposer exposer = secKillService.exportSecKillUrl(seckillId);
            result = new SecKillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SecKillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "/{secKillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<SecKillExecution> execute(@PathVariable("secKillId") Long secKillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long userPhone) {

        if (userPhone == null) {
            return new SecKillResult<SecKillExecution>(false, "Have not registered.");
        }
        SecKillResult<SecKillExecution> result;

        try {

            //invoke seckill execution method by stored procedure.
            SecKillExecution execution = secKillService.executeSecKillByProcedure(secKillId, userPhone, md5);
            return new SecKillResult<SecKillExecution>(true, execution);

        } catch (SecKillCloseException e) {

            SecKillExecution execution = new SecKillExecution(secKillId, SecKillStateEnum.END);
            return new SecKillResult<SecKillExecution>(true, execution);

        } catch (RepeatSecKillException e) {

            SecKillExecution execution = new SecKillExecution(secKillId, SecKillStateEnum.REPEAT_KILL);
            return new SecKillResult<SecKillExecution>(true, execution);

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
            SecKillExecution execution = new SecKillExecution(secKillId, SecKillStateEnum.INNER_ERROR);
            return new SecKillResult<SecKillExecution>(true, execution);

        }

    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SecKillResult<Long> time(){
        Date now = new Date();
        return new SecKillResult<Long>(true, now.getTime());
    }

//    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
//    @ResponseBody
//    public String time(){
//        Date now = new Date();
//        return "index";
//    }

}
