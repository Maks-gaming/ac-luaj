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
package org.luaj.vm2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.luajc.LuaJC;

/**
 * Compatibility tests for the Luaj VM
 * <p>
 * Results are compared for exact match with
 * the installed C-based lua environment.
 */
public class CompatibiltyTest {

    private static final String dir = "";

    abstract protected static class CompatibiltyTestSuite extends ScriptDrivenTest {
        LuaValue savedStringMetatable;

        protected CompatibiltyTestSuite(PlatformType platform) {
            super(platform, dir);
        }

        @BeforeEach
        protected void setUp() throws Exception {
            savedStringMetatable = LuaString.s_metatable;
            super.setUp();
        }

        @AfterEach
        protected void tearDown() throws Exception {
            LuaNil.s_metatable = null;
            LuaBoolean.s_metatable = null;
            LuaNumber.s_metatable = null;
            LuaFunction.s_metatable = null;
            LuaThread.s_metatable = null;
            LuaString.s_metatable = savedStringMetatable;
        }

        @Test
        public void testBaseLib() {runTest("baselib");}

        @Test
        public void testCoroutineLib() {runTest("coroutinelib");}

        @Test
        public void testDebugLib() {runTest("debuglib");}

        @Test
        public void testErrors() {runTest("errors");}

        @Test
        public void testFunctions() {runTest("functions");}

        @Test
        public void testIoLib() {runTest("iolib");}

        @Test
        public void testManyUpvals() {runTest("manyupvals");}

        @Test
        public void testMathLib() {runTest("mathlib");}

        @Test
        public void testMetatags() {runTest("metatags");}

        @Test
        public void testOsLib() {runTest("oslib");}

        @Test
        public void testStringLib() {runTest("stringlib");}

        @Test
        public void testTableLib() {runTest("tablelib");}

        @Test
        public void testTailcalls() {runTest("tailcalls");}

        @Test
        public void testUpvalues() {runTest("upvalues");}

        @Test
        public void testVm() {runTest("vm");}
    }


    public static class JseCompatibilityTest extends CompatibiltyTestSuite {
        public JseCompatibilityTest() {
            super(ScriptDrivenTest.PlatformType.JSE);
        }

        @BeforeEach
        protected void setUp() throws Exception {
            super.setUp();
            System.setProperty("JME", "false");
        }
    }

    public static class LuaJCCompatibilityTest extends CompatibiltyTestSuite {
        public LuaJCCompatibilityTest() {
            super(ScriptDrivenTest.PlatformType.LUAJIT);
        }

        @BeforeEach
        protected void setUp() throws Exception {
            super.setUp();
            System.setProperty("JME", "false");
            LuaJC.install(globals);
        }

        // not supported on this platform - don't test
        @Test
        public void testDebugLib() {}
    }
}
