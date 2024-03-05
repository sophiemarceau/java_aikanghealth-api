package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.his.api.common.MinioUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.exception.HisException;
import com.example.his.api.mis.controller.form.AddCheckupWaybillForm;
import com.example.his.api.mis.controller.form.CreateCheckupReportForm;
import com.example.his.api.mis.controller.form.SearchCheckupReportByPageForm;
import com.example.his.api.mis.service.CheckupReportService;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mis/checkup/report")
public class CheckupReportController {
    @Resource
    private CheckupReportService checkupReportService;
    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchCheckupReportByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = checkupReportService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/createReport")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:SELECT"}, mode = SaMode.OR)
    public R createReport(@RequestBody @Valid CreateCheckupReportForm form) {
        boolean bool = checkupReportService.createReport(form.getId());
        return R.ok().put("result", bool);
    }

    @GetMapping("/downloadReport")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:SELECT"}, mode = SaMode.OR)
    public void downloadReport(@RequestParam String filePath, @RequestParam String name, HttpServletResponse response) {
        if (StrUtil.isBlank(name)) {
            throw new HisException("name不能为空");
        } else if (!ReUtil.isMatch("^[\\u4e00-\\u9fa5]{2,10}$", name)) {
            throw new HisException("name内容不正确");
        }
        try (InputStream in = minioUtil.downloadFile(filePath);
             OutputStream out = response.getOutputStream();) {
            response.reset();
            response.setContentType("application/x-msdownloadoctet-stream;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + URLEncoder.encode(name + "的体检报告.docx", "UTF-8"));
            IOUtil.copyCompletely(in, out);
        } catch (Exception e) {
            throw new HisException("下载体检报告失败");
        }
    }

    @SneakyThrows
    @PostMapping("/identifyWaybill")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:UPDATE"}, mode = SaMode.OR)
    public R identifyWaybill(@RequestParam("file") MultipartFile file) {
        InputStream in = file.getInputStream();
        BufferedImage image = ImgUtil.read(in);
        String imgBase64 = "data:image/jpg;base64," + ImgUtil.toBase64(image, "jpg");
        in.close();
        HashMap map = checkupReportService.identifyWaybill(imgBase64);
        return R.ok().put("result", map);
    }

    @PostMapping("/addWaybill")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:UPDATE"}, mode = SaMode.OR)
    public R addWaybill(@RequestBody @Valid AddCheckupWaybillForm form) {
        Map param = BeanUtil.beanToMap(form);
        param.put("status", 3);
        boolean bool = checkupReportService.addWaybill(param);
        return R.ok().put("result", bool);
    }

    @PostMapping("/importWaybills")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:UPDATE"}, mode = SaMode.OR)
    public R importWaybills(@RequestParam("file") MultipartFile file) throws IOException {
        XSSFWorkbook workbook = checkupReportService.importWaybills(file);
        if (workbook != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            byte[] byteArray = out.toByteArray();
            String base64 = Base64.encode(byteArray);
            return R.ok().put("fileBase64", base64);
        }
        return R.ok();
    }
}
