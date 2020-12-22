package com.example.bianhan.iftttgenerator.pojo;

import lombok.Data;

import java.util.Iterator;
import java.util.Map;

@Data
public class Conflict {
    private Map<Integer, String> lineMappingToClause;
    private String errorType;

    public Conflict(Map<Integer, String> lineMappingToClause, String errorType) {
        this.lineMappingToClause = lineMappingToClause;
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        String result = "";
        Iterator it = lineMappingToClause.keySet().iterator();
        result = result + "<";
        while (it.hasNext()){
            int line = (int) it.next();
            String clause = lineMappingToClause.get(line);
            result = result + "line" + line + ", ";
        }
        result = result.substring(0,result.length() - 2);
        result = result + ">";
        return result;
    }
}
