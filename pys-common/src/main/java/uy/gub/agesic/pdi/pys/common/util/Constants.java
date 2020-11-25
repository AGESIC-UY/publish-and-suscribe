package uy.gub.agesic.pdi.pys.common.util;

public class Constants {

    public static final String ERRORPROCHEADER = "ERRORPROCHEADER";
    public static final String ERRORMATCHROOTELEMENT =  "ERRORMATCHROOTELEMENT";
    public static final String ERRORMATCHDN = "ERRORMATCHDN";
    public static final String ERRORMATCHPUBLISHER = "ERRORMATCHPUBLISHER";
    public static final String ERRORMATCHTOPIC = "ERRORMATCHTOPIC";
    public static final String ERRORSOAPFAULTTEMPLATE = "ERRORSOAPFAULTTEMPLATE";
    public static final String ERRORCODESOAP = "ERRORCODESOAP";
    public static final String ERRORMATCHPRODTOPICO = "ERRORMATCHPRODTOPICO";
    public static final String ERRORMATCHSUSCRIBER = "ERRORMATCHSUSCRIBER";
    public static final String ERRORMATCHSUSCTOPICO = "ERRORMATCHSUSCTOPICO";

    public static final String HTTPSONLY = "HTTPSONLY";
    public static final String ERRORSUSCNOHAB = "ERRORSUSCNOHAB";
    public static final String ERRORPRODNOHAB = "ERRORPRODNOHAB";
    public static final String ERRORTOPICONOHAB = "ERRORTOPICONOHAB";

    public static final String PATHXSLPUBRESPONSE = "soapResponse.xsl";
    public static final String PATHXSLSOAPFAULT = "soapFault.xsl";
    public static final String PATHXSLPULLRESCONDATOS= "responsePullConDatos.xsl";
    public static final String PATHXSLPULLRESCONDATOS_RAZON= "responsePullConDatosRazon.xsl";
    public static final String PATHXSLPULLRESSINDATOS= "responsePullSinDatos.xsl";

    public static final String TRANSACTIONID_HEADER_NAME = "transactionId";

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String DEFAULT_CONTENTTYPE = "text/xml;charset=UTF-8";

    public static final String ERROR_HEADER_NAME = "error";

    public static final String MESSAGEID_HEADER_NAME = "messageID";

    public static final String PATRON_FECHA_HORA = "dd/MM/yy HH:mm:ss.SSS";

    public static final String PATRON_URI = "^http(s{0,1})://[a-zA-Z0-9][a-zA-Z0-9_/\\-\\.\\:]*$";

    public static final String  PATRON_ROOTELEMENT = "^[a-zA-Z_][a-zA-Z0-9_\\-\\.]*$";

    public static final String PATRON_NOMBRE = "^[a-zA-Z0-9][a-zA-Z0-9 _/\\-\\.\\:]*$";

    private Constants() {
    }

}
