package org.luaj.vm2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.require.RequireSampleClassCastExcep;
import org.luaj.vm2.require.RequireSampleLoadLuaError;
import org.luaj.vm2.require.RequireSampleLoadRuntimeExcep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RequireClassTest {

    private LuaTable globals;
    private LuaValue require;

    @BeforeEach
    public void setUp() {
        globals = JsePlatform.standardGlobals();
        require = globals.get("require");
    }

    @Test
    public void testLoadClass() {
        LuaValue result = globals.load(new org.luaj.vm2.require.RequireSampleSuccess());
        assertEquals("require-sample-success-", result.tojstring());
    }

    @Test
    public void testRequireClassSuccess() {
        LuaValue result = require.call(LuaValue.valueOf("org.luaj.vm2.require.RequireSampleSuccess"));
        assertEquals("require-sample-success-org.luaj.vm2.require.RequireSampleSuccess", result.tojstring());
        result = require.call(LuaValue.valueOf("org.luaj.vm2.require.RequireSampleSuccess"));
        assertEquals("require-sample-success-org.luaj.vm2.require.RequireSampleSuccess", result.tojstring());
    }

    @Test
    public void testRequireClassLoadLuaError() {
        try {
            LuaValue result = require.call(LuaValue.valueOf(RequireSampleLoadLuaError.class.getName()));
            fail("incorrectly loaded class that threw lua error");
        } catch (LuaError le) {
            assertEquals(
                "sample-load-lua-error",
                le.getMessage());
        }
        try {
            LuaValue result = require.call(LuaValue.valueOf(RequireSampleLoadLuaError.class.getName()));
            fail("incorrectly loaded class that threw lua error");
        } catch (LuaError le) {
            assertEquals(
                "loop or previous error loading module '" + RequireSampleLoadLuaError.class.getName() + "'",
                le.getMessage());
        }
    }

    @Test
    public void testRequireClassLoadRuntimeException() {
        try {
            LuaValue result = require.call(LuaValue.valueOf(RequireSampleLoadRuntimeExcep.class.getName()));
            fail("incorrectly loaded class that threw runtime exception");
        } catch (RuntimeException le) {
            assertEquals(
                "sample-load-runtime-exception",
                le.getMessage());
        }
        try {
            LuaValue result = require.call(LuaValue.valueOf(RequireSampleLoadRuntimeExcep.class.getName()));
            fail("incorrectly loaded class that threw runtime exception");
        } catch (LuaError le) {
            assertEquals(
                "loop or previous error loading module '" + RequireSampleLoadRuntimeExcep.class.getName() + "'",
                le.getMessage());
        }
    }


    @Test
    public void testRequireClassClassCastException() {
        try {
            LuaValue result = require.call(LuaValue.valueOf(RequireSampleClassCastExcep.class.getName()));
            fail("incorrectly loaded class that threw class cast exception");
        } catch (LuaError le) {
            String msg = le.getMessage();
            if (msg.indexOf("not found") < 0)
                fail("expected 'not found' message but got " + msg);
        }
        try {
            LuaValue result = require.call(LuaValue.valueOf(RequireSampleClassCastExcep.class.getName()));
            fail("incorrectly loaded class that threw class cast exception");
        } catch (LuaError le) {
            String msg = le.getMessage();
            if (msg.indexOf("not found") < 0)
                fail("expected 'not found' message but got " + msg);
        }
    }
}
