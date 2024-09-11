package com.example.eatsorderapplication.service.driver;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.Serial;

public class SimpleWeightedEdge<V> extends DefaultWeightedEdge {

    // 소스에 대한 public 제네릭 getter 메서드
    @SuppressWarnings("unchecked")
    @Override
    public V getSource() {
        return (V) super.getSource();
    }

    // 타겟에 대한 public 제네릭 getter 메서드
    @SuppressWarnings("unchecked")
    @Override
    public V getTarget() {
        return (V) super.getTarget();
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
