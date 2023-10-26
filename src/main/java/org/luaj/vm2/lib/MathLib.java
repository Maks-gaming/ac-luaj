/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.lib;

import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.*;

import java.util.Random;

/**
 * Subclass of {@link LibFunction} which implements the lua standard {@code math}
 * library.
 * <p>
 * It contains only the math library support that is possible on JME.
 * For a more complete implementation based on math functions specific to JSE
 * use {@link org.luaj.vm2.lib.jse.JseMathLib}.
 * In Particular the following math functions are <b>not</b> implemented by this library:
 * <ul>
 * <li>acos</li>
 * <li>asin</li>
 * <li>atan</li>
 * <li>cosh</li>
 * <li>log</li>
 * <li>sinh</li>
 * <li>tanh</li>
 * <li>atan2</li>
 * </ul>
 * <p>
 * The implementations of {@code exp()} and {@code pow()} are constructed by
 * hand for JME, so will be slower and less accurate than when executed on the JSE platform.
 * <p>
 * Typically, this library is included as part of a call to either
 * {@link org.luaj.vm2.lib.jse.JsePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * When using {@link org.luaj.vm2.lib.jse.JsePlatform} as in this example,
 * the subclass {@link org.luaj.vm2.lib.jse.JseMathLib} will
 * be included, which also includes this base functionality.
 * <p>
 * To instantiate and use it directly,
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new MathLib());
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * Doing so will ensure the library is properly initialized
 * and loaded into the globals table.
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 *
 * @see LibFunction
 * @see org.luaj.vm2.lib.jse.JsePlatform
 * @see org.luaj.vm2.lib.jse.JseMathLib
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.6">Lua 5.2 Math Lib Reference</a>
 */
public class MathLib extends TwoArgFunction {
    /**
     * Pointer to the latest MathLib instance, used only to dispatch
     * math.exp to tha correct platform math library.
     */
    private static @Nullable MathLib MATHLIB = null;

    /**
     * Construct a MathLib, which can be initialized by calling it with a
     * modname string, and a global environment table as arguments using
     * {@link #call(LuaValue, LuaValue)}.
     */
    public MathLib() {
        MATHLIB = this;
    }

    /**
     * Perform one-time initialization on the library by creating a table
     * containing the library functions, adding that table to the supplied environment,
     * adding the table to package.loaded, and returning table as the return value.
     *
     * @param modname the module name supplied if this is loaded via 'require'.
     * @param env     the environment to load into, typically a Globals instance.
     */
    @Override
    public LuaValue call(final LuaValue modname, final LuaValue env) {
        final var math = new LuaTable(0, 30);
        math.set("abs", unaryOp(Math::abs));
        math.set("ceil", unaryOp(Math::ceil));
        math.set("cos", unaryOp(Math::cos));
        math.set("deg", unaryOp(Math::toDegrees));
        math.set("exp", unaryOp(d -> dpow_lib(Math.E, d)));
        math.set("floor", unaryOp(Math::floor));
        math.set("fmod", LuaExtensions.function(this::fmod));
        math.set("frexp", LuaExtensions.function(this::frexp));
        math.set("huge", LuaDouble.POSINF);
        math.set("ldexp", binaryOp((x, y) -> x * Double.longBitsToDouble((((long) y) + 1023) << 52)));
        math.set("max", LuaExtensions.function(this::max));
        math.set("min", LuaExtensions.function(this::min));
        math.set("modf", LuaExtensions.function(this::modf));
        math.set("pi", Math.PI);
        math.set("pow", binaryOp(MathLib::dpow_default));
        final random r;
        math.set("random", r = new random());
        math.set("randomseed", new randomseed(r));
        math.set("rad", unaryOp(Math::toRadians));
        math.set("sin", unaryOp(Math::sin));
        math.set("sqrt", unaryOp(Math::sqrt));
        math.set("tan", unaryOp(Math::tan));
        env.set("math", math);
        if (!env.get("package").isnil()) env.get("package").get("loaded").set("math", math);
        return math;
    }

    @FunctionalInterface
    public interface UnaryOperator {
        double call(double d);
    }
    public final LuaFunction unaryOp(final UnaryOperator op) {
        return LuaExtensions.function(arg -> {
            return valueOf(op.call(arg.checkdouble()));
        });
    }

    @FunctionalInterface
    public interface BinaryOperator {
        double call(double x, double y);
    }
    public final LuaFunction binaryOp(final BinaryOperator op) {
        return LuaExtensions.function((argx, argy) -> {
            return valueOf(op.call(argx.checkdouble(), argy.checkdouble()));
        });
    }

    private LuaValue fmod(final LuaValue xv, final LuaValue yv) {
        if (xv.islong() && yv.islong())
            return valueOf(xv.tolong() % yv.tolong());

        return valueOf(xv.checkdouble() % yv.checkdouble());
    }

    private Varargs frexp(final Varargs args) {
        final var x = args.checkdouble(1);
        if (x == 0) return varargsOf(ZERO, ZERO);
        final var bits = Double.doubleToLongBits(x);
        final var m = ((bits & (~(-1L << 52))) + (1L << 52)) * ((bits >= 0) ? (.5 / (1L << 52)) : (-.5 / (1L << 52)));
        final double e = (((int) (bits >> 52)) & 0x7ff) - 1022;
        return varargsOf(valueOf(m), valueOf(e));
    }

    private Varargs max(final Varargs args) {
        var m = args.checkvalue(1);
        for (int i = 2, n = args.narg(); i <= n; ++i) {
            final var v = args.checkvalue(i);
            if (m.lt_b(v)) m = v;
        }
        return m;
    }

    private Varargs min(final Varargs args) {
        var m = args.checkvalue(1);
        for (int i = 2, n = args.narg(); i <= n; ++i) {
            final var v = args.checkvalue(i);
            if (v.lt_b(m)) m = v;
        }
        return m;
    }

    private Varargs modf(final Varargs args) {
        final var n = args.arg1();
        /* number is its own integer part, no fractional part */
        if (n.islong()) return varargsOf(n, valueOf(0.0));
        final var x = n.checkdouble();
        /* integer part (rounds toward zero) */
        final var intPart = (x > 0) ? Math.floor(x) : Math.ceil(x);
        /* fractional part (test needed for inf/-inf) */
        final var fracPart = x == intPart ? 0.0 : x - intPart;
        return varargsOf(valueOf(intPart), valueOf(fracPart));
    }

    static class random extends LibFunction {
        Random random = new Random();

        @Override
        public LuaValue call() {
            return valueOf(random.nextDouble());
        }

        @Override
        public LuaValue call(final LuaValue a) {
            final var m = a.checkint();
            if (m < 1) argerror(1, "interval is empty");
            return valueOf(1 + random.nextInt(m));
        }

        @Override
        public LuaValue call(final LuaValue a, final LuaValue b) {
            final var m = a.checkint();
            final var n = b.checkint();
            if (n < m) argerror(2, "interval is empty");
            return valueOf(m + random.nextInt(n + 1 - m));
        }

    }

    static class randomseed extends OneArgFunction {
        final random random;

        randomseed(final random random) {
            this.random = random;
        }

        @Override
        public LuaValue call(final LuaValue arg) {
            final var seed = arg.checklong();
            random.random = new Random(seed);
            return NONE;
        }
    }

    /**
     * compute power using installed math library, or default if there is no math library installed
     */
    public static LuaValue dpow(final double a, final double b) {
        return LuaDouble.valueOf(
            MATHLIB != null ?
                MATHLIB.dpow_lib(a, b) :
                dpow_default(a, b));
    }

    public static double dpow_d(final double a, final double b) {
        return MATHLIB != null ?
            MATHLIB.dpow_lib(a, b) :
            dpow_default(a, b);
    }

    /**
     * Hook to override default dpow behavior with faster implementation.
     */
    public double dpow_lib(final double a, final double b) {
        return dpow_default(a, b);
    }

    /**
     * Default JME version computes using longhand heuristics.
     */
    protected static double dpow_default(double a, double b) {
        if (b < 0)
            return 1 / dpow_default(a, -b);
        double p = 1;
        var whole = (int) b;
        for (var v = a; whole > 0; whole >>= 1, v *= v)
            if ((whole & 1) != 0)
                p *= v;
        if ((b -= whole) > 0) {
            var frac = (int) (0x10000 * b);
            for (; (frac & 0xffff) != 0; frac <<= 1) {
                a = Math.sqrt(a);
                if ((frac & 0x8000) != 0)
                    p *= a;
            }
        }
        return p;
    }
}
