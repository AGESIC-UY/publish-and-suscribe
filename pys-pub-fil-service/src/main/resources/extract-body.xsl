<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                version="1.0">
  <xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="no"/>
  <xsl:template match="/">
    <xsl:copy-of select="//soapenv:Body/child::node()"/>
  </xsl:template>
</xsl:stylesheet>
