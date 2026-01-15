package me.matl114.matlib.utils.crafting.recipe;

public interface Recipe {
    IConsumer[] getInputs();

    IGenerator[] getOutputs();
}
