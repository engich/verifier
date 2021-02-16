import automata.Automata;
import automata.BA;
import automata.GBA;
import ltl.formulae.LtlFormulae;
import ltl.formulae.Not;
import ltl.formulae.visitor.LtlFormulaeVisitor;
import ltl.gen.LtlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.xml.sax.SAXException;
import processing.Answer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
    private static final String DELIMETER = "------------------------------";

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        if (args.length < 2 || args.length > 3) {
            showUsage();
            return;
        }

        final PrintStream printStream = args.length < 3 ? System.out : new PrintStream(new File(args[2]));
        final BA ba = BA.build(Automata.fromFile(args[0]));
        final LtlFormulae ltlFormulae = new Not(new LtlFormulaeVisitor().visit(new LtlParser(new CommonTokenStream(new LtlLexer(CharStreams.fromString(args[1])))).expression()));
        final GBA gba = GBA.build(ltlFormulae, ba.getAtomicPropositions());
        final BA BAByLtl = BA.build(gba);
        final Answer answer = Answer.build(ba, BAByLtl);

        if (!answer.getHolds()) {
            printStream.println(args[1] + " - not holds");
            printStream.println(DELIMETER + "\n" + "In:");

            printCePath(printStream, answer);

            printStream.println(DELIMETER + "\n" + "-> " + answer.getInd() + ". - cycle");
        } else {
            printStream.println(args[1] + " - holds");
        }
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar verifier.jar <file with automaton> <ltl formula> [<output file>]");
    }

    private static void printCePath(final PrintStream printStream, final Answer answer) {
        for (int i = 0; i < answer.getCePath().size(); ++i) {
            printStream.println(i + ". " + answer.getCePath().get(i).getL());
        }
    }
}
