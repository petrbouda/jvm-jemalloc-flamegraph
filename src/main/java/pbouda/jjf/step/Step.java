package pbouda.jjf.step;

import pbouda.jjf.MutableContext;

@FunctionalInterface
public interface Step {

    void execute(MutableContext context);

}
