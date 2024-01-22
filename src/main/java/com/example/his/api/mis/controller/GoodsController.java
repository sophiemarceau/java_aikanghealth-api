package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.example.his.api.common.MinioUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.db.pojo.GoodsEntity;
import com.example.his.api.exception.HisException;
import com.example.his.api.mis.controller.form.*;
import com.example.his.api.mis.service.GoodsService;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController("MisGoodsController")
@RequestMapping("/mis/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchGoodsByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = goodsService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/uploadImage")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R uploadImage(@Param("file") MultipartFile file) {
        String path = goodsService.uploadImage(file);
        return R.ok().put("result", path);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertGoodsForm form) {
        //pojo 对象中的check_1,check_2,check_3,check_4是String类型
//        FORM中这些都是ArrayList 为了避免form 对象转换成pojo对象 出现异常 所以要排除这两个变量
        GoodsEntity entity = BeanUtil.toBean(form, GoodsEntity.class, CopyOptions.create());
        String temp = null;
        if (form.getCheckup_1() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_1()).toString();
            entity.setCheckup_1(temp);
        }
        if (form.getCheckup_2() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_2()).toString();
            entity.setCheckup_2(temp);
        }
        if (form.getCheckup_3() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_3()).toString();
            entity.setCheckup_3(temp);
        }
        if (form.getCheckup_4() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_4()).toString();
            entity.setCheckup_4(temp);
        }
        if (form.getTag() != null) {
            temp = JSONUtil.parseArray(form.getTag()).toString();
            entity.setTag(temp);
        }
        int rows = goodsService.insert(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchGoodsByIdForm form) {
        HashMap map = goodsService.searchById(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateGoodsForm form) {
        GoodsEntity entity = BeanUtil.toBean(form, GoodsEntity.class, CopyOptions.create().setIgnoreProperties("checkup_1", "checkup_2", "checkup_3", "checkup_4", "tag"));
        String temp = null;
        if (form.getCheckup_1() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_1()).toString();
            entity.setCheckup_1(temp);
        }
        if (form.getCheckup_2() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_2()).toString();
            entity.setCheckup_2(temp);
        }
        if (form.getCheckup_3() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_3()).toString();
            entity.setCheckup_3(temp);
        }
        if (form.getCheckup_4() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_4()).toString();
            entity.setCheckup_4(temp);
        }
        if (form.getTag() != null) {
            temp = JSONUtil.parseArray(form.getTag()).toString();
            entity.setTag(temp);
        }
        int rows = goodsService.update(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/uploadCheckupExcel")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE", "GOODS:INSERT"}, mode = SaMode.OR)
    public R uploadCheckExcel(@Valid UploadCheckupExcelForm form, @Param("file") MultipartFile file) {
        goodsService.updateCheckup(form.getId(), file);
        return R.ok();
    }

    @GetMapping("/downloadCheckupExcel")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public void downloadCheckupExcel(@Valid DownloadCheckupExcelForm form, HttpServletResponse response) {
        //下载文件名称
        response.setHeader("Content-Disposition", "attachment;filename=" + form.getId() + ".xlsx");
        //MIME类型会让浏览器弹出下载对话框
        response.setContentType("application/x-download");
        String path = "/mis/goods/checkup/" + form.getId() + ".xlsx";
        try (
                //Minio存储文件的输入流
                InputStream in = minioUtil.downloadFile(path);
                BufferedInputStream bin = new BufferedInputStream(in);
                //对响应输出数据的输出流
                ServletOutputStream out = response.getOutputStream();
                BufferedOutputStream bout = new BufferedOutputStream(out);
        ) {
            //输入流中的数据拷贝到输出流
            IoUtil.copy(bin, bout);
        } catch (Exception e) {
            throw new HisException("文档下载失败");
        }
    }

    @PostMapping("/updateStatus")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R updateStatus(@RequestBody @Valid UpdateGoodsStatusForm form) {
        Map param = BeanUtil.beanToMap(form);
        boolean bool = goodsService.updateStatus(param);
        return R.ok().put("result", bool);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "GOODS:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteGoodByIdsForm form) {
        int rows = goodsService.deleteByIds(form.getIds());
        return R.ok().put("rows", rows);
    }
}


