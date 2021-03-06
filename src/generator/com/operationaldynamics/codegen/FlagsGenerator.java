/*
 * java-gnome, a UI library for writing GTK and GNOME programs from Java!
 *
 * Copyright © 2007-2010 Operational Dynamics Consulting, Pty Ltd
 * Copyright © 2007 Vreixo Formoso
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://java-gnome.sourceforge.net/.
 */
package com.operationaldynamics.codegen;

import java.io.PrintWriter;

import com.operationaldynamics.defsparser.FlagsBlock;
import com.operationaldynamics.driver.DefsFile;

/**
 * Output the file header necessary to declare the class containing the
 * constant objects of our representation of C enums via subclasses of
 * Constant. See {@link FlagsBlock} for an example of a (define-flags ...)
 * stanza.
 * 
 * @author Vreixo Formoso
 * @author Andrew Cowie
 */
public class FlagsGenerator extends EnumGenerator
{
    private String[][] values;

    /**
     * 
     * @param data
     *            a reference back to the DefsFile from which we will pull the
     *            gTypeName (as found in the defs files) of the enum that you
     *            are generating bindings code for.
     * @param values
     *            The different values enum could take.
     */
    public FlagsGenerator(DefsFile data, String[][] values) {
        super(data, values);

        this.values = new String[values.length][2];
        for (int i = 0; i < values.length; ++i) {
            this.values[i][0] = values[i][0].replace('-', '_');
            this.values[i][1] = values[i][1];
        }
    }

    protected void writeTranslationValues(PrintWriter out) {
        for (int i = 0; i < values.length; ++i) {

            /*
             * Write the static field
             */

            out.print("\n");
            out.print("    ");
            out.print("static final int ");
            out.print(toAllCaps(values[i][0]));
            out.print(" = get_ordinal_");
            out.print(values[i][0]);
            out.print("();\n");

            /*
             * And now the native method declaration
             */

            out.print("\n");
            out.print("    ");
            out.print("private static native final int get_ordinal_");
            out.print(values[i][0]);
            out.print("();\n");
        }
    }

    public void writeJniCode(PrintWriter out) {
        /*
         * write file header and includes care of the code up in abstract
         * parent class TypeGenerator
         */
        commonFileHeader(out, objectType.bindingsClass + ".c");
        hashIncludeStatements(out);

        /*
         * and now the methods for get the values
         */

        for (int i = 0; i < values.length; ++i) {

            out.print("\n");
            out.print("JNIEXPORT jint JNICALL\n");

            out.print("Java_");
            out.print(encodeJavaClassName(objectType.bindingsPackage, objectType.bindingsClass));
            out.print("_get_1ordinal_1");
            out.print(encodeJavaMethodName(values[i][0]));
            out.print("\n(\n\tJNIEnv* env,\n\tjclass cls\n)\n");
            out.print("{\n");
            out.print("\treturn (jint) ");
            out.print(values[i][1]);
            out.print(";\n");
            out.print("}\n");
        }
    }
}
