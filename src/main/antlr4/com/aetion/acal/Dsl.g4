grammar Dsl;
import  Symbols;

prog: stat+;

// statement non-terminal rule
stat	:	expr NEWLINE			  # printExpr
			| ID '=' expr NEWLINE	# assign
			| 'clear' NEWLINE			# clear
			| NEWLINE							# blank;

// expression non-terminal rule
expr	: expr op=(ADD | SUB) expr 	# addSub
			| expr op=(MUL | DIV) expr  # mulDiv
			| INT												# int
			| ID												# id
			| '(' expr ')'							# parens;
