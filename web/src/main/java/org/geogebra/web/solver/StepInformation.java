package org.geogebra.web.solver;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import java.util.List;

public class StepInformation extends HorizontalPanel {

    private WebStepGuiBuilder builder;

    private SolutionStep steps;
    private VerticalPanel container;
    private FlowPanel renderedResult;
    private VerticalPanel renderedSteps;
    private StandardButton stepsButton;

    private boolean rendered;

    /**
     * Constructs a StepInformation given a single StepNode
     * @param app AppW for StandardButton
     * @param builder WebStepGuiBuilder for rendering step tree
     * @param result StepNode result
     * @param steps SolutionSteps tree to be rendered
     */
    public StepInformation(AppW app, WebStepGuiBuilder builder,
                           StepNode result, SolutionStep steps) {
        setupInformation(app, builder,
                new SolutionLine(SolutionStepType.EQUATION, result), steps);
    }

    /**
     * Constructs a StepInformation given a list of StepSolutions
     * @param app AppW for StandardButton
     * @param builder WebStepGuiBuilder for rendering step tree
     * @param result list of StepSolutions, to be rendered as the result
     * @param steps SolutionSteps tree to be rendered
     */
    public StepInformation(AppW app, WebStepGuiBuilder builder,
                           List<StepSolution> result, SolutionStep steps) {
		setupInformation(app, builder,
				new SolutionLine(SolutionStepType.LIST, result.toArray(new StepNode[0])),
				steps);
    }

    private void setupInformation(AppW app, WebStepGuiBuilder builder,
                           SolutionLine display, SolutionStep steps) {
        this.steps = steps;
        this.builder = builder;

        if (steps != null) {
            setStyleName("stepInformation");

            container = new VerticalPanel();

            renderedResult = builder.createRow(display, false);
            container.add(renderedResult);

            add(container);

            stepsButton = new StandardButton("Show Steps", app);
            stepsButton.setStyleName("solveButton");
            stepsButton.addFastClickHandler(new FastClickHandler() {
                @Override
                public void onClick(Widget source) {
                    showSteps();
                }
            });
            add(stepsButton);
        } else {
            add(builder.createRow(display, false));
        }
    }

    public void showSteps() {
        if (!rendered) {
            rendered = true;
            renderedSteps = builder.buildStepGui(steps);
            container.add(renderedSteps);
            stepsButton.setLabel("Hide Steps");
            renderedResult.setVisible(false);
        } else if (renderedSteps.isVisible()) {
            stepsButton.setLabel("Show Steps");
            renderedSteps.setVisible(false);
            renderedResult.setVisible(true);
        } else {
            stepsButton.setLabel("Hide Steps");
            renderedSteps.setVisible(true);
            renderedResult.setVisible(false);
        }
    }
}
