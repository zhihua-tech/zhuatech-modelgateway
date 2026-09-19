/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.controller;import cn.zhuatech.modelgateway.common.ApiResponse;import cn.zhuatech.modelgateway.model.GatewayAudit;import cn.zhuatech.modelgateway.service.ModelGatewayService;import jakarta.validation.Valid;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController@RequestMapping("/api/model-gateway")public class ModelGatewayController{private final ModelGatewayService service;/**
                                                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                   */
public ModelGatewayController(ModelGatewayService s){service=s;}/**
                                                                                                                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                   */
@PostMapping("/route")@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")public ApiResponse<ModelGatewayService.RouteResult>route(@Valid@RequestBody ModelGatewayService.RouteRequest r,Authentication a){return ApiResponse.ok("模型路由完成",service.route(r,a.getName()));}/**
                                                                                                                                                                                                                                                                                                                                                                                                                                                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                                                                                       */
@GetMapping("/audits")@PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")public ApiResponse<List<GatewayAudit>>audits(){return ApiResponse.ok("审计查询成功",service.audits());}}
