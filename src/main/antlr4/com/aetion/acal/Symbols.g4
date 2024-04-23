lexer grammar Symbols;

/*---------------------------------------------------
	Terminal rules
---------------------------------------------------*/
CARET: '^';
SEMI: ';';
MUL: '*'; // assigns token name to '*' used above in grammar
DIV: '/';
ADD: '+';
SUB: '-';
ID: [a-zA-Z]+; // match identifiers
INT: [0-9]+; // match integers
WS: [ \t\r\n]+ -> skip; // toss out whitespace