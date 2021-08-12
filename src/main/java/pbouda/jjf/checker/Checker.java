package pbouda.jjf.checker;

import pbouda.jjf.Flag;

import java.util.Map;

public interface Checker {

    void check(Map<Flag, String> flags);

}
