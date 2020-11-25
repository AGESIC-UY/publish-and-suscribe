package uy.gub.agesic.pdi.pys.pub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class PubEndpoint extends HttpServlet {

    private final PubProcessor pubProcessor;

    private static boolean httpsOnly;

    @Autowired
    public PubEndpoint(PubProcessor pubProcessor) {
        this.pubProcessor = pubProcessor;
    }

    public static void accetpHttpsOnly(boolean httpsOnly) {
        PubEndpoint.httpsOnly = httpsOnly;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        pubProcessor.publicar(req, resp, PubEndpoint.httpsOnly);
    }

}
