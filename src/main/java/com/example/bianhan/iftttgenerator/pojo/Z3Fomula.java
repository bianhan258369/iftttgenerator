package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//(assert (forall ((humidity Int) (temperature Int)) (exists ((Window Int)) (and (=> (< humidity 30) (= Window 0)) (=> (> temperature 40) (= Window 1))))))
@Data
public class Z3Fomula {
    String device;//Window
    Set<String> attributes;//Air.umidity
    List<String> expressions;//[(=> (< Air.humidity 30) (= Window 0)),(= Window 1)]

    public Z3Fomula(String device) {
        this.device = device;
        this.attributes = new HashSet<>();
        this.expressions = new ArrayList<>();
    }

    public Z3Fomula(String device, Set<String> attributes, List<String> expressions) {
        this.device = device;
        this.attributes = attributes;
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(assert (forall (");
        for(String attribute : attributes){
            sb.append("(" + attribute + " Int)");
        }
        sb.append(") (exists ((" + device + " Int)) ");
        if(expressions.size() == 1) sb.append(expressions.get(0) + ")))");
        else {
            sb.append("(and ");
            for(String imply : expressions) sb.append(imply);
            sb.append("))))");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String device = "Window";
        Set<String> attributes = new HashSet<>();
        attributes.add("Air.humidity");
        attributes.add("Air.temperature");
        List<String> implies = new ArrayList<>();
        implies.add("(= Window 0))");
        implies.add("(=> (< Air.humidity 30) (= Window 0))");
        implies.add("(=> (> Air.temperature 40) (= Window 1))");
        Z3Fomula z3Fomula = new Z3Fomula(device, attributes, implies);
        System.out.println(z3Fomula.toString());
    }
}


