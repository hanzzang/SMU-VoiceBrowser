<?xml version="1.0" encoding="UTF-8" ?>
<!-- 모든 텍스트 노드를 하나의 텍스트 스트링으로 추출 -->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="*">
        <xsl:apply-templates select="node()|@*"/>
        <xsl:text> </xsl:text>
    </xsl:template>
</xsl:stylesheet>