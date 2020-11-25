package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.seguridad;

import org.apache.wicket.Application;
import org.apache.wicket.core.request.handler.RequestSettingRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.IProvider;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;

import java.util.List;

public class BackofficeCryptoMapper implements IRequestMapper {

    private final Logger logger;
    private final IRequestMapper wrappedMapper;
    private final IProvider<ICrypt> cryptProvider;

    public BackofficeCryptoMapper(final IRequestMapper wrappedMapper, final Application application, final Logger logger) {
        this.wrappedMapper = Args.notNull(wrappedMapper, "wrappedMapper");
        this.cryptProvider = Args.notNull(new ApplicationCryptProvider(application), "cryptProvider");
        this.logger = logger;
    }

    @Override
    public int getCompatibilityScore(final Request request) {
        return wrappedMapper.getCompatibilityScore(request);
    }

    @Override
    public Url mapHandler(final IRequestHandler requestHandler) {
        final Url url = wrappedMapper.mapHandler(requestHandler);

        if (url == null) {
            return null;
        }

        if (url.isFull()) {
            // do not encrypt full urls
            return url;
        }

        return encryptUrl(url);
    }

    @Override
    public IRequestHandler mapRequest(final Request request) {
        Url url = decryptUrl(request, request.getUrl());

        if (url == null) {
            return wrappedMapper.mapRequest(request);
        }

        Request decryptedRequest = request.cloneWithUrl(url);

        IRequestHandler handler = wrappedMapper.mapRequest(decryptedRequest);

        if (handler != null) {
            handler = new RequestSettingRequestHandler(decryptedRequest, handler);
        }

        return handler;
    }

    protected final ICrypt getCrypt() {
        return cryptProvider.get();
    }

    protected final IRequestMapper getWrappedMapper() {
        return wrappedMapper;
    }

    protected Url encryptUrl(final Url url) {
        if (url.getSegments().isEmpty()) {
            return url;
        }

        // Usado para evitar encriptacion de solicitudes ajax
        if (url.toString().contains("IOnChangeListener")) {
            return (url);
        }

        String encryptedUrlString = getCrypt().encryptUrlSafe(url.toString());

        Url encryptedUrl = new Url(url.getCharset());
        encryptedUrl.getSegments().add(encryptedUrlString);

        int numberOfSegments = url.getSegments().size();
        HashedSegmentGenerator generator = new HashedSegmentGenerator(encryptedUrlString);
        for (int segNo = 0; segNo < numberOfSegments; segNo++) {
            encryptedUrl.getSegments().add(generator.next());
        }

        // Add the pageName at the begining, but add it with  no encryption
        String pageName = url.getSegments().get(0);
        encryptedUrl.getSegments().add(0, pageName);

        return encryptedUrl;
    }

    @java.lang.SuppressWarnings("squid:S135")
    protected Url decryptUrl(final Request request, final Url encryptedUrl) {
        /*
         * If the encrypted URL has no segments it is the home page URL, and does not need
         * decrypting.
         */
        if (encryptedUrl.getSegments().isEmpty()) {
            return encryptedUrl;
        }

        // Usado para evitar desencriptaciones de solicitudes ajax
        if (encryptedUrl.toString().contains("IOnChangeListener")) {
            return (encryptedUrl);
        }

        List<String> encryptedSegments = encryptedUrl.getSegments();

        // Remove the first segment, the name of the page as plain text
        encryptedSegments.remove(0);

        Url url = new Url(request.getCharset());
        try {
            /*
             * The first encrypted segment contains an encrypted version of the entire plain text
             * url.
             */
            String encryptedUrlString = null;
            if (!encryptedSegments.isEmpty()) {
                encryptedUrlString = encryptedSegments.get(0);
                if (Strings.isEmpty(encryptedUrlString)) {
                    return null;
                }
            } else {
                return null;
            }

            String decryptedUrl = getCrypt().decryptUrlSafe(encryptedUrlString);
            if (decryptedUrl == null) {
                return null;
            }
            Url originalUrl = Url.parse(decryptedUrl, request.getCharset());

            int originalNumberOfSegments = originalUrl.getSegments().size();
            int encryptedNumberOfSegments = encryptedUrl.getSegments().size();

            HashedSegmentGenerator generator = new HashedSegmentGenerator(encryptedUrlString);
            int segNo = 1;
            for (; segNo < encryptedNumberOfSegments; segNo++) {
                if (segNo > originalNumberOfSegments) {
                    break;
                }

                String next = generator.next();
                String encryptedSegment = encryptedSegments.get(segNo);
                if (!next.equals(encryptedSegment)) {
                    /*
                     * This segment received from the browser is not the same as the expected
                     * segment generated by the HashSegmentGenerator. Hence it, and all subsequent
                     * segments are considered plain text siblings of the original encrypted url.
                     */
                    break;
                }

                /*
                 * This segments matches the expected checksum, so we add the corresponding segment
                 * from the original URL.
                 */
                url.getSegments().add(originalUrl.getSegments().get(segNo - 1));
            }

            /*
             * Add all remaining segments from the encrypted url as plain text segments.
             */
            for (; segNo < encryptedNumberOfSegments; segNo++) {
                // modified or additional segment
                url.getSegments().add(encryptedUrl.getSegments().get(segNo));
            }

            url.getQueryParameters().addAll(originalUrl.getQueryParameters());

            // WICKET-4923 additional parameters
            url.getQueryParameters().addAll(encryptedUrl.getQueryParameters());
        } catch (Exception ex) {
            logger.error("Error desencriptando la URL: " + url.toString(), ex);
            url = null;
        }

        return url;
    }

    private static class ApplicationCryptProvider implements IProvider<ICrypt> {
        private final Application application;

        public ApplicationCryptProvider(final Application application) {
            this.application = application;
        }

        @Override
        public ICrypt get() {
            return application.getSecuritySettings().getCryptFactory().newCrypt();
        }
    }

    /**
     * A generator of hashed segments.
     */
    public static class HashedSegmentGenerator {
        private char[] characters;

        private int hash = 0;

        public HashedSegmentGenerator(String string) {
            characters = string.toCharArray();
        }

        /**
         * Generate the next segment
         *
         * @return segment
         */
        public String next() {
            char a = characters[Math.abs(hash % characters.length)];
            hash++;
            char b = characters[Math.abs(hash % characters.length)];
            hash++;
            char c = characters[Math.abs(hash % characters.length)];

            String segment = "" + a + b + c;
            hash = hashString(segment);

            segment += String.format("%02x", Math.abs(hash % 256));
            hash = hashString(segment);

            return segment;
        }

        public int hashString(final String str) {
            int hashIndex = 97;

            for (char c : str.toCharArray()) {
                int i = c;
                hashIndex = 47 * hashIndex + i;
            }

            return hashIndex;
        }
    }
}