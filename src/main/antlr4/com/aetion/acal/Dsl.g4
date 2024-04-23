grammar Dsl;
import  Symbols;

@header {
import java.util.*;
// efv was here!!
}

@parser::members {
	int idRefCount = 1;
	void countIdRef(String s) {
		System.out.println("id reference number " + (idRefCount++) + " : " + s);
	}
}

prog: stat* EOF;

// statement non-terminal rule
stat	:	block			     	    # blockStmt	// creates BlockStmtContext, which contains BlockContext
			| expr SEMI 					# printExpr
			| ID '=' expr SEMI    # assign
			| 'clear' SEMI        # clear
			| SEMI 	 							# blank;

block: '{' stat* '}';							// Always creates a Context - BlockContext

// expression non-terminal rule
expr  : <assoc = right> expr '^' expr		# exp 
			| expr op=(MUL | DIV) expr  			# mulDiv
    	| expr op=(ADD | SUB) expr 	 			# addSub
			| INT	{if ($INT.int <= 0) notifyErrorListeners("values must be > 0"); }	# int
			| ID	{ countIdRef($ID.text); }	  # id
			| '(' expr ')'										# parens;
