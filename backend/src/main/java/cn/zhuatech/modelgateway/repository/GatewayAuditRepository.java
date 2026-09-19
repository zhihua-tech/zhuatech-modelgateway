/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.repository;import cn.zhuatech.modelgateway.model.GatewayAudit;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface GatewayAuditRepository extends JpaRepository<GatewayAudit,Long>{/**
                                                                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                  */
Optional<GatewayAudit>findByRequestId(String id);/**
                                                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                   */
List<GatewayAudit>findTop100ByOrderByCreatedAtDesc();}
