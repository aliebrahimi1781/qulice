/**
 * Copyright (c) 2011-2014, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Pattern;

/**
 * Checks that non static method must contain at least one reference to
 * {@code this}.
 *
 * <p>If your method doesn't need {@code this} than why it is not
 * {@code static}?
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 */
public final class NonStaticMethodCheck extends Check {

    /**
     * Files to exclude from this check.
     * This is mostly to exclude JUnit tests.
     */
    private transient Pattern exclude = Pattern.compile("^$");

    /**
     * Exclude files matching given pattern.
     * @param excl Regexp of classes to exclude.
     */
    public void setExcludeFileNamePattern(final String excl) {
        this.exclude = Pattern.compile(excl);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
        };
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (this.exclude.matcher(this.getFileContents().getFilename())
            .find()) {
            return;
        }
        if (TokenTypes.CLASS_DEF == ast.getParent().getParent().getType()) {
            this.checkClassMethod(ast);
        }
    }

    /**
     * Check that non static class method refer \"this\".
     * @param method DetailAST of method
     */
    private void checkClassMethod(final DetailAST method) {
        final DetailAST modifiers = method
            .findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null) {
            return;
        }
        if (!method.branchContains(TokenTypes.LITERAL_THIS)) {
            final int line = method.getLineNo();
            this.log(
                line,
                // @checkstyle LineLength (1 line)
                "This method must be static, because it does not refer to \"this\""
            );
        }
    }
}
