package org.orient.otc.api.system.vo;

import lombok.Data;
import org.orient.otc.common.core.util.FieldAlias;

import java.time.LocalDate;

/**
 * 客户列表VO
 */
@Data
public class ClientDataChangeDetailVO {
    private Integer id;
    @FieldAlias(value = "客户编号")
    private String code;

    @FieldAlias(value = "客户名称")
    private String name;

    @FieldAlias(value = "客户简称")
    private String shortName;

    @FieldAlias(value = "客户等级id")
    private Integer levelId;
    /**
     * 客户等级
     */
    @FieldAlias("客户等级")
    private String level;
    /**
     * 授信方向
     */
    @FieldAlias("授信方向")
    private String creditDirection;
    /**
     * 年度授信
     */
    @FieldAlias("年度授信")
    private Double creditYear;
    /**
     * 授信到期日
     */
    @FieldAlias("授信到期日")
    private LocalDate creditDeadline;
    /**
     * 临时授信
     */
    @FieldAlias("临时授信")
    private Double creditTemp;
    /**
     * 临时授信到期日
     */
    @FieldAlias("临时授信到期日")
    private LocalDate creditTempDeadline;
    /**
     * 授信额度
     */
    @FieldAlias("授信额度")
    private Double credit;
    /**
     * 客户类型
     */
    @FieldAlias("客户类型")
    private String type;
    /**
     * 证件号码
     */
    @FieldAlias("证件号码")
    private String identificationNumber;
    /**
     * 手机号码
     */
    @FieldAlias("手机号码")
    private String phone;
    /**
     * 地址信息
     */
    @FieldAlias("地址信息")
    private String address;
    /**
     * 电子邮箱
     */
    @FieldAlias("电子邮箱")
    private String email;
    /**
     * 资金余额
     */
    @FieldAlias("资金余额")
    private Double capitalBalance;
    /**
     * 是否需要追保
     */
    @FieldAlias("是否需要追保")
    private Integer pendingMarginCallPayment;
    /**
     * 销售员
     */
    @FieldAlias("销售员")
    private String seller;
    /**
     * 销售部门ID
     */
    @FieldAlias("销售部门ID")
    private Integer salesDepartmentId;
    /**
     * 商品类签署日期
     */
    @FieldAlias("商品类签署日期")
    private LocalDate protocolSignDate;
    /**
     * 办公地址
     */
    @FieldAlias("办公地址")
    private String postalAddress;
    /**
     * 邮政编码
     */
    @FieldAlias("邮政编码")
    private String postalCode;
    /**
     * 证照编号
     */
    @FieldAlias("证照编号")
    private String licenseCode;
    /**
     * 开户流程状态
     */
    @FieldAlias("开户流程状态")
    private String processStatus;
    /**
     * 开户时间
     */
    @FieldAlias("开户时间")
    private LocalDate processOptDate;
    /**
     * 流程进度，0：未提交，>0：进度顺序，-1：已拒绝，-2：已通过
     */
    @FieldAlias("流程进度，0：未提交，>0：进度顺序，-1：已拒绝，-2：已通过")
    private Integer processOrderId;
    /**
     * 评估日期
     */
    @FieldAlias("评估日期")
    private LocalDate evaluateDate;
    /**
     * 评估有效期
     */
    @FieldAlias("评估有效期")
    private String evaluateOfValidity;
    /**
     * 评估到期日
     */
    @FieldAlias("评估到期日")
    private LocalDate evaluateExpireDate;
    /**
     * 适当性客户分类
     */
    @FieldAlias("适当性客户分类")
    private String properClientClass;
    /**
     * 个人客户风险承受能力等级
     */
    @FieldAlias("个人客户风险承受能力等级")
    private String endureLevel;
    /**
     * 客户经理
     */
    @FieldAlias("客户经理")
    private String customerManager;
    /**
     * 客户性质
     */
    @FieldAlias("客户性质")
    private Integer customerNature;
    /**
     * 注册资本
     */
    @FieldAlias("注册资本")
    private String registeredCapital;
    /**
     * 实际受益人
     */
    @FieldAlias("实际受益人")
    private String actualBeneficiary;
    /**
     * 实际控制人
     */
    @FieldAlias("实际控制人")
    private String actualController;
    /**
     * 注册地址
     */
    @FieldAlias("注册地址")
    private String registeredAddress;
    /**
     * 财务状况
     */
    @FieldAlias("财务状况")
    private String financialSituation;
    /**
     * 投资经验
     */
    @FieldAlias("投资经验")
    private String investmentExperience;
    /**
     * 所属行业是否和投资品种有关
     */
    @FieldAlias("所属行业是否和投资品种有关")
    private Integer isIndustryConnectVariety;
    /**
     * 交易目标
     */
    @FieldAlias("交易目标")
    private String transactionTarget;
    /**
     * 投资期限
     */
    @FieldAlias("投资期限")
    private Integer investmentTerm;
    /**
     * 是否存在实际控制关系
     */
    @FieldAlias("是否存在实际控制关系")
    private Integer isRealControl;
    /**
     * 衍生品投资品种
     */
    @FieldAlias("衍生品投资品种")
    private String derivativesInvestmentVarieties;
    /**
     * 资金来源
     */
    @FieldAlias("资金来源")
    private Integer fundsSource;
    /**
     * 可接受损失(%)
     */
    @FieldAlias("可接受损失(%)")
    private String acceptableLoss;
    /**
     * 风险偏好
     */
    @FieldAlias("风险偏好")
    private String riskPreference;
    /**
     * 期望收益
     */
    @FieldAlias("期望收益")
    private Integer expectedReturn;
    /**
     * 期望收益
     */
    @FieldAlias("期望收益2")
    private String expectedReturn2;
    /**
     * 期权费授信（授信是否可以用来支付期权费）
     */
    @FieldAlias("期权费授信（授信是否可以用来支付期权费）")
    private Integer isTradeCredit;
    /**
     * 当前拒绝角色
     */
    @FieldAlias("当前拒绝角色")
    private Integer rejectOrderId;
    /**
     * 默认登录名
     */
    @FieldAlias("默认登录名")
    private String defaultLoginName;
    /**
     * 投资者类型
     */
    @FieldAlias("投资者类型")
    private Integer investorType;
    /**
     * 同业/非同业(0:同业;1:非同业)
     */
    @FieldAlias("同业/非同业(0:同业;1:非同业)")
    private Integer samePeer;
    /**
     * 是否自动撤单（通道业务）
     */
    @FieldAlias("是否自动撤单（通道业务）")
    private Integer isAutoReback;
    /**
     * 可接受风险服务
     */
    @FieldAlias("可接受风险服务")
    private Integer riskServiceDegree;
    /**
     * 客户缩写
     */
    @FieldAlias("客户缩写")
    private String abbreviation;
    /**
     * 期权费授信(是否可以添加授信)
     */
    @FieldAlias("期权费授信(是否可以添加授信)")
    private Integer isCreditOn;
    /**
     * 双向保证金
     */
    @FieldAlias("双向保证金")
    private Integer hasTwoSideMargin;
    /**
     * 客户性质1
     */
    @FieldAlias("客户性质1")
    private String customerNature1;
    /**
     * 客户性质2
     */
    @FieldAlias("客户性质2")
    private String customerNature2;
    /**
     * 权益类签署日期
     */
    @FieldAlias("权益类签署日期")
    private LocalDate rightProtocolSignDate;
    /**
     * 产品编号
     */
    @FieldAlias("产品编号")
    private String productNumber;
    /**
     * 协议签署版本
     */
    @FieldAlias("协议签署版本")
    private String protocolSignVersion;
    /**
     * 管理人全称
     */
    @FieldAlias("管理人全称")
    private String adminFullName;
    /**
     * 管理人登记编号
     */
    @FieldAlias("管理人登记编号")
    private String adminRegisteredNum;
    /**
     * 交易资产(0: all 1: equity 2 : commodity)
     */
    @FieldAlias("交易资产(0: all 1: equity 2 : commodity)")
    private Integer tradingInstType;
    /**
     * 客户父级ID
     */
    @FieldAlias("客户父级ID")
    private Integer parentId;
    /**
     * 追保方向
     */
    @FieldAlias("追保方向")
    private Integer marginOptionType;
    /**
     * 如果该字段为 “是” ，则该客户确认书和结算单中，客户名称显示的是所属机构的名称
     */
    @FieldAlias("如果该字段为 “是” ，则该客户确认书和结算单中，客户名称显示的是所属机构的名称")
    private Integer isDocShowParentName;
    /**
     * 组织机构代码
     */
    @FieldAlias("组织机构代码")
    private String counterpartyCode;
    /**
     * 行业代码
     */
    @FieldAlias("行业代码")
    private String nfiCode;
    /**
     * 是否曾通过审批
     */
    @FieldAlias("是否曾通过审批")
    private Integer hadPass;
    /**
     * 准入规则（0:我方准入,1:对手准入,2:双方准入）
     */
    @FieldAlias("准入规则（0:我方准入,1:对手准入,2:双方准入）")
    private Integer accessRule;
    /**
     * 资金阈值
     */
    @FieldAlias("资金阈值")
    private Double fundThreshold;
    /**
     * 方顿保证金模板选择
     */
    @FieldAlias("方顿保证金模板选择")
    private String ruleT0orT1;
    /**
     * 确认书模板
     */
    @FieldAlias("确认书模板")
    private String confirmBookMode;
    /**
     * 是否上市公司
     */
    @FieldAlias("是否上市公司")
    private Integer isListed;
    /**
     * 是否内部客户
     */
    @FieldAlias("是否内部客户")
    private Integer isInsided;
    /**
     * 扩展信息
     */
    @FieldAlias("扩展信息")
    private String exJson;
    /**
     *
     */
    @FieldAlias("")
    private Integer boundSide;
    /**
     * 结算币种
     */
    @FieldAlias("结算币种")
    private String settlementCurrency;
    /**
     * 席位（对客户主体）
     */
    @FieldAlias("席位（对客户主体）")
    private String seat;
    /**
     * 履约协议编号
     */
    @FieldAlias("履约协议编号")
    private String agreementBookNo;
    /**
     * 履约协议类型
     */
    @FieldAlias("履约协议类型")
    private Integer agreementBookType;
    /**
     * 清算机构
     */
    @FieldAlias("清算机构")
    private String clearingAgency;
    /**
     * 主协议编号
     */
    @FieldAlias("主协议编号")
    private String mainProtocolCode;
    /**
     * 补充协议编号
     */
    @FieldAlias("补充协议编号")
    private String supProtocolCode;
    /**
     * 补充协议二签署日期
     */
    @FieldAlias("补充协议二签署日期")
    private LocalDate supProtocolDate;
    /**
     * 银行信用评级
     */
    @FieldAlias("银行信用评级")
    private String bankCreditRating;
    /**
     * 互换对冲方式: 0：手动对冲 1：自动对冲
     */
    @FieldAlias("互换对冲方式: 0：手动对冲 1：自动对冲")
    private Integer swapHedgingType;
    /**
     * 交易分组id
     */
    @FieldAlias("交易分组id")
    private Integer tradeGroupId;
    /**
     * 是否小微客户
     */
    @FieldAlias("是否小微客户")
    private Integer isSmallAndMicroEnterprises;
    /**
     * 结算报告批量发送是否使用追保模板（0 否 1 是）
     */
    @FieldAlias("结算报告批量发送是否使用追保模板（0 否 1 是）")
    private Integer isSendRecovery;
    /**
     * 全球法人识别编码
     */
    @FieldAlias("全球法人识别编码")
    private String leiCode;
    /**
     * 是否中央对手方清算
     */
    @FieldAlias("是否中央对手方清算")
    private String isCentralClearing;
    /**
     * 中央清算平台名称
     */
    @FieldAlias("中央清算平台名称")
    private String centralClearingPaltform;
    /**
     * 交易平台
     */
    @FieldAlias("交易平台")
    private String tradingPaltform;
    /**
     * 追保系数
     */
    @FieldAlias("追保系数")
    private Double marginRate;
    /**
     * 可取系数
     */
    @FieldAlias("可取系数")
    private Double desirableRate;
    /**
     * 最小转账金额
     */
    @FieldAlias("最小转账金额")
    private Double minimumTransferAmount;
    /**
     * 客户类型
     */
    @FieldAlias("客户类型")
    private String clientType;
    /**
     * 机构属性
     */
    @FieldAlias("机构属性")
    private String institutionalAttributes;
    /**
     * 行业
     */
    @FieldAlias("行业")
    private String businessType;
    /**
     * 地域
     */
    @FieldAlias("地域")
    private String region;
    /**
     * 证件类型
     */
    @FieldAlias("证件类型")
    private String licenseType;
    /**
     * 不良诚信记录
     */
    @FieldAlias("不良诚信记录")
    private Integer badFaithRecord;
    /**
     * 备注
     */
    @FieldAlias("备注")
    private String remark;
    /**
     * 企业营业执照有效期
     */
    @FieldAlias("企业营业执照有效期")
    private LocalDate licenseValidTime;
    /**
     * 变动原因及历史
     */
    @FieldAlias("变动原因及历史")
    private String changeReasonAndEvaluationResults;
    /**
     * 失信行为记录
     */
    @FieldAlias(value = "失信行为记录")
    private String disbeliefRecord;
    /**
     * 资信等级Id
     */
    @FieldAlias(value = "资信等级Id")
    private Integer creditRatingId;
    /**
     * 客户经理Id
     */
    @FieldAlias(value = "客户经理Id")
    private String customerManagerId;
    /**
     * 密码
     */
    @FieldAlias(value = "密码")
    private String pwd;
    /**
     * 营业执照号有效期
     */
    @FieldAlias(value = "营业执照号有效期")
    private LocalDate licenseCodeDate;
    /**
     * 经营范围
     */
    @FieldAlias(value = "经营范围")
    private Integer businessScope;
    /**
     * 卖方费率差价
     */
    @FieldAlias(value = "卖方费率差价")
    private Double sellerRateSpread;
    /**
     * 卖方权利金交割日类型
     */
    @FieldAlias(value = "卖方权利金交割日类型")
    private Integer sellerRightDeliveryDayType;
    /**
     * 私募基金产品信息
     */
    @FieldAlias(value = "私募基金产品信息")
    private String privateFundProductInformation;
    /**
     * 交易确认书编码长度
     */
    @FieldAlias(value = "交易确认书编码长度")
    private Integer contractNoLength;
    /**
     * 交易确认书子编码样式
     */
    @FieldAlias(value = "交易确认书子编码样式")
    private String contractSubNoStyle;
    /**
     * 交易确认书当前编号
     */
    @FieldAlias(value = "交易确认书当前编号")
    private Integer currentContractNo;
    /**
     * 交易确认书版本
     */
    @FieldAlias(value = "交易确认书版本")
    private String contractVersion;
    /**
     * 该字段和客户登陆系统数据库systemusers表中的Guid做匹配
     */
    @FieldAlias(value = "该字段和客户登陆系统数据库systemusers表中的Guid做匹配")
    private String guid;
    /**
     * 双录状态
     */
    @FieldAlias(value = "双录状态")
    private Integer doubleRecordStatus;
    /**
     * 是否评估
     */
    @FieldAlias(value = "是否评估")
    private Integer isEvaluate;
    /**
     * 问卷得分
     */
    @FieldAlias(value = "问卷得分")
    private Integer questionnaireScore;
    /**
     * 适当性等级
     */
    @FieldAlias(value = "适当性等级")
    private Integer appropriatenessDegree;
    /**
     * 是否接受高风险等级服务
     */
    @FieldAlias(value = "是否接受高风险等级服务")
    private Integer isAcceptHighRiskService;
    /**
     * 适当性评估人
     */
    @FieldAlias(value = "适当性评估人")
    private Integer appropriatenessAssessor;
    /**
     * 客户是否要求转化类型
     */
    @FieldAlias(value = "客户是否要求转化类型")
    private Integer isRequireConversionTypes;
    /**
     * 评估结果是否变动
     */
    @FieldAlias(value = "评估结果是否变动")
    private Integer isAssessmentResultChange;
    /**
     * 实际受益人证件有效期
     */
    @FieldAlias(value = "实际受益人证件有效期")
    private LocalDate actualBeneficiaryLicenseCodeDate;
    /**
     * 报送主体
     */
    @FieldAlias(value = "报送主体")
    private String reportName;
}
