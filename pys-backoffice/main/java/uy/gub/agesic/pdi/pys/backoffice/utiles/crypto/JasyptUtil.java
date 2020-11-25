package uy.gub.agesic.pdi.pys.backoffice.utiles.crypto;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.digest.StringDigester;

/**
 *
 * DEBEN INSTALARSE LAS UNLIMITED JAVA CRYPTOGRAPHY EXTENSIONS EN LA JVM QUE EJECUTE EL CODIGO ESTE , SINO NO FUNCIONA.
 * SON UN PAR DE JARS QUE SE PUEDEN DESCARGAR DESDE: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 *
 * DEBEN SER INSTALADOS EN JAVA_HOME/jre/lib/security
 */
public class JasyptUtil {

	private static final StringDigester PASSWORD_DIGESTER = buildStringDigester();

	private static StringDigester buildStringDigester() {
		StandardStringDigester encriptador = new StandardStringDigester();
		encriptador.setAlgorithm("SHA-256");
		encriptador.setIterations(1000);
		encriptador.setSaltSizeBytes(16);
		encriptador.setStringOutputType("hexadecimal");
		return encriptador;
	}

	public static String hashPassword(String password) {
		return PASSWORD_DIGESTER.digest(password);
	}

	public static boolean matchesPassword(String plainPassword, String hashedPassword) {
		return PASSWORD_DIGESTER.matches(plainPassword, hashedPassword);
	}

	private JasyptUtil() {
    }

}

