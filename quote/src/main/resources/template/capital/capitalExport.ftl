<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <Author>dzrh</Author>
  <LastAuthor>qiang cheng</LastAuthor>
  <Created>2024-01-09T01:51:05Z</Created>
  <LastSaved>2024-01-09T01:57:29Z</LastSaved>
  <Version>16.00</Version>
 </DocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>15720</WindowHeight>
  <WindowWidth>29040</WindowWidth>
  <WindowTopX>32767</WindowTopX>
  <WindowTopY>32767</WindowTopY>
  <ProtectStructure>False</ProtectStructure>
  <ProtectWindows>False</ProtectWindows>
 </ExcelWorkbook>
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Center"/>
   <Borders/>
   <Font ss:FontName="等线" x:CharSet="134" ss:Size="11" ss:Color="#000000"/>
   <Interior/>
   <NumberFormat/>
   <Protection/>
  </Style>
  <Style ss:ID="s57">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center" ss:WrapText="1"/>
   <Font ss:FontName="等线" x:CharSet="134" ss:Size="11" ss:Color="#000000" ss:Bold="1"/>
   <Interior ss:Color="#D0CECE" ss:Pattern="Solid"/>
  </Style>
  <Style ss:ID="s67">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="sheet1">
  <Table ss:ExpandedColumnCount="13" ss:ExpandedRowCount="2" x:FullColumns="1"
   x:FullRows="1" ss:DefaultColumnWidth="54" ss:DefaultRowHeight="14.25">
   <Column ss:AutoFitWidth="0" ss:Width="105"/>
   <Column ss:AutoFitWidth="0" ss:Width="126.75"/>
   <Column ss:Width="104.25"/>
   <Column ss:Width="183"/>
   <Column ss:Width="75"/>
   <Column ss:Width="71.25"/>
   <Column ss:AutoFitWidth="0" ss:Width="58.5"/>
   <Column ss:Width="115.5"/>
   <Column ss:AutoFitWidth="0" ss:Width="68.25"/>
   <Column ss:AutoFitWidth="0" ss:Width="73.5"/>
   <Column ss:Width="116.25" ss:Span="1"/>
   <Column ss:Index="13" ss:Width="29.25"/>
   <Row>
    <Cell ss:StyleID="s57"><Data ss:Type="String">操作时间</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">归属时间</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">资金编号</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">客户名称</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">方向类型</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">金额</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">币种</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">相关交易</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">标的代码</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">资金状态</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">操作人</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">创建人</Data></Cell>
    <Cell ss:StyleID="s57"><Data ss:Type="String">备注</Data></Cell>
   </Row>
   <#if capitalRecords?? >
    <#list capitalRecords as item>
     <Row>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.updateTimeString!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.vestingDate!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.capitalCode!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.clientName!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.directionName!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.money!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.currency!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.tradeCode!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.underlyingCode!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.capitalStatusName!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.updatorName!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.creatorName!''}</Data></Cell>
      <Cell ss:StyleID="s67"><Data ss:Type="String">${item.remark!''}</Data></Cell>
     </Row>
    </#list>
   </#if>
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <HorizontalResolution>600</HorizontalResolution>
    <VerticalResolution>600</VerticalResolution>
   </Print>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>4</ActiveRow>
     <ActiveCol>10</ActiveCol>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>
