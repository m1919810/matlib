package me.matl114.matlib.unitTest;

import java.util.ArrayList;
import java.util.List;

public class TestSet implements TestCase {
    List<TestCase> tests = new ArrayList<>();

    public void init() {}

    public List<TestCase> getTests() {
        return tests;
    }

    public TestSet addTest(TestCase testCase) {
        this.tests.add(testCase);
        return this;
    }
}
