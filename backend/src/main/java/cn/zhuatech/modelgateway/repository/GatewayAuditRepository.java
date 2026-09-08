/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.repository;import cn.zhuatech.modelgateway.model.GatewayAudit;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface GatewayAuditRepository extends JpaRepository<GatewayAudit,Long>{Optional<GatewayAudit>findByRequestId(String id);List<GatewayAudit>findTop100ByOrderByCreatedAtDesc();}
