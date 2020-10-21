package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.configuration.PathConfiguration;
import com.example.bianhan.iftttgenerator.pojo.*;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;
import rwth.i2.ltl2ba4j.formula.IFormula;
import rwth.i2.ltl2ba4j.formula.IFormulaFactory;
import rwth.i2.ltl2ba4j.formula.impl.FormulaFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeIfThenRequirements;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.computeMap;
import static com.example.bianhan.iftttgenerator.util.ComputeUtil.initRequirements;

@Service("formalismService")
public class FormalismService {
    public IFormula toLtl(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        Map<String, String> intendMap = computeMap(PathConfiguration.DROOLSMAPPATH, "intendMap", eo);
        List<Requirement> requirements = initRequirements(Arrays.asList(requirementTexts.split("//")));
        IFormulaFactory factory = new FormulaFactory();
        List<IFormula> formulas = new ArrayList<>();
        IFormula formula;
        for(Requirement requirement : requirements){
            if(requirement instanceof AlwaysNeverRequirement){
                AlwaysNeverRequirement alwaysNeverRequirement = (AlwaysNeverRequirement) requirement;
                if(alwaysNeverRequirement.getAttribute() == null){
                    String alwaysNever = alwaysNeverRequirement.getAlwaysNever();
                    String deviceEventOrState = alwaysNeverRequirement.getDeviceEventOrState();
                    if(alwaysNever.equals("ALWAYS")) formula = factory.G(factory.Proposition(deviceEventOrState));
                    else formula = factory.G(factory.Not(factory.Proposition(eo.getEventMappingToState().get(deviceEventOrState))));
                }
                else {
                    String attribute = alwaysNeverRequirement.getAttribute();
                    String relation = alwaysNeverRequirement.getRelation();
                    double value = alwaysNeverRequirement.getValue();
                    String alwaysNever = alwaysNeverRequirement.getAlwaysNever();
                    String proposition = relation.endsWith("ABOVE") ? attribute + ">" + value : attribute + "<" + value;
                    if(alwaysNever.equals("ALWAYS")) formula = factory.G(factory.Proposition(proposition));
                    else formula = factory.G(factory.Not(factory.Proposition(proposition)));
                }
                formulas.add(formula);
            }
            else if(requirement instanceof OccurenceRequirement){
                OccurenceRequirement occurenceRequirement = (OccurenceRequirement) requirement;
                String state1 = occurenceRequirement.getDeviceStates().get(0);
                String state2 = occurenceRequirement.getDeviceStates().get(1);
                IFormula tempFormula = factory.And(factory.Proposition(state1), factory.Proposition(state2));
                if(occurenceRequirement.getDeviceStates().size() > 2){
                    for(int i = 2;i < occurenceRequirement.getDeviceStates().size() - 1;i++){
                        String state = occurenceRequirement.getDeviceStates().get(i);
                        tempFormula = factory.And(tempFormula, factory.Proposition(state));
                    }
                }
                formula = factory.Not(tempFormula);
                formulas.add(formula);
            }
        }

        List<IfThenRequirement> ifThenRequirements = computeIfThenRequirements(requirements, intendMap, ontologyPath).get(index);
        for(IfThenRequirement ifThenRequirement : ifThenRequirements){
            List<String> triggers = ifThenRequirement.getTriggerList();
            List<String> actions = ifThenRequirement.getActionList();
            IFormula triggerFormula;
            IFormula actionFormula;
            if(triggers.size() == 1) triggerFormula = factory.Proposition(triggers.get(0));
            else if(triggers.size() == 2){
                triggerFormula = factory.And(factory.Proposition(triggers.get(0)), factory.Proposition(triggers.get(1)));
            }
            else {
                triggerFormula = factory.And(factory.Proposition(triggers.get(0)), factory.Proposition(triggers.get(1)));
                for(int i = 2;i < triggers.size();i++) triggerFormula = factory.And(triggerFormula, factory.Proposition(triggers.get(i)));
            }
            if(actions.size() == 1) actionFormula = factory.Proposition(actions.get(0));
            else if(actions.size() == 2){
                actionFormula = factory.And(factory.Proposition(actions.get(0)), factory.Proposition(actions.get(1)));
            }
            else {
                actionFormula = factory.And(factory.Proposition(actions.get(0)), factory.Proposition(actions.get(1)));
                for(int i = 2;i < actions.size();i++) actionFormula = factory.And(actionFormula, factory.Proposition(actions.get(i)));
            }
            formula = factory.Impl(triggerFormula, factory.X(actionFormula));
            formulas.add(formula);
        }
        if(formulas.size() == 1) return formulas.get(0);
        else if(formulas.size() == 2) return factory.And(formulas.get(0), formulas.get(1));
        else {
            IFormula tempFormula = factory.And(formulas.get(0), formulas.get(1));
            for(int i = 2;i < formulas.size();i++) tempFormula = factory.And(tempFormula, formulas.get(i));
            return tempFormula;
        }
    }

    public static void main(String[] args) throws IOException, DocumentException {
        FormalismService formalismService = new FormalismService();
        String ontologyPath = "ontology_SmartConferenceRoom.xml";
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
        String re = "IF Person.distanceFromPro<2 THEN Blind.bclosed,Projector.pon//IF Air.humidity>30 THEN allow ventilating the room";
        System.out.println(formalismService.toLtl(re, ontologyPath ,0));
    }
}

