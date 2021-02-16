grammar Ltl;

formulae
    : '(' formulae ')'                   # parentheses
    | '!' formulae                       # negation
    | 'X' formulae                       # next
    | 'F' formulae                       # future
    | 'G' formulae                       # globally
    | lhs=formulae 'U' rhs=formulae      # until
    | lhs=formulae 'R' rhs=formulae      # release
    | lhs=formulae '&&' rhs=formulae     # conjunction
    | lhs=formulae '||' rhs=formulae     # disjunction
    | lhs=formulae '->' rhs=formulae     # implication
    | ID                                 # variable
    | BOOLEAN                            # boolean
    ;

BOOLEAN : 'true' | 'false';
NUM : [0-9]+;
ID : [a-zA-Z][_a-zA-Z0-9]*;
WS : [ \t\r\n]+ -> skip;
