<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="uuid"/>
    <xsl:param name="messageId"/>
    <xsl:param name="relatesTo"/>
    <xsl:param name="wsaAction"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://www.w3.org/2005/08/addressing"
                          xmlns:ps="http://ps.agesic.gub.uy">
            <soapenv:Header>
                <wsa:MessageID><xsl:value-of select="$messageId"/></wsa:MessageID>
                <wsa:RelatesTo><xsl:value-of select="$relatesTo"/></wsa:RelatesTo>
                <xsl:if test="string-length($wsaAction) > 0">
                <wsa:Action><xsl:value-of select="$wsaAction"/>Response</wsa:Action>
                </xsl:if>
                <ps:notificationId><xsl:value-of select="$uuid"/></ps:notificationId>
            </soapenv:Header>
            <soapenv:Body/>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>



