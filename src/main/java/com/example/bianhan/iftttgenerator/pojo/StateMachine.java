package com.example.bianhan.iftttgenerator.pojo;

import java.util.List;

public class StateMachine {
    private String name;
    private List<String> states;
    private List<Transition> transitions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StateMachine(String name, List<String> states, List<Transition> transitions) {
        this.name = name;
        this.states = states;
        this.transitions = transitions;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     * e.g. getActionByState("windowClosed") -> "windowClosePulse"
     * @param state
     * @return action that will cause the input state to happen
     */
    public String getActionByState(String state){
        for(int i = 0;i < transitions.size();i++){
            Transition transition = transitions.get(i);
            if(!transition.getFrom().equals(state) && transition.getTo().equals(state)) return transition.getEvent();
        }
        return null;
    }

    @Override
    public String toString() {
        String result = "states:{";
        for(int i =0 ;i < states.size();i++){
            result = result + states.get(i);
            if(i != states.size() - 1) result += ", ";
            else result += "} transitions:{";
        }
        for(int i = 0;i < transitions.size();i++){
            Transition transition = transitions.get(i);
            result = result + transition.toString();
            if(i != transitions.size() - 1) result += ", ";
            else result += "} ";
        }
        return result;
    }
}
