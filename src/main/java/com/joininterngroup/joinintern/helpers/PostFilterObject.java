package com.joininterngroup.joinintern.helpers;

import java.util.List;

public class PostFilterObject {

    private String title;

    private Integer minDuration;

    private Integer maxDuration;

    private List<Integer> majors;

    private Float distanceZB;

    private Float distanceMH;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public Float getDistanceZB() {
        return distanceZB;
    }

    public void setDistanceZB(Float distanceZB) {
        this.distanceZB = distanceZB;
    }

    public Float getDistanceMH() {
        return distanceMH;
    }

    public void setDistanceMH(Float distanceMH) {
        this.distanceMH = distanceMH;
    }

    @Override
    public String toString() {
        return "PostFilterObject{" +
                "title='" + title + '\'' +
                ", minDuration=" + minDuration +
                ", maxDuration=" + maxDuration +
                ", majors=" + majors +
                ", distanceZB=" + distanceZB +
                ", distanceMH=" + distanceMH +
                '}';
    }
}
