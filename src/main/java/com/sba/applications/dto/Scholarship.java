
package com.sba.applications.dto;

public class Scholarship {
    private double score;
    private boolean isEligible;

    public Scholarship() {}

    public Scholarship(double score) {
        this.score = score;
        this.isEligible = score >= 8.0; // Eligible if score >= 8.0
    }
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
        this.isEligible = score >= 8.0;
    }

    public boolean isEligible() {
        return isEligible;
    }
}