/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.service;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.stereotype.Service;import java.math.BigDecimal;import java.util.*;
/**
 * 对模型调用失败执行有边界的重试、故障转移或人工接管。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class ModelFailoverPolicyService{
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public Result decide(Request r){List<String>excluded=new ArrayList<>(),controls=new ArrayList<>();
  if(r.failureType()==FailureType.AUTH_ERROR||r.failureType()==FailureType.CONTENT_REJECTED)return new Result(Decision.BLOCKED,"",List.of(),List.of("认证错误或内容安全拒绝不得通过切换模型绕过"));
  if(!r.idempotentRequest()||r.streamingOutputStarted())return new Result(Decision.MANUAL,"",List.of(),List.of("请求可能已产生副作用或部分输出，转人工确认后续动作"));
  if(r.retryCount()<r.maxRetries()&&(r.failureType()==FailureType.TIMEOUT||r.failureType()==FailureType.SERVER_ERROR)){controls.add("按指数退避重试当前模型并复用请求幂等键");return new Result(Decision.RETRY_CURRENT,r.currentModel(),List.of(),controls);}
  List<Candidate>eligible=new ArrayList<>();for(Candidate c:r.candidates()){List<String>miss=new ArrayList<>();if(!c.healthy())miss.add("不健康");if(!c.region().equals(r.requiredRegion()))miss.add("数据驻留地域不符");if(!c.capabilities().contains(r.requiredCapability()))miss.add("能力不匹配");if(c.estimatedCost().compareTo(r.maxFallbackCost())>0)miss.add("超过故障转移预算");if(miss.isEmpty()&&!c.modelName().equals(r.currentModel()))eligible.add(c);else if(!miss.isEmpty())excluded.add(c.modelName()+":"+String.join("/",miss));}
  eligible.sort(Comparator.comparing(Candidate::estimatedCost).thenComparing(Comparator.comparing(Candidate::qualityScore).reversed()));
  if(eligible.isEmpty())return new Result(Decision.BLOCKED,"",List.copyOf(excluded),List.of("没有满足地域、能力、健康和预算约束的备用模型"));
  Candidate chosen=eligible.getFirst();controls.add("切换到备用模型并沿用请求 ID、租户策略和审计上下文");controls.add("记录原模型故障类型与备用模型响应质量");return new Result(Decision.FAILOVER,chosen.modelName(),List.copyOf(excluded),List.copyOf(controls));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Request(@NotBlank String requestId,@NotBlank String currentModel,@NotNull FailureType failureType,@Min(0)int retryCount,@Min(0)@Max(10)int maxRetries,boolean idempotentRequest,boolean streamingOutputStarted,@NotBlank String requiredRegion,@NotBlank String requiredCapability,@DecimalMin("0")BigDecimal maxFallbackCost,@NotEmpty List<@Valid Candidate>candidates){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Candidate(@NotBlank String modelName,@NotBlank String region,@NotEmpty Set<@NotBlank String>capabilities,boolean healthy,@DecimalMin("0")BigDecimal estimatedCost,@DecimalMin("0")@DecimalMax("1")BigDecimal qualityScore){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Result(Decision decision,String selectedModel,List<String>excludedCandidates,List<String>controls){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public enum FailureType{TIMEOUT,RATE_LIMIT,SERVER_ERROR,CONTENT_REJECTED,AUTH_ERROR}/**
                                                                                      * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                      */
public enum Decision{RETRY_CURRENT,FAILOVER,MANUAL,BLOCKED}
}
