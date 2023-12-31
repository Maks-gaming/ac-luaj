/*******************************************************************************
 * Copyright (c) 2009-2011 Luaj.org. All rights reserved.
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
package org.luaj.vm2.lib.jse;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaExtensions;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Subclass of {@link LibFunction} which implements the lua standard {@code math}
 * library.
 * <p>
 * It contains all lua math functions, including those not available on the JME platform.
 * See {@link org.luaj.vm2.lib.MathLib} for the exception list.
 * <p>
 * Typically, this library is included as part of a call to
 * {@link org.luaj.vm2.lib.jse.JsePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * <p>
 * For special cases where the smallest possible footprint is desired,
 * a minimal set of libraries could be loaded
 * directly via {@link Globals#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new JseMathLib());
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * <p>However, other libraries such as <em>CoroutineLib</em> are not loaded in this case.
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 *
 * @see LibFunction
 * @see org.luaj.vm2.lib.jse.JsePlatform
 * @see org.luaj.vm2.lib.jse.JseMathLib
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.6">Lua 5.2 Math Lib Reference</a>
 */
public final class JseMathLib extends org.luaj.vm2.lib.MathLib {
    /**
     * Perform one-time initialization on the library by creating a table
     * containing the library functions, adding that table to the supplied environment,
     * adding the table to package.loaded, and returning table as the return value.
     * <P>Specifically, adds all library functions that can be implemented directly
     * in JSE but not JME: acos, asin, atan, atan2, cosh, exp, log, pow, sinh, and tanh.
     *
     * @param modname the module name supplied if this is loaded via 'require'.
     * @param env     the environment to load into, which must be a Globals instance.
     */
    @Override
    public LuaValue call(final LuaValue modname, final LuaValue env) {
        super.call(modname, env);
        final var math = env.get("math");
        math.set("acos", op(Math::acos));
        math.set("asin", op(Math::asin));
        final LuaValue atan = op(Math::atan2);
        math.set("atan", atan);
        math.set("atan2", atan);
        math.set("cosh", op(Math::cosh));
        math.set("exp", op(Math::exp));
        math.set("log", LuaExtensions.function(this::log));
        math.set("pow", op(Math::pow));
        math.set("sinh", op(Math::sinh));
        math.set("tanh", op(Math::tanh));
        return math;
    }

    private LuaValue log(final LuaValue x, final LuaValue base) {
        var nat = Math.log(x.checkdouble());
        final var b = base.optdouble(Math.E);
        if (b != Math.E) nat /= Math.log(b);
        return valueOf(nat);
    }

    /**
     * Faster, better version of pow() used by arithmetic operator ^
     */
    @Override
    public double dpow_lib(final double a, final double b) {
        return Math.pow(a, b);
    }
}

