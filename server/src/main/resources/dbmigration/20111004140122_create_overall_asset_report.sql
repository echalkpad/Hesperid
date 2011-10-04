INSERT INTO report_type (id,hql_query,jasper_xml_code,name) VALUES (1,'from Asset','<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="report name" pageWidth="1050" pageHeight="595" orientation="Landscape" columnWidth="1050" leftMargin="0" rightMargin="0" topMargin="0" bottomMargin="0">
	<property name="net.sf.jasperreports.print.keep.full.text" value="true"/>
	<property name="ireport.zoom" value="1.0"/>
	<property name="ireport.x" value="287"/>
	<property name="ireport.y" value="0"/>
	<parameter name="reportTitle" class="java.lang.String"/>
	<parameter name="logo" class="java.lang.Object"/>
	<field name="assetName" class="java.lang.String"/>
	<field name="assetIdentifier" class="java.lang.String"/>
	<field name="carePack" class="java.lang.String"/>
	<field name="description" class="java.lang.String"/>
	<field name="host" class="java.lang.String"/>
	<field name="purchased" class="java.util.Date"/>
	<field name="costPerYear" class="java.math.BigDecimal"/>
	<field name="roomNumber" class="java.lang.String"/>
	<field name="location" class="ch.astina.hesperid.model.base.Location"/>
	<field name="asset" class="ch.astina.hesperid.model.base.Asset">
		<fieldDescription><![CDATA[_THIS]]></fieldDescription>
	</field>
	<title>
		<band height="100" splitType="Stretch">
			<image>
				<reportElement x="0" y="0" width="301" height="48"/>
				<imageExpression class="java.awt.Image"><![CDATA[$P{logo}]]></imageExpression>
			</image>
			<textField isBlankWhenNull="true">
				<reportElement x="400" y="0" width="650" height="48"/>
				<textElement verticalAlignment="Middle">
					<font size="36"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$P{reportTitle}]]></textFieldExpression>
			</textField>
			<staticText>
				<reportElement x="0" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Asset Name]]></text>
			</staticText>
			<staticText>
				<reportElement x="100" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Asset Identifier]]></text>
			</staticText>
			<staticText>
				<reportElement x="200" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Care Pack]]></text>
			</staticText>
			<staticText>
				<reportElement x="300" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Description]]></text>
			</staticText>
			<staticText>
				<reportElement x="400" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Host]]></text>
			</staticText>
			<staticText>
				<reportElement x="500" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Purchased]]></text>
			</staticText>
			<staticText>
				<reportElement x="600" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Cost per Year]]></text>
			</staticText>
			<staticText>
				<reportElement x="700" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Location]]></text>
			</staticText>
			<staticText>
				<reportElement x="800" y="80" width="100" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Room Number]]></text>
			</staticText>
			<staticText>
				<reportElement x="900" y="80" width="150" height="20"/>
				<textElement>
					<font isBold="true"/>
				</textElement>
				<text><![CDATA[Application Owner]]></text>
			</staticText>
		</band>
	</title>
	<detail>
		<band height="20" splitType="Stretch">
			<textField isBlankWhenNull="true">
				<reportElement x="0" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{assetName}]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="100" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{assetIdentifier}]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="200" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{carePack}]]></textFieldExpression>
			</textField>
			<textField isStretchWithOverflow="true" isBlankWhenNull="true">
				<reportElement x="300" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{description}]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="400" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{host}]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="500" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{purchased}.toString()]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="600" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{costPerYear}.toString()]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="700" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{location}.getName()]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="800" y="0" width="100" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{roomNumber}]]></textFieldExpression>
			</textField>
			<textField isBlankWhenNull="true">
				<reportElement x="900" y="0" width="150" height="20"/>
				<textElement>
					<font size="8"/>
				</textElement>
				<textFieldExpression class="java.lang.String"><![CDATA[$F{asset}.getAssetContactForRolename("Application Owner").getContact().getLastName() + ", " + $F{asset}.getAssetContactForRolename("Application Owner").getContact().getFirstName()]]></textFieldExpression>
			</textField>
		</band>
	</detail>
</jasperReport>', 'Overall Asset Report');