/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.gwt.web.ui.asset.client;

/**
 * @author <a href="mailto:MarkusNeumannMarkus@gmail.com">Markus Neumann</a>
 *         Basic static string i18n mechanism by GWT. Just add:
 *         DefaultStringValue("English default String")
 *         Key("Key to map value to the translated property files") String
 *         myI18nString() method to get the i18n string
 */
public interface AssetPageConstants extends com.google.gwt.i18n.client.Constants {

    @DefaultStringValue("附属硬件设备")
    @Key("additionalhardware")
    String additionalhardware();

    @DefaultStringValue("附属硬件设备")
    @Key("additionalhardwareHelp")
    String additionalhardwareHelp();

    @DefaultStringValue("地址 1")
    @Key("address1")
    String address1();

    @DefaultStringValue("设备所处地址，用于技术人员派遣")
    @Key("address1Help")
    String address1Help();

    @DefaultStringValue("经度")
    @Key("longitude")
    String longitude();

    @DefaultStringValue("经度")
    @Key("longitudeHelp")
    String longitudeHelp();

    @DefaultStringValue("纬度")
    @Key("latitude")
    String latitude();

    @DefaultStringValue("纬度")
    @Key("latitudeHelp")
    String latitudeHelp();

    @DefaultStringValue("地址 2")
    @Key("address2")
    String address2();

    @DefaultStringValue("地址（续）")
    @Key("address2Help")
    String address2Help();

    @DefaultStringValue("管理员")
    @Key("admin")
    String admin();

    @DefaultStringValue("所属区域管理员联系方式")
    @Key("adminHelp")
    String adminHelp();

    @DefaultStringValue("资产编号")
    @Key("assetNumber")
    String assetNumber();

    @DefaultStringValue("若因库存管理而建立资产标签后，该字段需相应启用")
    @Key("assetNumberHelp")
    String assetNumberHelp();

    @DefaultStringValue("保存失败：1个或多个字段存在无效输入，请更正后再次尝试保存")
    @Key("assetPageNotValidDontSave")
    String assetPageNotValidDontSave();

    /* Authentication */
    @DefaultStringValue("身份验证")
    @Key("authenticationHeader")
    String authenticationHeader();

    @DefaultStringValue("自动启用")
    @Key("autoEnable")
    String autoEnable();

    @DefaultStringValue("当通过验证的用户登录后该节点后，自动将其模式设为‘启用’")
    @Key("autoEnableHelp")
    String autoEnableHelp();

    @DefaultStringValue("建筑")
    @Key("building")
    String building();

    @DefaultStringValue("大楼、中心、机房等")
    @Key("buildingHelp")
    String buildingHelp();

    @DefaultStringValue("类别")
    @Key("category")
    String category();

    @DefaultStringValue("设备类别（例如WAN路由器，防火墙等)")
    @Key("categoryHelp")
    String categoryHelp();

    @DefaultStringValue("线路编号")
    @Key("circuitId")
    String circuitId();

    @DefaultStringValue("线路编号，一般由ISP网络供应商提供")
    @Key("circuitIdHelp")
    String circuitIdHelp();

    @DefaultStringValue("城市")
    @Key("city")
    String city();

    @DefaultStringValue("城市")
    @Key("cityHelp")
    String cityHelp();

    @DefaultStringValue("备注")
    @Key("comment")
    String comment();

    @DefaultStringValue("备注及说明")
    @Key("commentHelp")
    String commentHelp();

    /* Comments */
    @DefaultStringValue("备注")
    @Key("commentsHeader")
    String commentsHeader();

    /* Configuration Categories */
    @DefaultStringValue("配置类别")
    @Key("configurationCatHeader")
    String configurationCatHeader();

    @DefaultStringValue("连接")
    @Key("connection")
    String connection();

    @DefaultStringValue("连接")
    @Key("connectionHelp")
    String connectionHelp();

    @DefaultStringValue("合同失效期")
    @Key("contractExpires")
    String contractExpires();

    @DefaultStringValue("运维合同失效的日期")
    @Key("contractExpiresHelp")
    String contractExpiresHelp();

    @DefaultStringValue("CPU")
    @Key("cpu")
    String cpu();

    @DefaultStringValue("CPU类型")
    @Key("cpuHelp")
    String cpuHelp();

    @DefaultStringValue("客户")
    @Key("custom")
    String custom();

    @DefaultStringValue("客户合同")
    @Key("customerContract")
    String customerContract();

    @DefaultStringValue("客户合同失效期")
    @Key("customerContractExp")
    String customerContractExp();

    @DefaultStringValue("客户合同失效期")
    @Key("customerContractExpHelp")
    String customerContractExpHelp();

    @DefaultStringValue("客户合同")
    @Key("customerContractHelp")
    String customerContractHelp();

    /* Customer */
    @DefaultStringValue("客户")
    @Key("customerHeader")
    String customerHeader();

    @DefaultStringValue("客户邮件")
    @Key("customerMail")
    String customerMail();

    @DefaultStringValue("客户邮件")
    @Key("customerMailHelp")
    String customerMailHelp();

    @DefaultStringValue("客户名称")
    @Key("customerName")
    String customerName();

    @DefaultStringValue("客户名称")
    @Key("customerNameHelp")
    String customerNameHelp();

    @DefaultStringValue("客户编号")
    @Key("customerNumber")
    String customerNumber();

    @DefaultStringValue("客户编号")
    @Key("customerNumberHelp")
    String customerNumberHelp();

    @DefaultStringValue("客户电话")
    @Key("customerPhone")
    String customerPhone();

    @DefaultStringValue("客户电话")
    @Key("customerPhoneHelp")
    String customerPhoneHelp();

    /* Custom */
    @DefaultStringValue("客户")
    @Key("customHeader")
    String customHeader();

    @DefaultStringValue("客户")
    @Key("customHelp")
    String customHelp();

    @DefaultStringValue("安装日期")
    @Key("dateInstalled")
    String dateInstalled();

    @DefaultStringValue("设备安装日期")
    @Key("dateInstalledHelp")
    String dateInstalledHelp();

    @DefaultStringValue("部门")
    @Key("department")
    String department();

    @DefaultStringValue("部门")
    @Key("departmentHelp")
    String departmentHelp();

    @DefaultStringValue("设备描述")
    @Key("description")
    String description();

    @DefaultStringValue("设备描述，如用途、功能等")
    @Key("descriptionHelp")
    String descriptionHelp();

    @DefaultStringValue("显示类别")
    @Key("displayCategory")
    String displayCat();

    @DefaultStringValue("显示类别")
    @Key("displayCategoryHelp")
    String displayCatHelp();

    @DefaultStringValue("科室")
    @Key("division")
    String division();

    @DefaultStringValue("科室")
    @Key("divisionHelp")
    String divisionHelp();

    @DefaultStringValue("启用密码")
    @Key("enablePassword")
    String enablePassword();

    @DefaultStringValue("启用密码：仅当‘自动启用’未设置为‘A’时可用")
    @Key("enablePasswordHelp")
    String enablePasswordHelp();

    @DefaultStringValue("获取资产数据失败，节点编号：")
    @Key("errorFatchingAssetData")
    String errorFatchingAssetData();

    @DefaultStringValue("获取资产建议数据失败，节点编号：")
    @Key("errorFetchingAssetSuggData")
    String errorFetchingAssetSuggData();

    @DefaultStringValue("保存资产数据失败，节点编号：")
    @Key("errorSavingAssetData")
    String errorSavingAssetData();

    @DefaultStringValue("传真")
    @Key("fax")
    String fax();

    @DefaultStringValue("传真")
    @Key("faxHelp")
    String faxHelp();

    @DefaultStringValue("楼层")
    @Key("floor")
    String floor();

    @DefaultStringValue("楼层")
    @Key("floorHelp")
    String floorHelp();

    /* Hardware */
    @DefaultStringValue("硬件")
    @Key("hardwareHeader")
    String hardwareHeader();

    @DefaultStringValue("硬盘")
    @Key("hdd")
    String hdd();

    @DefaultStringValue("硬盘")
    @Key("hddHelp")
    String hddHelp();

    /* Identification */
    @DefaultStringValue("身份认证")
    @Key("identificationHeader")
    String identificationHeader();

    @DefaultStringValue("资产信息，节点: ")
    @Key("infoAsset")
    String infoAsset();

    @DefaultStringValue("资产信息加载中，节点: ")
    @Key("infoAssetLoging")
    String infoAssetLoging();

    @DefaultStringValue("资产信息重置中，节点: ")
    @Key("infoAssetRestting")
    String infoAssetRestting();

    @DefaultStringValue("资产信息已保存，节点: ")
    @Key("infoAssetSaved")
    String infoAssetSaved();

    @DefaultStringValue("资产信息保存中，节点: ")
    @Key("infoAssetSaving")
    String infoAssetSaving();

    @DefaultStringValue("电源")
    @Key("inputpower")
    String inputpower();

    @DefaultStringValue("电源类型")
    @Key("inputpowerHelp")
    String inputpowerHelp();

    @DefaultStringValue("最近修改：")
    @Key("lastModified")
    String lastModified();

    @DefaultStringValue("租赁")
    @Key("lease")
    String lease();

    @DefaultStringValue("租赁失效期")
    @Key("leaseExpires")
    String leaseExpires();

    @DefaultStringValue("租赁失效期")
    @Key("leaseExpiresHelp")
    String leaseExpiresHelp();

    @DefaultStringValue("租赁信息")
    @Key("leaseHelp")
    String leaseHelp();

    @DefaultStringValue("已修改")
    @Key("legendGreen")
    String legendGreen();

    @DefaultStringValue("已保存")
    @Key("legendGrey")
    String legendGrey();

    @DefaultStringValue("图例")
    @Key("legendHeadline")
    String legendHeadline();

    @DefaultStringValue("已修改但出错，无法保存")
    @Key("legendRed")
    String legendRed();

    @DefaultStringValue("已修改但有警告，可能可以保存")
    @Key("legendYellow")
    String legendYellow();

    /* Location */
    @DefaultStringValue("地址")
    @Key("locationHeader")
    String locationHeader();

    @DefaultStringValue("运维合同")
    @Key("maintContract")
    String maintContract();

    @DefaultStringValue("运维合同编号")
    @Key("maintContractHelp")
    String maintContractHelp();

    @DefaultStringValue("运维电话")
    @Key("maintPhone")
    String maintPhone();

    @DefaultStringValue("运维电话")
    @Key("maintPhoneHelp")
    String maintPhoneHelp();

    @DefaultStringValue("生产商")
    @Key("manufacturer")
    String manufacturer();

    @DefaultStringValue("生产商")
    @Key("manufacturerHelp")
    String manufacturerHelp();

    @DefaultStringValue("型号")
    @Key("modelNumber")
    String modelNumber();

    @DefaultStringValue("设备型号，例如Cisco 3845，Oki B4400等")
    @Key("modelNumberHelp")
    String modelNumberHelp();

    @DefaultStringValue("节点ID：")
    @Key("nodeIdLabel")
    String nodeIdLabel();

    @DefaultStringValue("节点信息")
    @Key("nodeInfoLink")
    String nodeInfoLink();

    @DefaultStringValue("参数有误，节点：")
    @Key("nodeParamNotValidInt")
    String nodeParamNotValidInt();

    @DefaultStringValue("告警类别")
    @Key("notificationCategory")
    String notificationCat();

    @DefaultStringValue("告警类别")
    @Key("notificationCategoryHelp")
    String notificationCatHelp();

    @DefaultStringValue("电源数量")
    @Key("numpowersupplies")
    String numpowersupplies();

    @DefaultStringValue("电源数量")
    @Key("numpowersuppliesHelp")
    String numpowersuppliesHelp();

    @DefaultStringValue("操作系统")
    @Key("operatingSystem")
    String operatingSystem();

    @DefaultStringValue("操作系统")
    @Key("operatingSystemHelp")
    String operatingSystemHelp();

    @DefaultStringValue("密码")
    @Key("password")
    String password();

    @DefaultStringValue("密码")
    @Key("passwordHelp")
    String passwordHelp();

    @DefaultStringValue("电话")
    @Key("phone")
    String phone();

    @DefaultStringValue("电话")
    @Key("phoneHelp")
    String phoneHelp();

    @DefaultStringValue("Poller类别")
    @Key("pollerCategory")
    String pollerCat();

    @DefaultStringValue("Poller类别")
    @Key("pollerCategoryHelp")
    String pollerCatHelp();

    @DefaultStringValue("端口")
    @Key("port")
    String port();

    @DefaultStringValue("端口")
    @Key("portHelp")
    String portHelp();

    @DefaultStringValue("机架")
    @Key("rack")
    String rack();

    @DefaultStringValue("机架")
    @Key("rackHelp")
    String rackHelp();

    @DefaultStringValue("机架高度")
    @Key("rackUnitHeight")
    String rackUnitHeight();

    @DefaultStringValue("机架高度")
    @Key("rackUnitHeightHelp")
    String rackUnitHeightHelp();

    @DefaultStringValue("内存")
    @Key("ram")
    String ram();

    @DefaultStringValue("内存")
    @Key("ramHelp")
    String ramHelp();

    @DefaultStringValue("区域")
    @Key("region")
    String region();

    @DefaultStringValue("区域")
    @Key("regionHelp")
    String regionHelp();

    @DefaultStringValue("重置")
    @Key("resetButton")
    String resetButton();

    @DefaultStringValue("房间")
    @Key("room")
    String room();

    @DefaultStringValue("房间")
    @Key("roomHelp")
    String roomHelp();

    /* Submit */
    @DefaultStringValue("保存")
    @Key("saveButton")
    String saveButton();

    @DefaultStringValue("序列号")
    @Key("serialNumber")
    String serialNumber();

    @DefaultStringValue("序列号")
    @Key("serialNumberHelp")
    String serialNumberHelp();

    @DefaultStringValue("槽位")
    @Key("slot")
    String slot();

    @DefaultStringValue("槽位")
    @Key("slotHelp")
    String slotHelp();

    @DefaultStringValue("SNMP community")
    @Key("snmpcommunity")
    String snmpcommunity();

    @DefaultStringValue("SNMP团体名")
    @Key("snmpcommunityHelp")
    String snmpcommunityHelp();

    /* SNMP Labels */
    @DefaultStringValue("SNMP信息")
    @Key("snmpHeader")
    String snmpHeader();

    @DefaultStringValue("省")
    @Key("state")
    String state();

    @DefaultStringValue("省")
    @Key("stateHelp")
    String stateHelp();

    @DefaultStringValue("存储控制器")
    @Key("storagectrl")
    String storagectrl();

    @DefaultStringValue("存储控制器")
    @Key("storagectrlHelp")
    String storagectrlHelp();

    @DefaultStringValue("无法识别日期，请从日历中选取")
    @Key("stringNotADate")
    String stringNotADate();

    @DefaultStringValue("输入内容不属于Integer：")
    @Key("stringNoValidInteger")
    String stringNoValidInteger();

    @DefaultStringValue("输入内容过长。该字段最大长度：")
    @Key("stringToLongError")
    String stringToLongError();

    @DefaultStringValue("仅限输入A-Z、a-z、0-9、‘-’、和‘_’")
    @Key("stringBasicValidationError")
    String stringBasicValidationError();

    @DefaultStringValue("输入中有空格，请去除")
    @Key("stringContainsWhiteSpacesError")
    String stringContainsWhiteSpacesError();

    @DefaultStringValue("输入不符合正则表达式")
    @Key("stringNotMatchingRegexpError")
    String stringNotMatchingRegexpError();

    @DefaultStringValue("系统联系人")
    @Key("systemContact")
    String systemContact();

    @DefaultStringValue("系统联系人")
    @Key("systemContactHelp")
    String systemContactHelp();

    @DefaultStringValue("系统描述")
    @Key("systemDescription")
    String systemDescription();

    @DefaultStringValue("系统描述")
    @Key("systemDescriptionHelp")
    String systemDescriptionHelp();

    @DefaultStringValue("系统编号")
    @Key("systemId")
    String systemId();

    @DefaultStringValue("系统编号")
    @Key("systemIdHelp")
    String systemIdHelp();

    @DefaultStringValue("系统位置")
    @Key("systemLocation")
    String systemLocation();

    @DefaultStringValue("系统位置")
    @Key("systemLocationHelp")
    String systemLocationHelp();

    @DefaultStringValue("系统名称")
    @Key("systemName")
    String systemName();

    @DefaultStringValue("系统名称")
    @Key("systemNameHelp")
    String systemNameHelp();

    @DefaultStringValue("阈值类别")
    @Key("thresholdCategory")
    String thresholdCat();

    @DefaultStringValue("阈值类别")
    @Key("thresholdCategoryHelp")
    String thresholdCatHelp();

    @DefaultStringValue("用户名")
    @Key("username")
    String username();

    @DefaultStringValue("用户名")
    @Key("usernameHelp")
    String usernameHelp();

    @DefaultStringValue("供应商资产")
    @Key("vendorAsset")
    String vendorAsset();

    @DefaultStringValue("供应商资产")
    @Key("vendorAssetHelp")
    String vendorAssetHelp();

    /* Vendor */
    @DefaultStringValue("供应商")
    @Key("vendorHeader")
    String vendorHeader();

    @DefaultStringValue("供应商名称")
    @Key("vendorName")
    String vendorName();

    @DefaultStringValue("供应商名称")
    @Key("vendorNameHelp")
    String vendorNameHelp();

    @DefaultStringValue("邮编")
    @Key("zip")
    String zip();

    @DefaultStringValue("邮编")
    @Key("zipHelp")
    String zipHelp();

    @DefaultStringValue("国家")
    @Key("country")
    String country();

    @DefaultStringValue("国家")
    @Key("countryHelp")
    String countryHelp();

    /* VMware asset fields */
    @DefaultStringValue("虚拟机")
    @Key("vmwareHeader")
    String vmwareHeader();

    @DefaultStringValue("虚拟机管理对象编号")
    @Key("vmwareManagedObjectId")
    String vmwareManagedObjectId();

    @DefaultStringValue("虚拟机内部编号")
    @Key("vmwareManagedObjectIdHelp")
    String vmwareManagedObjectIdHelp();

    @DefaultStringValue("虚拟机管理对象类别")
    @Key("vmwareManagedEntityType")
    String vmwareManagedEntityType();

    @DefaultStringValue("虚拟机管理对象类别")
    @Key("vmwareManagedEntityTypeHelp")
    String vmwareManagedEntityTypeHelp();

    @DefaultStringValue("虚拟机管理服务器")
    @Key("vmwareManagementServer")
    String vmwareManagementServer();

    @DefaultStringValue("虚拟机vCenter主机")
    @Key("vmwareManagementServerHelp")
    String vmwareManagementServerHelp();

    @DefaultStringValue("虚拟机拓扑信息")
    @Key("vmwareTopologyInfo")
    String vmwareTopologyInfo();

    @DefaultStringValue("虚拟机拓扑信息")
    @Key("vmwareTopologyInfoHelp")
    String vmwareTopologyInfoHelp();

    @DefaultStringValue("虚拟机状态")
    @Key("vmwareState")
    String vmwareState();

    @DefaultStringValue("虚拟机状态")
    @Key("vmwareStateHelp")
    String vmwareStateHelp();
}
