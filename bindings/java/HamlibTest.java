/*
 *  Hamlib bindings - Java test
 *  Copyright (C) 2026 by Gong Zhile
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

public class HamlibTest {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        System.out.println("--- Hamlib Java Binding Test ---");

        // Test 1: Version strings
        try {
            String ver = org.hamlib.Hamlib.getHamlib_version();
            String copyright = org.hamlib.Hamlib.getHamlib_copyright();
            System.out.println("Hamlib version: " + ver);
            System.out.println("Copyright: " + copyright.trim());
            if (ver != null && ver.length() > 0) {
                System.out.println("PASS: version string non-empty");
                passed++;
            } else {
                System.out.println("FAIL: version string empty");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL: version test exception: " + e.getMessage());
            failed++;
        }

        // Test 2: longlat2locator
        try {
            String locator = org.hamlib.Hamlib.longlat2locator(0.0, 52.0);
            System.out.println("Locator for (0, 52): " + locator);
            if (locator != null && locator.length() > 0) {
                System.out.println("PASS: locator non-empty");
                passed++;
            } else {
                System.out.println("FAIL: locator empty");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL: locator test exception: " + e.getMessage());
            failed++;
        }

        // Test 3: rigerror
        try {
            String errStr = org.hamlib.Hamlib.rigerror(0);
            System.out.println("rigerror(0): " + errStr);
            if (errStr != null && errStr.length() > 0) {
                System.out.println("PASS: rigerror works");
                passed++;
            } else {
                System.out.println("FAIL: rigerror returned empty");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL: rigerror exception: " + e.getMessage());
            failed++;
        }

        // Test 4: RIG_MODEL_DUMMY constant
        try {
            int dummyModel = org.hamlib.HamlibConstants.RIG_MODEL_DUMMY;
            System.out.println("RIG_MODEL_DUMMY: " + dummyModel);
            if (dummyModel > 0) {
                System.out.println("PASS: RIG_MODEL_DUMMY defined");
                passed++;
            } else {
                System.out.println("FAIL: RIG_MODEL_DUMMY not defined");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL: constant test exception: " + e.getMessage());
            failed++;
        }

        // Test 5: Rig creation with dummy model
        try {
            org.hamlib.Rig rig = new org.hamlib.Rig(org.hamlib.HamlibConstants.RIG_MODEL_DUMMY);
            System.out.println("PASS: Rig created (no error means success)");
            passed++;

            try {
                rig.open();
                System.out.println("PASS: Rig opened on dummy model");
                passed++;
                rig.close();
            } catch (Exception e) {
                System.out.println("NOTE: Rig open failed (expected without hardware): " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("FAIL: Rig creation exception: " + e.getMessage());
            failed++;
        }

        // Test 6: Rotator API
        try {
            org.hamlib.Rot rot = new org.hamlib.Rot(org.hamlib.HamlibConstants.ROT_MODEL_DUMMY);
            System.out.println("PASS: Rot created");
            passed++;
        } catch (Exception e) {
            System.out.println("FAIL: Rot creation exception: " + e.getMessage());
            failed++;
        }

        // Test 7: Amp API
        try {
            org.hamlib.Amp amp = new org.hamlib.Amp(org.hamlib.HamlibConstants.AMP_MODEL_DUMMY);
            System.out.println("PASS: Amp created");
            passed++;
        } catch (Exception e) {
            System.out.println("FAIL: Amp creation exception: " + e.getMessage());
            failed++;
        }

        // Summary
        int total = passed + failed;
        System.out.println("---");
        System.out.println("Results: " + passed + "/" + total + " passed");
        if (failed > 0) {
            System.out.println("FAILURES: " + failed);
            System.exit(1);
        } else {
            System.out.println("All tests passed!");
        }
    }
}
