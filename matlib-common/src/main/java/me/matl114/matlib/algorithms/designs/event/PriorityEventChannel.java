package me.matl114.matlib.algorithms.designs.event;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import me.matl114.matlib.utils.reflect.ReflectUtils;

public class PriorityEventChannel<T extends Event> {
    protected final Node<T> headNode;

    @Getter
    protected final Logger logger;

    public PriorityEventChannel(String name) {
        headNode = new Node<>(null);
        logger = Logger.getLogger(name);
    }

    public PriorityEventChannel() {
        this("EventChannel");
    }

    public <R> void registerHandler(PriorityEventHandler<R, T> handler) {
        Preconditions.checkNotNull(handler);
        Node<T> node = new Node<>(handler);
        Node<T> prev = headNode;

        int p = handler.priority;
        while ((prev = prev.casInsertNew(node, p)) != null) {}
    }

    public <R> void unregisterAll(R owner) {
        unregisterAll(o -> o == owner);
    }

    public <R> void unregisterAll(Predicate<R> ownerPredicate) {
        Node<T> prev = headNode;
        while ((prev = prev.casRemove(ownerPredicate)) != null) {}
    }

    public boolean isEmpty() {
        return headNode.next == null;
    }

    public void dispatch(T event) {
        Preconditions.checkNotNull(event);
        Node<T> prev = headNode;
        Node<T> iter;
        while ((iter = prev.next) != null) {
            PriorityEventHandler<?, T> handler = iter.handler;
            if (!handler.ignoreIfCancel || !event.isCancelled()) {
                try {
                    handler.task.accept(event);
                } catch (Throwable e) {
                    logger.log(
                            Level.SEVERE,
                            "Could not pass event " + event.getClass().getSimpleName() + " to the handler owned by "
                                    + handler.owner + " due to the exception:",
                            e);
                }
            }
            prev = iter;
        }
    }

    protected static class Node<T extends Event> {
        volatile Node<T> next;
        final PriorityEventHandler<?, T> handler;
        //        static final AtomicReferenceFieldUpdater<Node, Node> NEXT_UPDATOR =
        // AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
        public static final VarHandle NEXT_UPDATOR = Objects.requireNonNull(
                        ReflectUtils.getVarHandlePrivate(Node.class, "next"))
                .withInvokeExactBehavior();

        protected Node(PriorityEventHandler<?, T> handler) {
            this.handler = handler;
        }

        protected Node<T> casInsertNew(Node<T> newNode, int p) {
            Node<T> oldNext;
            // 通过保护当前next的原子状态
            // 当next被更改的时候 重新处理
            // 当next被更改成比p大的时候,选取下一个
            do {
                oldNext = next;
                if (oldNext != null) {
                    if (oldNext.handler.priority <= p) {
                        return oldNext;
                    }
                    newNode.next = oldNext;
                } else {
                    newNode.next = null;
                }
            } while (!NEXT_UPDATOR.compareAndSet((Node) this, (Node) oldNext, (Node) newNode));
            return null;
        }

        protected <R> Node<T> casRemove(Predicate<R> owner) {
            Node oldNext;
            Node newNext;
            do {
                oldNext = next;
                if (oldNext != null) {
                    if (oldNext.handler != null && !owner.test((R) oldNext.handler.owner)) {
                        return oldNext;
                    }
                    // should remove next
                    newNext = oldNext.next;
                } else {
                    newNext = null;
                }
            } while (!NEXT_UPDATOR.compareAndSet((Node) this, (Node) oldNext, (Node) newNext));
            return newNext;
        }

        @Override
        public String toString() {
            return handler.toString();
        }
    }

    public String toString() {
        var sb = new StringBuffer("PriorityEventChannel{ handlers = [");
        Node<T> pr = headNode;
        while ((pr = pr.next) != null) {
            sb.append(pr.toString());
            sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
}
