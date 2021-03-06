/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.compile.stage1.spec;

import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.context.aifactory.core.SAIFFInitializeSymbol;
import com.espertech.esper.common.internal.context.controller.initterm.ContextControllerDetailInitiatedTerminated;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.epl.expression.core.ExprNodeUtilityCodegen;
import com.espertech.esper.common.internal.epl.expression.core.ExprNodeUtilityQuery;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

public class ContextSpecInitiatedTerminated implements ContextSpec {

    private ContextSpecCondition startCondition;
    private ContextSpecCondition endCondition;
    private boolean overlapping;
    private ExprNode[] distinctExpressions;

    public ContextSpecInitiatedTerminated(ContextSpecCondition startCondition, ContextSpecCondition endCondition, boolean overlapping, ExprNode[] distinctExpressions) {
        this.startCondition = startCondition;
        this.endCondition = endCondition;
        this.overlapping = overlapping;
        this.distinctExpressions = distinctExpressions;
    }

    public ContextSpecCondition getStartCondition() {
        return startCondition;
    }

    public ContextSpecCondition getEndCondition() {
        return endCondition;
    }

    public void setStartCondition(ContextSpecCondition startCondition) {
        this.startCondition = startCondition;
    }

    public void setEndCondition(ContextSpecCondition endCondition) {
        this.endCondition = endCondition;
    }

    public boolean isOverlapping() {
        return overlapping;
    }

    public ExprNode[] getDistinctExpressions() {
        return distinctExpressions;
    }

    public CodegenExpression makeCodegen(CodegenMethodScope parent, SAIFFInitializeSymbol symbols, CodegenClassScope classScope) {
        CodegenMethod method = parent.makeChild(ContextControllerDetailInitiatedTerminated.class, this.getClass(), classScope);

        method.getBlock()
                .declareVar(ContextControllerDetailInitiatedTerminated.class, "detail", newInstance(ContextControllerDetailInitiatedTerminated.class))
                .exprDotMethod(ref("detail"), "setStartCondition", startCondition.make(method, symbols, classScope))
                .exprDotMethod(ref("detail"), "setEndCondition", endCondition.make(method, symbols, classScope))
                .exprDotMethod(ref("detail"), "setOverlapping", constant(overlapping));
        if (distinctExpressions != null && distinctExpressions.length > 0) {
            method.getBlock()
                    .exprDotMethod(ref("detail"), "setDistinctEval", ExprNodeUtilityCodegen.codegenEvaluatorMayMultiKeyWCoerce(ExprNodeUtilityQuery.getForges(distinctExpressions), null, method, this.getClass(), classScope))
                    .exprDotMethod(ref("detail"), "setDistinctTypes", constant(ExprNodeUtilityQuery.getExprResultTypes(distinctExpressions)));
        }
        method.getBlock().methodReturn(ref("detail"));
        return localMethod(method);
    }
}
