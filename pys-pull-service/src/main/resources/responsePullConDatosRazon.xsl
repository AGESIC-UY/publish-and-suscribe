<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ps="http://ps.agesic.gub.uy">
    <xsl:output omit-xml-declaration="yes" method="xml"/>
    <xsl:param name="replayProducer"/>
    <xsl:param name="replayNotificationId"/>
    <xsl:param name="createTS"/>
    <xsl:param name="replayXML"/>
    <xsl:param name="messageId"/>
    <xsl:param name="relatesTo"/>
    <xsl:param name="wsaAction"/>
    <xsl:param name="reason"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ps="http://ps.agesic.gub.uy" xmlns:wsa="http://www.w3.org/2005/08/addressing">
            <soapenv:Header>
                <wsa:MessageID><xsl:value-of select="$messageId"/></wsa:MessageID>
                <wsa:RelatesTo><xsl:value-of select="$relatesTo"/></wsa:RelatesTo>
                <xsl:if test="string-length($wsaAction) > 0">
                    <wsa:Action><xsl:value-of select="$wsaAction"/>Response</wsa:Action>
                </xsl:if>
                <ps:producer>
                    <xsl:value-of select="$replayProducer"/>
                </ps:producer>
                <ps:notificationId>
                    <xsl:value-of select="$replayNotificationId"/>
                </ps:notificationId>
                <ps:createTS>
                    <xsl:value-of select="$createTS"/>
                </ps:createTS>
                <ps:reason>
                    <xsl:value-of select="$reason"/>
                </ps:reason>
            </soapenv:Header>
            <soapenv:Body>
                <xsl:value-of select="$replayXML" disable-output-escaping="yes"/>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>
