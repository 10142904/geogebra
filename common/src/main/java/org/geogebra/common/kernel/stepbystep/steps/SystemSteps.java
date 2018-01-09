package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.util.debug.Log;

import java.util.HashSet;
import java.util.Set;

public class SystemSteps {

    public static StepSet solveBySubstitution(StepEquationSystem ses, SolutionBuilder steps) {
        int n = ses.getEquations().length;
        boolean[] solved = new boolean[n];

        SolutionBuilder tempSteps = new SolutionBuilder();
        StepEquationSystem tempSystem = ses.deepCopy();

        steps.add(SolutionStepType.SOLVE, ses);

        for (int k = 0; k < n; k++) {
            int eqIndex = -1, minSolutions = -1, minComplexity = -1;
            StepVariable minVariable = null;
            for (int i = 0; i < n; i++) {
                if (solved[i]) {
                    continue;
                }

                Set<StepVariable> variableSet = new HashSet<>();
                tempSystem.getEquation(i).getListOfVariables(variableSet);

                for (StepVariable variable : variableSet) {
                    StepNode[] solutions;

                    try {
                        tempSteps.reset();
                        solutions = tempSystem.getEquation(i).solve(variable, tempSteps).getElements();
                    } catch (SolveFailedException e) {
                        Log.error("failed to solve: ");
                        Log.error(tempSystem.getEquation(i) + "");

                        continue;
                    }

                    int complexity = tempSteps.getSteps().getComplexity();

                    Log.error(tempSystem.getEquation(i) + "");
                    Log.error(variable + "");
                    Log.error(complexity + "");

                    if (minSolutions == -1 || minSolutions > solutions.length ||
                            (minSolutions == solutions.length && minComplexity > complexity)) {
                        eqIndex = i;
                        minVariable = variable;
                        minSolutions = solutions.length;
                        minComplexity = complexity;
                    }
                }
            }

            if (eqIndex == -1) {
                throw new SolveFailedException(steps.getSteps());
            }
            solved[eqIndex] = true;

            tempSteps.reset();
            StepNode[] solutions = tempSystem.getEquation(eqIndex).solve(minVariable, tempSteps).getElements();

            if (tempSteps.getSteps().getComplexity() != 2) {
                steps.addAll(tempSteps.getSteps());
            }

            if (solutions.length == 0) {
                steps.add(SolutionStepType.NO_REAL_SOLUTION);
            }

            if (solutions.length == 1) {
                StepExpression solution = (StepExpression) solutions[0];

                StepEquation[] newEquations = new StepEquation[n];
                for (int j = 0; j < n; j++) {
                    if (j == eqIndex) {
                        newEquations[j] = new StepEquation(minVariable, solution);
                    } else {
                        newEquations[j] = tempSystem.getEquation(j).deepCopy();
                    }
                }

                steps.add(SolutionStepType.REPLACE_WITH_AND_REGROUP, minVariable, solution);
                steps.levelDown();
                for (int j = 0; j < n; j++) {
                    if (j == eqIndex) {
                        continue;
                    }

                    steps.add(SolutionStepType.EQUATION, newEquations[j]);
                    steps.levelDown();
                    newEquations[j].replace(minVariable, solution, steps);
                    newEquations[j].regroup(steps);
                    steps.levelUp();
                }
                steps.levelUp();

                tempSystem = new StepEquationSystem(newEquations);
                steps.add(SolutionStepType.EQUATION, tempSystem);
            } else {
                for (StepNode solutionNode : solutions) {
                    StepExpression solution = (StepExpression) solutionNode;

                    StepEquation[] newEquations = new StepEquation[n];
                    for (int j = 0; j < n; j++) {
                        if (j == eqIndex) {
                            newEquations[j] = new StepEquation(minVariable, solution);
                        } else {
                            newEquations[j] = tempSystem.getEquation(j).deepCopy();
                        }
                    }
                    StepEquationSystem newSes = new StepEquationSystem(newEquations);

                    solveBySubstitution(newSes, steps);
                }

                return new StepSet();
            }
        }


        return new StepSet(tempSystem);
    }

}
