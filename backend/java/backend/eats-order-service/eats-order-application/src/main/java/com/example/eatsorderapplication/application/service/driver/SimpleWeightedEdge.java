package com.example.eatsorderapplication.application.service.driver;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.Serial;

public class SimpleWeightedEdge extends DefaultWeightedEdge {

    // 소스에 대한 public 제네릭 getter 메서드
    @SuppressWarnings("unchecked")
    @Override
    public String getSource() {
        return (String) super.getSource();
    }

    // 타겟에 대한 public 제네릭 getter 메서드
    @SuppressWarnings("unchecked")
    @Override
    public String getTarget() {
        return (String) super.getTarget();
    }

    // 가중치에 대한 public getter 메서드
    @Override
    public double getWeight() {
        return super.getWeight();
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + ")";
    }
}
