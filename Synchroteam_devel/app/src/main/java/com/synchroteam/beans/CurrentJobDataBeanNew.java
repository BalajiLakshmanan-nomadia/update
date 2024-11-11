package com.synchroteam.beans;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrentJobDataBean is used to store data of Jobs in Current Job Screen.
 */
public class CurrentJobDataBeanNew {

    /** The current jobs beans. */
    private Vector<CommonJobBean> currentJobsBeans;

    /** The current job count. */
    private int currentJobCount;

    /**
     * Instantiates a new current job data bean.
     *
     * @param currentJobsBeans the current jobs beans
     * @param currentJobCount the current job count
     */
    public CurrentJobDataBeanNew(Vector<CommonJobBean > currentJobsBeans,
                              int currentJobCount) {

        this.currentJobsBeans = currentJobsBeans;
        this.currentJobCount = currentJobCount;
    }

    /**
     * Gets the common job data bean.
     *
     * @return the common job data bean
     */
    public Vector<CommonJobBean> getCommonJobDataBean(){
        return currentJobsBeans;
    }


    /**
     * Gets the currrnt job count.
     *
     * @return the currrnt job count
     */
    public int getCurrrntJobCount(){
        return currentJobCount;
    }


}
