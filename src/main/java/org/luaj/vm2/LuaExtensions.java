package org.luaj.vm2;

import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.function.*;

public class LuaExtensions {
    public static LuaFunction function(final Runnable func) {
        return function(() -> {
            func.run();
            return LuaValue.NIL;
        });
    }

    public static LuaFunction function(final Supplier<Varargs> func) {
        return varArgFunction(args -> func.get());
    }

    public static LuaFunction function(final Consumer<LuaValue> func) {
        return function(arg -> {
            func.accept(arg);
            return LuaValue.NIL;
        });
    }

    public static LuaFunction function(final Function<LuaValue, Varargs> func) {
        return varArgFunction(arg -> func.apply(arg.arg(1)));
    }

    public static LuaFunction function(final BiConsumer<LuaValue, LuaValue> func) {
        return function((arg1, arg2) -> {
            func.accept(arg1, arg2);
            return LuaValue.NIL;
        });
    }

    public static LuaFunction function(final BiFunction<LuaValue, LuaValue, Varargs> func) {
        return varArgFunction(args -> func.apply(args.arg(1), args.arg(2)));
    }

    public static LuaFunction function(final TriConsumer<LuaValue, LuaValue, LuaValue> func) {
        return function((arg1, arg2, arg3) -> {
            func.accept(arg1, arg2, arg3);
            return LuaValue.NIL;
        });
    }

    public static LuaFunction function(final TriFunction<LuaValue, LuaValue, LuaValue, Varargs> func) {
        return varArgFunction(args -> func.apply(args.arg(1), args.arg(2), args.arg(3)));
    }

    public static LuaFunction function(final QuadConsumer<LuaValue, LuaValue, LuaValue, LuaValue> func) {
        return function((arg1, arg2, arg3, arg4) -> {
            func.accept(arg1, arg2, arg3, arg4);
            return LuaValue.NIL;
        });
    }

    public static LuaFunction function(final QuadFunction<LuaValue, LuaValue, LuaValue, LuaValue, Varargs> func) {
        return varArgFunction(args -> func.apply(args.arg(1), args.arg(2), args.arg(3), args.arg(4)));
    }

    public static LuaFunction function(final PentaConsumer<LuaValue, LuaValue, LuaValue, LuaValue, LuaValue> func) {
        return function((arg1, arg2, arg3, arg4, arg5) -> {
            func.accept(arg1, arg2, arg3, arg4, arg5);
            return LuaValue.NIL;
        });
    }

    public static LuaFunction function(final PentaFunction<LuaValue, LuaValue, LuaValue, LuaValue, LuaValue, Varargs> func) {
        return varArgFunction(args -> func.apply(args.arg(1), args.arg(2), args.arg(3), args.arg(4), args.arg(5)));
    }

    public static LuaFunction varArgFunction(final Function<Varargs, Varargs> func) {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(final Varargs args) {
                return func.apply(args);
            }
        };
    }


    @FunctionalInterface
    public interface QuadFunction<P1, P2, P3, P4, R> {
        R apply(P1 object, P2 object2, P3 object3, P4 object4);
    }

    @FunctionalInterface
    public interface QuadConsumer<P1, P2, P3, P4> {
        void accept(P1 object, P2 object2, P3 object3, P4 object4);
    }


    @FunctionalInterface
    public interface PentaFunction<P1, P2, P3, P4, P5, R> {
        R apply(P1 object, P2 object2, P3 object3, P4 object4, P5 object5);
    }

    @FunctionalInterface
    public interface PentaConsumer<P1, P2, P3, P4, P5> {
        void accept(P1 object, P2 object2, P3 object3, P4 object4, P5 object5);
    }
}
