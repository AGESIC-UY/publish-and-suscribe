package uy.gub.agesic.pdi.pys.pull.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class PullEndpoint extends HttpServlet {

    private final PullProcessor pullProcessor;

    private static boolean httpsOnly;

    @Autowired
    public PullEndpoint(PullProcessor pullProcessor) {
        this.pullProcessor = pullProcessor;
    }

    public static void accetpHttpsOnly(boolean httpsOnly) {
        PullEndpoint.httpsOnly = httpsOnly;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        pullProcessor.pull(req, resp, PullEndpoint.httpsOnly);
    }

}
