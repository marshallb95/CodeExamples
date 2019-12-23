#pragma once
#include "scanner.h"
#include "Predicate.h"
#include "Parameter.h"
#include "DataLogProgram.h"
#include "Rule.h"

#include <stack>

using namespace std;

class Parser {
	private:
		vector<Token> compTokens;
		DataLogProgram myDataLog;
	public:
		Parser(vector<Token> scannedTokens);
		void match(string curToken);
		void parse();
		void DataLogParse();
		void datalogProgram();
		void schemeList();
		void factList();
		void ruleList();
		void queryList();
		void scheme();
		void fact();
		void rule();
		void query();
		void headPredicate(Rule &myRule);
		Predicate predicate();
		void predicateList(Rule &myRule);
		void parameterList(Predicate &pred);
		void stringList(Predicate &factPred);
		void  idList(Predicate &myPred);
		void parameter(Parameter &param);
		void expression(Parameter &param);
		void parse_operator(Parameter &param);
		DataLogProgram getDataLog();
};
