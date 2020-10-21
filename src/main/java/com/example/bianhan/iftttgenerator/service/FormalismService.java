package com.example.bianhan.iftttgenerator.service;

import com.example.bianhan.iftttgenerator.pojo.AlwaysNeverRequirement;
import com.example.bianhan.iftttgenerator.pojo.EnvironmentOntology;
import com.example.bianhan.iftttgenerator.pojo.OccurenceRequirement;
import com.example.bianhan.iftttgenerator.pojo.Requirement;
import org.dom4j.DocumentException;
import rwth.i2.ltl2ba4j.formula.IFormula;
import rwth.i2.ltl2ba4j.formula.IFormulaFactory;
import rwth.i2.ltl2ba4j.formula.impl.FormulaFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.bianhan.iftttgenerator.util.ComputeUtil.initRequirements;

public class FormalismService {
    private List<IFormula> toLtl(String requirementTexts, String ontologyPath, int index) throws IOException, DocumentException {
        EnvironmentOntology eo = new EnvironmentOntology(ontologyPath);
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

        return formulas;
    }
}

