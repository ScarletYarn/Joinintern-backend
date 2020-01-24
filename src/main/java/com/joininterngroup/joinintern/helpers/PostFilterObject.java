package com.joininterngroup.joinintern.helpers;

import java.util.List;

public class PostFilterObject {

    private Integer minDuration;

    private Integer maxDuration;

    private List<Integer> majors;

    private float distanceZB;

    private float distanceMH;

    public Integer getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public List<Integer> getMajors() {
        return majors;
    }

    public void setMajors(List<Integer> majors) {
        this.majors = majors;
    }

    public float getDistanceZB() {
        return distanceZB;
    }

    public void setDistanceZB(float distanceZB) {
        this.distanceZB = distanceZB;
    }

    public float getDistanceMH() {
        return distanceMH;
    }

    public void setDistanceMH(float distanceMH) {
        this.distanceMH = distanceMH;
    }

    @Override
    public String toString() {
        return "PostFilterObject{" +
                "minDuration=" + minDuration +
                ", maxDuration=" + maxDuration +
                ", majors=" + majors +
                ", distanceZB=" + distanceZB +
                ", distanceMH=" + distanceMH +
                '}';
    }
}
