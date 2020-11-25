<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="yes" method="xml"/>
    <xsl:param name="errorMessage"/>
    <xsl:param name="errorDescription"/>
    <xsl:param name="errorCode"/>
    <xsl:param name="messageId"/>
    <xsl:param name="relatesTo"/>
    <xsl:param name="wsaAction"/>
    <xsl:template match="/">
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
            <SOAP-ENV:Header>
                <wsa:MessageID><xsl:value-of select="$messageId"/></wsa:MessageID>
                <wsa:RelatesTo><xsl:value-of select="$relatesTo"/></wsa:RelatesTo>
                <xsl:if test="string-length($wsaAction) > 0">
                    <wsa:Action><xsl:value-of select="$wsaAction"/>Response</wsa:Action>
                </xsl:if>
            </SOAP-ENV:Header>
            <SOAP-ENV:Body>
                <SOAP-ENV:Fault>
                    <faultcode>
                        <xsl:value-of select="$errorCode"/>
                    </faultcode>
                    <faultstring>
                        <xsl:value-of select="$errorDescription"/>
                    </faultstring>
                </SOAP-ENV:Fault>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
    </xsl:template>
</xsl:stylesheet>
