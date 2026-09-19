/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.controller;import cn.zhuatech.modelgateway.common.ApiResponse;import cn.zhuatech.modelgateway.service.ModelFailoverPolicyService;import jakarta.validation.Valid;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController@RequestMapping("/api/model-gateway")public class ModelFailoverPolicyController{private final ModelFailoverPolicyService service;/**
                                                                                                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                 */
public ModelFailoverPolicyController(ModelFailoverPolicyService s){service=s;}/**
                                                                                                                                                                                                                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                               */
@PostMapping("/failover-policy")@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")public ApiResponse<ModelFailoverPolicyService.Result>decide(@Valid@RequestBody ModelFailoverPolicyService.Request r){return ApiResponse.ok("模型故障转移决策完成",service.decide(r));}}
