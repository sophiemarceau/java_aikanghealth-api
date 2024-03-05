package com.example.his.api.schedule;

import com.example.his.api.async.CheckupWorkAsync;
import com.example.his.api.db.dao.CheckupReportDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Component
@Slf4j
public class CheckupReportSchedule {
    @Resource
    private CheckupReportDao checkupReportDao;
    @Resource
    private CheckupWorkAsync checkupWorkAsync;

    /**
     * 每日 凌晨1-5点 每20分钟，生成一批10天前的体检报告
     * if 体检结果数据并未录入结束，自动把该体检信息发送给相关领导负责人
     */
    @Scheduled(cron = "0 0,20,40 1,2,3,4,5 * * ?")
//    @Scheduled(cron = "0 0/1 * * * ? ")
    public void createReport() {
        //查询10天以前的体检记录，每次提取50条记录
        ArrayList<Integer> list = checkupReportDao.searchWillGenerateReport();
        if (list == null || list.size() == 0) {
            return;
        }
        //异步线程去生成体检报告
        list.forEach(one -> {
            checkupWorkAsync.createReport(one);
        });
        log.debug("对" + list.size() + "分体检生成报告");
    }
}
