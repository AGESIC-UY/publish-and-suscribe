package uy.gub.agesic.pdi.pys.backoffice.utiles.ui;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.resource.UrlResourceStream;
import org.apache.wicket.core.util.resource.locator.ResourceStreamLocator;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;

import java.net.MalformedURLException;
import java.net.URL;

public class BackofficeStreamLocator extends ResourceStreamLocator {

    @Override
    public IResourceStream locate(Class<?> clazz, String path) {

        String extension = path.substring(path.lastIndexOf('.') + 1);
        String simpleClassName = Strings.lastPathComponent(clazz.getName(), '.');

        char[] pageChars = simpleClassName.toCharArray();

        pageChars[0] = Character.toLowerCase(pageChars[0]);

        String pageName = new String(pageChars);

        String location = "/" + pageName + "." + extension;

        try {
            URL url = WebApplication.get().getServletContext().getResource(location);

            if (url != null) {
                return new UrlResourceStream(url);
            }
        } catch (MalformedURLException e) {
            throw new WicketRuntimeException(e);
        }

        // Default lookup strategy
        return super.locate(clazz, path);
    }

}