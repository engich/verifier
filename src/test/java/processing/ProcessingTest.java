package processing;

import automata.Automata;
import automata.BA;
import automata.GBA;
import ltl.formulae.LtlFormulae;
import ltl.formulae.Not;
import ltl.formulae.Sym;
import ltl.formulae.Var;
import ltl.formulae.visitor.LtlFormulaeVisitor;
import ltl.gen.LtlLexer;
import ltl.gen.LtlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class ProcessingTest {
    private static final String TEST_AUTOMATA_FILE_PATH = "src/test/resources/test1.xml";
    private static final String TEST_LTL_FORMULAE_1 = "G (F POWER_ON)";
    private static final String TEST_LTL_FORMULAE_2 = "G (PRESTART -> (PRESTART U POWER_ON))";

    @Test
    public void test1() throws Exception {
        final Answer answer = getResult(TEST_AUTOMATA_FILE_PATH, TEST_LTL_FORMULAE_1);

        Assert.assertFalse(answer.getHolds());
        Assert.assertEquals(answer.getCePath().size(), 23);

        Assert.assertEquals(answer.getCePath().get(0), new Sym(Collections.singleton(new Var("Start")), Collections.singleton(new Var("Start"))));
        Assert.assertEquals(answer.getCePath().get(4), new Sym(Collections.singleton(new Var("PRESTART")), Collections.singleton(new Var("PRESTART"))));
        Assert.assertEquals(answer.getCePath().get(12), new Sym(Collections.singleton(new Var("POWER_ON")), Collections.singleton(new Var("POWER_ON"))));
        Assert.assertEquals(answer.getCePath().get(14), new Sym(Collections.singleton(new Var("CPU_ON")), Collections.singleton(new Var("CPU_ON"))));
        Assert.assertEquals(answer.getCePath().get(16), new Sym(Collections.singleton(new Var("BAT_ONLY")), Collections.singleton(new Var("BAT_ONLY"))));
        Assert.assertEquals(answer.getCePath().get(18), new Sym(Collections.singleton(new Var("CPU_ON")), Collections.singleton(new Var("CPU_ON"))));
        Assert.assertEquals(answer.getCePath().get(20), new Sym(Collections.singleton(new Var("BAT_ONLY")), Collections.singleton(new Var("BAT_ONLY"))));
        Assert.assertEquals(answer.getCePath().get(22), new Sym(Collections.singleton(new Var("CPU_ON")), Collections.singleton(new Var("CPU_ON"))));
    }

    @Test
    public void test2() throws Exception {
        final Answer answer = getResult(TEST_AUTOMATA_FILE_PATH, TEST_LTL_FORMULAE_2);

        Assert.assertTrue(answer.getHolds());
        Assert.assertNull(answer.getCePath());
    }

    private Answer getResult(final String filePath, final String formulae) throws Exception {
        final BA ba = BA.build(Automata.fromFile(filePath));
        final LtlFormulae ltlFormulae = new Not(new LtlFormulaeVisitor().visit(new LtlParser(new CommonTokenStream(new LtlLexer(CharStreams.fromString(formulae)))).expression()));
        final GBA generalizedBuchiAutomatonByLtl = GBA.build(ltlFormulae, ba.getAtomicPropositions());
        final BA BAByLtl = BA.build(generalizedBuchiAutomatonByLtl);
        final Answer result = Answer.build(ba, BAByLtl);

        return result;
    }
}
