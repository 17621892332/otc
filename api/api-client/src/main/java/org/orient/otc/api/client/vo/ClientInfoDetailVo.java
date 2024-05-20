package org.orient.otc.api.client.vo;

import lombok.Data;
import org.orient.otc.common.dictionary.annotion.Dictionary;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientInfoDetailVo {
    /**
     *  "ClientId": 1,
     *             "ClientName": "建发（上海）有限公司",
     *             "ClientNumber": "JFSH",
     *             "ClientType": "机构",
     *             "LicenseCode": "9131011573978005X5",
     *             "LicenseType": "营业执照",
     *             "CreditStartDate": "2022-06-15T00:00:00",
     *             "CreditDeadLine": "2023-06-14T00:00:00"
     */
    Integer id;
    /**
     * 客户名称
     */
    String name;
    /**
     * 客户代码
     */
    String code;
    /**
     * 客户类别
     */
    @Dictionary(type="ClientType")
    String clientType;
    /**
     * 证件代码
     */
    String licenseCode;
    /**
     * 证照类型
     */
    @Dictionary(type = "licenseType")
    String licenseType;

    LocalDateTime creditStartDate;

    LocalDateTime creditDeadLine;
    /**
     * 客户等级ID
     */
    Integer levelId;
    /**
     * 客户等级
     */
    String level;
    /**
     * 授信方向
     */
    String creditDirection;
    /**
     * 年度授信
     */
    Double creditYear;
    /**
     * 授信到期日
     */
    LocalDateTime creditDeadline;
    /**
     * 临时授信
     */
    Double creditTemp;
    /**
     * 临时授信到期日
     */
    LocalDateTime creditTempDeadline;
    /**
     * 授信额度
     */
    Double credit;
    /**
     * 客户类型
     */
    String type;
    /**
     * 证件号码
     */
    String identificationNumber;
    /**
     * 手机号码
     */
    String phone;
    /**
     * 地址信息
     */
    String address;
    /**
     * 电子邮箱
     */
    String email;
    /**
     * 资金余额
     */
    Double capitalbalance;
    /**
     * 是否需要追保
     */
    Integer pendingMarginCallPayment;
    /**
     * 营业部销售
     */
    String seller;
    /**
     * 营业部名称
     */Integer salesDepartmentId;
    /**
     * 商品类签署日期
     */
    LocalDateTime protocolSignDate;
    /**
     * 办公地址
     */
    String postalAddress;
    /**
     * 邮政编码
     */
    String postalCode;
    /**
     * 开户流程状态
     */
    String processStatus;
    /**
     * 开户时间
     */
    LocalDateTime processOptDate;
    /**
     * 流程进度，0：未提交，>0：进度顺序，-1：已拒绝，-2：已通过
     */
    Integer processOrderId;
    /**
     * 评估日期
     */
    LocalDateTime evaluateDate;
    /**
     * 评估有效期
     */
    @Dictionary(type = "EvaluateOfValidity")
    String evaluateOfValidity;
    /**
     * 评估到期日
     */
    LocalDateTime evaluateExpireDate;
    /**
     * 适当性客户分类
     */
    @Dictionary(type = "ProperClientClass")
    String properClientClass;
    /**
     * 个人客户风险承受能力等级
     */
    String endureLevel;
    /**
     * 客户经理
     */
    String customerManager;
    /**
     * 客户性质
     */
    Integer customerNature;
    /**
     * 注册资本
     */
    String registeredCapital;
    /**
     * 实际受益人
     */
    String actualBeneficiary;
    /**
     * 实际控制人
     */
    String actualController;
    /**
     * 注册地址
     */
    String registeredAddress;
    /**
     * 财务状况
     */
    String financialSituation;
    /**
     * 投资经验
     */
    @Dictionary(type = "InvestmentExperience")
    String investmentExperience;
    /**
     * 所属行业是否和投资品种有关
     */
    Integer isIndustryConnectVariety;
    /**
     * 交易目的
     */
    //@Dictionary(type = "TransactionTarget")
    String transactionTarget;
    /**
     * 交易目的
     */
    List<String> transactionTargetList;
    /**
     * 投资期限
     */
    //@Dictionary(type = "InvestmentTerm")
    Integer investmentTerm;
    /**
     * 是否存在实际控制关系
     */
    Integer isRealControl;
    /**
     * 交易种类
     */
    String derivativesInvestmentVarieties;
    /**
     * 资金来源
     */
    Integer fundsSource;
    /**
     * 可接受损失(%)
     */
    String acceptableLoss;
    /**
     * 风险偏好
     */
    @Dictionary(type = "RiskPreference")
    String riskPreference;
    /**
     * 期望收益
     */
    Integer expectedReturn;
    /**
     * 期望收益2
     */
    @Dictionary(type = "ExpectedReturn")
    String expectedReturn2;
    /**
     * 不良诚信记录
     */
    Integer badFaithRecord;
    /**
     * 是否评估
     */
    Integer isEvaluate;
    /**
     * 问卷得分
     */
    Integer questionnaireScore;
    /**
     * 适当性等级
     */
    Integer appropriatenessDegree;
    /**
     * 是否接受高风险等级服务
     */
    Integer isAcceptHighRiskService;
    /**
     * 适当性评估人
     */
    Integer appropriatenessAssessor;
    /**
     * 客户是否要求转化类型
     */
    Integer isRequireConversionTypes;
    /**
     * 评估结果是否变动
     */
    Integer isAssessmentResultChange;
    /**
     * 备注
     */
    String remark;
    /**
     * 变动原因及历史评估结果
     */
    String changeReasonAndEvaluationResults;
    /**
     * 失信行为记录
     */
    String disbeliefRecord;
    /**
     * 资信等级Id
     */
    Integer creditRatingId;
    /**
     * 客户经理Id
     */
    String customerManagerId;
    /**
     * 密码
     */
    String pwd;
    /**
     * 营业执照号有效期
     */
    LocalDateTime licenseCodeDate;
    /**
     * 经营范围
     */
    Integer businessScope;
    /**
     * 卖方费率差价
     */
    Double sellerRateSpread;
    /**
     * 卖方权利金交割日类型
     */
    Integer sellerRightDeliveryDayType;
    /**
     * 私募基金产品信息
     */
    String privateFundProductInformation;
    /**
     * 客户简称
     */
    String shortName;
    /**
     * 交易确认书编码长度
     */
    Integer contractNoLength;
    /**
     * 交易确认书子编码样式
     */
    String contractSubNoStyle;
    /**
     * 交易确认书当前编号
     */
    Integer currentContractNo;
    /**
     * 交易确认书版本
     */
    String contractVersion;
    /**
     * 该字段和客户登陆系统数据库systemusers表中的Guid做匹配
     */
    String guid;
    /**
     * 双录状态
     */
    Integer doubleRecordStatus;
    /**
     * 机构属性
     */
    @Dictionary(type = "InstitutionalAttributes")
    String institutionalAttributes;
    /**
     * 行业
     */
    String businessType;
    /**
     * 地域
     */
    String region;
    /**
     * 期权费授信（授信是否可以用来支付期权费）
     */
    Integer isTradeCredit;
    /**
     * 当前拒绝角色
     */
    Integer rejectOrderId;
    /**
     * 默认登录名
     */
    String defaultLoginName;
    /**
     * 投资者类型
     */
    Integer investorType;
    /**
     * 同业/非同业(0:同业;1:非同业)
     */
    //@Dictionary(type = "SamePeer")
    Integer samePeer;
    /**
     * 是否自动撤单（通道业务）
     */
    Integer isAutoReback;
    /**
     * 可接受风险服务
     */
    Integer riskServiceDegree;
    /**
     * 客户简称
     */
    String abbreviation;
    /**
     * 期权费授信(是否可以添加授信)
     */
    Integer isCreditOn;
    /**
     * 双向保证金
     */
    Integer hasTwoSideMargin;
    /**
     * 客户性质1
     */
    String customerNature1;
    /**
     * 客户性质2
     */
    String customerNature2;
    /**
     * 权益类签署日期
     */
    LocalDateTime rightProtocolSignDate;
    /**
     * 产品编号
     */
    String productNumber;
    /**
     * 协议签署版本
     */
    @Dictionary(type = "ProtocolSignVersion")
    String protocolSignVersion;
    /**
     * 管理人全称
     */
    String adminFullName;
    /**
     * 管理人登记编号
     */
    String adminRegisteredNum;
    /**
     * 交易资产(0: all 1: equity 2 : commodity)
     */
    Integer tradingInstType;
    /**
     * 客户父级ID
     */
    Integer parentId;
    /**
     * 追保方向
     */
    //@Dictionary(type = "MarginOptionType")
    Integer marginOptionType;
    /**
     * 如果该字段为 “是” ，则该客户确认书和结算单中，客户名称显示的是所属机构的名称
     */
    Integer isDocShowParentName;
    /**
     * 组织机构代码
     */
    String counterpartyCode;
    /**
     * 行业代码
     */
    String nfiCode;
    /**
     * 是否曾通过审批
     */
    Integer hadPass;
    /**
     * 准入规则（0:我方准入,1:对手准入,2:双方准入）
     */
    Integer accessRule;
    /**
     * 资金阈值
     */
    Double fundThreshold;
    /**
     * 方顿保证金模板选择
     */
    String ruleT0orT1;
    /**
     * 确认书模板
     */
    //@Dictionary(type = "ConfirmBookMode")
    String confirmBookMode;
    /**
     * 是否上市公司
     */
    Integer isListed;
    /**
     * 是否内部客户
     */
    Integer isInsided;
    /**
     * 扩展信息
     */
    String exJson;

    Integer boundSide;
    /**
     * 结算币种
     */
    String settlementCurrency;
    /**
     * 席位（对客户主体）
     */
    String seat;
    /**
     * 履约协议编号
     */
    String agreementBookNo;
    /**
     * 履约协议类型
     */
    Integer agreementBookType;
    /**
     * 清算机构
     */
    String clearingAgency;
    /**
     * 主协议编号
     */
    String mainProtocolCode;
    /**
     * 补充协议编号
     */
    String supProtocolCode;
    /**
     * 补充协议二签署日期
     */
    LocalDateTime supProtocolDate;
    /**
     * 银行信用评级
     */
    String bankCreditRating;
    /**
     * 互换对冲方式: 0：手动对冲 1：自动对冲
     */
    Integer swapHedgingType;
    /**
     * 交易分组id
     */
    Integer tradeGroupId;
    /**
     * 是否小微客户
     */
    Integer isSmallAndMicroEnterprises;
    /**
     * 结算报告批量发送是否使用追保模板（0 否 1 是）
     */
    Integer isSendRecovery;
    /**
     * 全球法人识别编码
     */
    String leiCode;
    /**
     * 是否中央对手方清算
     */
    String isCentralClearing;
    /**
     * 中央清算平台名称
     */
    String centralClearingPaltform;
    /**
     * 交易平台
     */
    @Dictionary(type = "TradingPaltform")
    String tradingPaltform;
    /**
     * 追保系数
     */
    Double marginRate;
    /**
     * 可取系数
     */
    Double desirableRate;
    /**
     * 最小转账金额
     */
    Double minimumTransferAmount;

    LocalDateTime licenseValidTime;

    MetaDicVo metaDic;
    /**
     * 实际受益人证件有效期
     */
    LocalDateTime actualBeneficiaryLicenseCodeDate;
    /**
     * 报送主体
     */
    String reportName;

}
