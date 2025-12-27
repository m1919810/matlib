package me.matl114.matlib.algorithms.dataStructures.complex;

import static me.matl114.matlib.algorithms.algorithm.TransformationUtils.*;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import me.matl114.matlib.utils.Debug;

public class MatrixStack {
    public MatrixStack() {
        stack.addLast(LCTransformation.ofIdentical());
    }

    public MatrixStack(LCTransformation initialTrans) {
        stack.addLast(initialTrans);
    }

    private final Deque<LCTransformation> stack = new ArrayDeque<>();

    public void push() {
        stack.addLast(stack.peekLast());
    }

    public void pop() {
        Preconditions.checkState(
                stack.size() > 1, "Illegal State to MatrixStack! Pop action before you push!(async access?)");
        stack.removeLast();
    }

    public void composition(LCTransformation nextTrans) {
        Preconditions.checkState(!stack.isEmpty(), "Illegal Access to MatrixStack! Transformation stack is empty");
        stack.addLast(stack.removeLast().compositionWith(nextTrans));
        Debug.logger(stack.size());
    }

    public LCTransformation peek() {
        Preconditions.checkState(!stack.isEmpty(), "Illegal Access to MatrixStack! Transformation stack is empty");
        return stack.peekLast();
    }
}
