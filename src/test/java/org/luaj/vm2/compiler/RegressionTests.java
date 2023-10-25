package org.luaj.vm2.compiler;

import org.junit.jupiter.api.Test;

/**
 * Framework to add regression tests as problem areas are found.
 * <p>
 * To add a new regression test:
 * 1) run "unpack.sh" in the project root
 * 2) add a new "lua" file in the "regressions" subdirectory
 * 3) run "repack.sh" in the project root
 * 4) add a line to the source file naming the new test
 * <p>
 * After adding a test, check in the zip file
 * rather than the individual regression test files.
 *
 * @author jrosebor
 */
public class RegressionTests extends AbstractUnitTests {

    public RegressionTests() {
        super("test/lua", "luaj3.0-tests.zip", "regressions");
    }

    @Test
    public void testModulo() {doTest("modulo.lua");}

    @Test
    public void testConstruct() {doTest("construct.lua");}

    @Test
    public void testBigAttrs() {doTest("bigattr.lua");}

    @Test
    public void testControlChars() {doTest("controlchars.lua");}

    @Test
    public void testComparators() {doTest("comparators.lua");}

    @Test
    public void testMathRandomseed() {doTest("mathrandomseed.lua");}

    @Test
    public void testVarargs() {doTest("varargs.lua");}
}
