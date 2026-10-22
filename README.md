# ZhuaTech Model Gateway｜企业 AI 模型统一网关

2026-10-23 功能更新：**上下文窗口预算编排**。先保留必需消息和输出预算，再按优先级纳入可选上下文；明确返回舍弃项和剩余 token。使用输入的 token 估算，不声称实现模型 tokenizer。 [接口、操作与边界说明](docs/AI_PRACTICAL_UPGRADE_20261023.md)。

[简体中文](README.md) | [English](README.en.md)

ZhuaTech Model Gateway 是 **[知华科技（上海如静知华信息科技有限公司）](https://www.zhuatech.cn/)** 面向多模型接入、成本治理和可靠性建设推出的社区源码项目。

## 不是“模型配置页面”，而是可工作的路由链

- 按能力标签、健康状态、数据驻留地、剩余 Token 配额和限流状态过滤模型。
- 综合质量、P95 延迟和预估费用计算路由得分。
- 返回首选模型及降级链；预算或配额不足时明确阻断。
- `execute=true` 时通过 OpenAI 兼容协议真实调用选中模型。
- 调用端点必须为 HTTPS 且命中 `MODEL_ALLOWED_HOSTS`，API Key 只从服务端环境读取。
- 请求 ID 幂等、输入仅保存哈希摘要、调用结果和操作者进入 MySQL 审计。
- 管理员/运营员执行路由，审计员只读审计记录。

## 核心接口

- `POST /api/model-gateway/route`：执行路由；可选择真实调用。
- `GET /api/model-gateway/audits`：读取最近 100 条调用审计。

## 快速运行

```bash
cp .env.example .env
# 编辑模型主机白名单与服务端密钥
docker compose up --build
```

前端地址：`http://localhost:8092`。管理台默认只执行路由演练；勾选真实调用后才访问模型提供商。

## 许可

本工程仅限个人非商业学习、研究和技术交流；企业使用、生产部署、SaaS、外包交付、咨询实施等商用行为必须取得上海如静知华信息科技有限公司书面授权，详见 [LICENSE](LICENSE)。商业模型接入与私有化部署请联系[知华科技](https://www.zhuatech.cn/)。

## 有边界的模型故障转移

新增 `POST /api/model-gateway/failover-policy`，区分超时、限流、服务故障、内容安全拒绝和认证错误；结合幂等性、流式输出状态、重试次数、数据驻留、能力、健康和预算，输出 `RETRY_CURRENT / FAILOVER / MANUAL / BLOCKED`，禁止通过切换模型绕过安全拒绝。

## 微信咨询

商业授权、私有化部署或深度定制开发，可扫描下方二维码添加微信 `zhuatech` 或 `zhuatech2` 咨询。

| 微信 zhuatech | 微信 zhuatech2 |
| --- | --- |
| ![知华科技微信咨询 zhuatech](docs/images/zhuatech-wechat-consulting.png) | ![知华科技微信咨询 zhuatech2](docs/images/zhuatech-wechat-consulting-2.png) |
