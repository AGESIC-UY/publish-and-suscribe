package uy.gub.agesic.pdi.pys.push.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RefreshScope
public class PYSPushProperties implements Serializable {

    @Value("${application.accessPDI.enabled:false}")
    private Boolean accessPDIEnabled;

    @Value("${application.client.config.file:none}")
    private String configFile;

    @Value("${application.push.sleepTimePolicy.diasMultiplicador:1}")
    private int diasMultiplicador;

    @Value("${application.push.sleepTimePolicy.maxReintentosExponencial:15}")
    private int maxReintentosExponencial;

    @Value("${application.push.ws.connect.timeout:1000}")
    private int wsConnectTimeout;

    @Value("${application.push.ws.request.timeout:3000}")
    private int wsRequestTimeout;

    @Value("${application.push.master:false}")
    private boolean isMaster;

    @Value("${application.push.clusterMembers:2}")
    private int clusterMembers;

    @Value("${application.push.controller.sleepTime:30000}")
    private long controllerSleepTime;

    @Value("${application.push.ts.sleepTime:10000}")
    private long tsSleepTime;

    @Value("${application.push.ts.count:1}")
    private int pushCount;

    @Value("${jgroups.udp.mcast_addr}")
    private String mcastAddress;

    @Value("${jgroups.udp.mcast_port}")
    private int mcastPort;

    public Boolean getAccessPDIEnabled() {
        return accessPDIEnabled;
    }

    public void setAccessPDIEnabled(Boolean accessPDIEnabled) {
        this.accessPDIEnabled = accessPDIEnabled;
    }

    public int getDiasMultiplicador() {
        return diasMultiplicador;
    }

    public void setDiasMultiplicador(int diasMultiplicador) {
        this.diasMultiplicador = diasMultiplicador;
    }

    public int getMaxReintentosExponencial() {
        return maxReintentosExponencial;
    }

    public void setMaxReintentosExponencial(int maxReintentosExponencial) {
        this.maxReintentosExponencial = maxReintentosExponencial;
    }

    public String getConfigFile() {
        if (configFile != null && configFile.equals("none")) {
            return null;
        }
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public int getWsConnectTimeout() {
        return wsConnectTimeout;
    }

    public void setWsConnectTimeout(int wsConnectTimeout) {
        this.wsConnectTimeout = wsConnectTimeout;
    }

    public int getWsRequestTimeout() {
        return wsRequestTimeout;
    }

    public void setWsRequestTimeout(int wsRequestTimeout) {
        this.wsRequestTimeout = wsRequestTimeout;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public int getClusterMembers() {
        return clusterMembers;
    }

    public void setClusterMembers(int clusterMembers) {
        this.clusterMembers = clusterMembers;
    }

    public long getControllerSleepTime() {
        return controllerSleepTime;
    }

    public void setControllerSleepTime(long controllerSleepTime) {
        this.controllerSleepTime = controllerSleepTime;
    }

    public long getTsSleepTime() {
        return tsSleepTime;
    }

    public void setTsSleepTime(long tsSleepTime) {
        this.tsSleepTime = tsSleepTime;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void setPushCount(int pushCount) {
        this.pushCount = pushCount;
    }

    public String getMcastAddress() {
        return mcastAddress;
    }

    public void setMcastAddress(String mcastAddress) {
        this.mcastAddress = mcastAddress;
    }

    public int getMcastPort() {
        return mcastPort;
    }

    public void setMcastPort(int mcastPort) {
        this.mcastPort = mcastPort;
    }
}